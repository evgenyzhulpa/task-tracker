package com.example.task_tracker.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document("tasks")
public class Task {

    @Id
    private String id;
    private String name;
    private String description;
    private Instant createdUp;
    private Instant updatedUp;
    private TaskStatus status;
    private String authorId;
    private String assigneeId;
    private Set<String> observersIds;

    @ReadOnlyProperty
    private User author;
    @ReadOnlyProperty
    private User assignee;
    @ReadOnlyProperty
    private Set<User> observers = new HashSet<>();

    public void addObserverId(String id) {
        observersIds.add(id);
    }

    public void addObserver(User observer) {
        observers.add(observer);
    }
}
