package com.example.task_tracker.controller;

import com.example.task_tracker.mapper.TaskMapper;
import com.example.task_tracker.model.TaskRequestModel;
import com.example.task_tracker.model.TaskResponseModel;
import com.example.task_tracker.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {
    private final TaskService taskService;
    private final TaskMapper taskMapper;

    @GetMapping("/")
    public Flux<TaskResponseModel> findAll() {
        return taskService.findAll()
                .map(taskMapper::taskToTaskResponseModel)
                .distinct();
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<TaskResponseModel>> findById(@PathVariable String id) {
        return taskService.findById(id)
                .map(taskMapper::taskToTaskResponseModel)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/by-name/{name}")
    public Mono<ResponseEntity<TaskResponseModel>> findByName(@PathVariable String name) {
        return taskService.findByName(name)
                .map(taskMapper::taskToTaskResponseModel)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Mono<ResponseEntity<TaskResponseModel>> createTask(@RequestBody TaskRequestModel requestModel) {
        return taskService.save(taskMapper.taskRequestModelToTask(requestModel))
                .map(taskMapper::taskToTaskResponseModel)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.badRequest().build());
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<TaskResponseModel>> updateTask(@PathVariable String id, @RequestBody TaskRequestModel requestModel) {
        return taskService.update(id, taskMapper.taskRequestModelToTask(requestModel))
                .map(taskMapper::taskToTaskResponseModel)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.badRequest().build());
    }

    @PutMapping("/add-observer/{id}")
    public Mono<ResponseEntity<TaskResponseModel>> addObserver(@PathVariable String id, @RequestBody TaskRequestModel requestModel) {
        return taskService.addObserver(id, taskMapper.taskRequestModelToTask(requestModel))
                .map(taskMapper::taskToTaskResponseModel)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.badRequest().build());
    }


    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteTask(@PathVariable String id) {
        return taskService.deleteById(id).then(Mono.just(ResponseEntity.noContent().build()));
    }

    @DeleteMapping("/")
    public Mono<ResponseEntity<Void>> deleteAllTasks() {
        return taskService.deleteAll().then(Mono.just(ResponseEntity.noContent().build()));
    }

}
