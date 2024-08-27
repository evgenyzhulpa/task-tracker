package com.example.task_tracker.model;

import com.example.task_tracker.entity.TaskStatus;
import lombok.Data;

import java.util.Set;

@Data
public class TaskRequestModel {

    private String name;
    private String description;
    private TaskStatus status;
    private String authorId;
    private String assigneeId;
    private Set<String> observersIds;
}
