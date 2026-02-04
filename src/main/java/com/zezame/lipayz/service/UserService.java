package com.zezame.lipayz.service;

import com.zezame.lipayz.dto.CommonResDTO;
import com.zezame.lipayz.dto.CreateResDTO;
import com.zezame.lipayz.dto.pagination.PageRes;
import com.zezame.lipayz.dto.user.*;
import com.zezame.lipayz.model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    User findByEmail(String email);

    PageRes<UserResDTO> getUsers(Integer page, Integer size);

    UserResDTO getUserById(String id);

    CreateResDTO registerUser(CreateUserReqDTO request);

    CommonResDTO deleteUser(String id);

    CommonResDTO activateCustomer(String email, String code);

    CommonResDTO changePassword(ChangePasswordDto request);

    CommonResDTO adminChangePassword(AdminChangePasswordDto request, String id);

    CommonResDTO updateUser(UpdateUserReqDTO request, String id);
}
