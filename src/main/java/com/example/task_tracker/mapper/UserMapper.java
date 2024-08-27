package com.example.task_tracker.mapper;

import com.example.task_tracker.entity.User;
import com.example.task_tracker.model.UserModel;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    UserModel userToUserModel(User user);

    User userModelToUser(UserModel userModel);
}
