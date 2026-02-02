package com.zezame.lipayz.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("debug")
public class DebugController {
    @GetMapping
    public Object debug(Authentication authentication) {
        return authentication;
    }

}
