package com.example.task_tracker.service;

import com.example.task_tracker.entity.Task;
import com.example.task_tracker.entity.TaskStatus;
import com.example.task_tracker.repository.TaskRepository;
import com.example.task_tracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public Flux<Task> findAll() {
        return taskRepository.findAll()
                .flatMap(task -> {
                    Mono<Task> taskMono = getTaskMonoWithUserEntitiesData(task);
                    return taskMono;
                });
    }

    private Mono<Task> getTaskMonoWithUserEntitiesData(Task task) {
        Mono<Task> taskMonoWithAuthor = getTaskMonoWithAuthor(task);
        Mono<Task> taskMonoWithAssignee = getTaskMonoWithAssignee(task);
        Mono<Task> taskMonoWithObservers = getTaskMonoWithObservers(task);

        return Mono.zip(taskMonoWithAuthor, taskMonoWithAssignee, taskMonoWithObservers)
                .map(taskMono -> {
                    Task taskWithAuthor = taskMono.getT1();
                    Task taskWithAssignee = taskMono.getT2();
                    Task taskWithObservers = taskMono.getT3();

                    taskWithAuthor.setAssignee(taskWithAssignee.getAssignee());
                    taskWithAuthor.setObservers(taskWithObservers.getObservers());
                    return taskWithAuthor;
                });
    }

    private Mono<Task> getTaskMonoWithAuthor(Task task) {
        String authorId = task.getAuthorId();
        if (authorId == null || authorId.isBlank()) {
            return Mono.just(task);
        }
        return userRepository.findById(authorId)
                .map(user -> {
                    task.setAuthor(user);
                    return task;
                });
    }

    private Mono<Task> getTaskMonoWithAssignee(Task task) {
        String assigneeId = task.getAssigneeId();
        if (assigneeId == null || assigneeId.isBlank()) {
            return Mono.just(task);
        }
        return userRepository.findById(assigneeId)
                .map(user -> {
                    task.setAssignee(user);
                    return task;
                });
    }

    private Mono<Task> getTaskMonoWithObservers(Task task) {
        Set<String> observersIds = task.getObserversIds()
                .stream()
                .collect(Collectors.toUnmodifiableSet());
        if (observersIds.isEmpty()) {
            return Mono.just(task);
        }
        return userRepository.findAllById(observersIds)
                .collectList()
                .map(list -> {
                    list.forEach(task::addObserver);
                    return task;
                });
    }

    public Mono<Task> findById(String id) {
        return getTaskMonoWithUserEntitiesDataFromTaskMonoWithIds(taskRepository.findById(id));
    }

    private Mono<Task> getTaskMonoWithUserEntitiesDataFromTaskMonoWithIds(Mono<Task> taskMono) {
        return taskMono.flatMap(task -> {
            Mono<Task> taskMonoWithUserEntitiesData = getTaskMonoWithUserEntitiesData(task);
            return taskMonoWithUserEntitiesData;
        });
    }

    public Mono<Task> findByName(String name) {
        return getTaskMonoWithUserEntitiesDataFromTaskMonoWithIds(taskRepository.findByName(name));
    }

    public Mono<Task> save(Task task) {
        task.setId(UUID.randomUUID().toString());
        task.setCreatedUp(Instant.now());
        task.setUpdatedUp(task.getCreatedUp());
        return getSavedTaskInMono(task);
    }

    private Mono<Task> getSavedTaskInMono(Task task) {
        return taskRepository.save(task)
                .flatMap(taskEntity -> {
                    Mono<Task> taskMono = getTaskMonoWithUserEntitiesData(taskEntity);
                    return taskMono;
                });
    }

    public Mono<Task> update(String id, Task task) {
        return taskRepository.findById(id)
                .flatMap(taskForUpdate -> {
                    Task updatedTask = updateTaskFields(taskForUpdate, task);
                    return getTaskMonoWithUserEntitiesDataFromTaskMonoWithIds(taskRepository.save(updatedTask));
                });
    }

    private Task updateTaskFields(Task taskForUpdate, Task task) {
        String name = task.getName();
        String description = task.getDescription();
        String authorId = task.getAuthorId();
        String assigneeId = task.getAssigneeId();
        Set<String> observersIds = task.getObserversIds();
        TaskStatus status = task.getStatus();
        if (!name.isBlank()) {
            taskForUpdate.setName(name);
        }
        if (!description.isBlank()) {
            taskForUpdate.setDescription(description);
        }
        if (!authorId.isBlank()) {
            taskForUpdate.setAuthorId(authorId);
        }
        if (!assigneeId.isBlank()) {
            taskForUpdate.setAssigneeId(assigneeId);
        }
        if (!observersIds.isEmpty()) {
            taskForUpdate.setObserversIds(observersIds);
        }
        taskForUpdate.setStatus(status);
        return taskForUpdate;
    }

    public Mono<Task> addObserver(String id, Task task) {
        return taskRepository.findById(id)
                .flatMap(taskForUpdate -> {
                    task.getObserversIds().forEach(taskForUpdate::addObserverId);
                    return getTaskMonoWithUserEntitiesDataFromTaskMonoWithIds(taskRepository.save(taskForUpdate));
                });
    }

    public Mono<Void> deleteById(String id) {
        return taskRepository.deleteById(id);
    }

    public Mono<Void> deleteAll() {
        return taskRepository.deleteAll();
    }

}
