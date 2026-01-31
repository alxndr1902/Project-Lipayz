package com.zezame.lipayz.mapper;

import com.zezame.lipayz.dto.user.UserResDTO;
import com.zezame.lipayz.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "roleName", source = "role.name")
    UserResDTO mapToDto(User user);
}
