package com.zezame.lipayz.controller;

import com.zezame.lipayz.dto.CommonResDTO;
import com.zezame.lipayz.dto.CreateResDTO;
import com.zezame.lipayz.dto.user.CreateUserReqDTO;
import com.zezame.lipayz.dto.user.UserResDTO;
import com.zezame.lipayz.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("users")
public class UserController {
    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<UserResDTO>> getUsers(@RequestParam(required = false) String roleCode) {
        List<UserResDTO> responses = userService.getUsers(roleCode);
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    @GetMapping("{id}")
    public ResponseEntity<UserResDTO> getUser(@PathVariable String id) {
        var response = userService.getUserById(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("register")
    public ResponseEntity<CreateResDTO> registerCustomer(@RequestBody @Valid CreateUserReqDTO request) {
        var response = userService.registerCustomer(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<CommonResDTO> deleteCustomer(@PathVariable String id) {
        var response = userService.deleteCustomer(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PatchMapping("activate")
    public ResponseEntity<CommonResDTO> activateCustomer(@RequestParam String email,
                                                         @RequestParam String code) {
        var response = userService.activateCustomer(email, code);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
