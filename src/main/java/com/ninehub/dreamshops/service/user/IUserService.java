package com.ninehub.dreamshops.service.user;

import com.ninehub.dreamshops.dto.UserDto;
import com.ninehub.dreamshops.model.User;
import com.ninehub.dreamshops.request.CreateUserRequest;
import com.ninehub.dreamshops.request.UpdateUserRequest;

public interface IUserService {
    User getUserById(Long userId);
    User createUser(CreateUserRequest request);
    User updateUser(UpdateUserRequest request, Long userId);
    void deleteUser(Long userid);

    UserDto convertUserToDto(User user);

    User getAuthenticatedUser();
}
