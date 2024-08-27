package com.example.task_tracker.mapper;

import com.example.task_tracker.entity.Task;
import com.example.task_tracker.model.TaskRequestModel;
import com.example.task_tracker.model.TaskResponseModel;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = UserMapper.class)
public interface TaskMapper {

    Task taskRequestModelToTask(TaskRequestModel model);

    TaskResponseModel taskToTaskResponseModel(Task task);
}
