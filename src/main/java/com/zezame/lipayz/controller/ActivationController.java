package com.zezame.lipayz.controller;

import com.zezame.lipayz.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@RequestMapping("users")
public class ActivationController {
    private final UserService userService;

    @GetMapping("activate")
    public String activateUser(@RequestParam String email,
                               @RequestParam String code) {
        var response = userService.activateCustomer(email, code);
        try {
            return "success";
        } catch (Exception e) {
            return "fail";
        }
    }
}
