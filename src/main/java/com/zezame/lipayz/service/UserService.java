package com.zezame.lipayz.service;

import com.zezame.lipayz.dto.CommonResDTO;
import com.zezame.lipayz.dto.CreateResDTO;
import com.zezame.lipayz.dto.pagination.PageRes;
import com.zezame.lipayz.dto.user.CreateUserReqDTO;
import com.zezame.lipayz.dto.user.UserResDTO;
import com.zezame.lipayz.model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {
    User findByEmail(String email);

    PageRes<UserResDTO> getUsers(Pageable pageable);

    UserResDTO getUserById(String id);

    CreateResDTO registerCustomer(CreateUserReqDTO request);

    CommonResDTO deleteCustomer(String id);

    CommonResDTO activateCustomer(String email, String code);
}
