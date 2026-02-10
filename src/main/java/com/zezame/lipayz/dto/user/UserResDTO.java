package com.zezame.lipayz.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserResDTO {
    private UUID id;
    private String name;
    private String roleName;
    private Integer version;
}
