package com.example.task_tracker.model;

import com.example.task_tracker.entity.TaskStatus;
import lombok.Data;

import java.util.Set;

@Data
public class TaskResponseModel {

    private String id;
    private String name;
    private String description;
    private TaskStatus status;
    private UserModel author;
    private UserModel assignee;
    private Set<UserModel> observers;
}
