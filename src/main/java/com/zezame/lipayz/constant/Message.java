package com.zezame.lipayz.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Message {
    CREATED("Data Created"),
    UPDATED("Data Updated"),
    DELETED("Data Deleted");

    private String description;
}
