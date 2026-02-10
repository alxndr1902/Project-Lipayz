package com.zezame.lipayz.controller;

import com.zezame.lipayz.dto.CommonResDTO;
import com.zezame.lipayz.dto.CreateResDTO;
import com.zezame.lipayz.dto.pagination.PageRes;
import com.zezame.lipayz.dto.user.*;
import com.zezame.lipayz.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("users")
public class UserController {
    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasAuthority('SA')")
    public ResponseEntity<PageRes<UserResDTO>> getUsers(@RequestParam(defaultValue = "1") Integer page,
                                                        @RequestParam(defaultValue = "10") Integer size,
                                                        @RequestParam(required = false) String name) {
        var responses = userService.getUsers(page, size, name);
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    @GetMapping("{id}")
    @PreAuthorize("hasAuthority('SA')")
    public ResponseEntity<UserResDTO> getUser(@PathVariable String id) {
        var response = userService.getUserById(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("register")
    public ResponseEntity<CreateResDTO> registerUser(@RequestBody @Valid CreateUserReqDTO request) {
        var response = userService.registerUser(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PreAuthorize("hasAuthority('SA')")
    @DeleteMapping("{id}")
    public ResponseEntity<CommonResDTO> deleteUser(@PathVariable String id) {
        var response = userService.deleteUser(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<CommonResDTO> updateUser(@RequestBody @Valid UpdateUserReqDTO request,
                                                   @RequestParam(required = false) String id) {
        var response = userService.updateUser(request, id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("change-password")
    public ResponseEntity<CommonResDTO> changePassword(@RequestParam @Valid ChangePasswordDto request) {
        var response = userService.changePassword(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('SA')")
    @PutMapping("{id}/change-password")
    public ResponseEntity<CommonResDTO> changePassword(@RequestBody @Valid AdminChangePasswordDto request,
                                                       @PathVariable String id) {
        var response = userService.adminChangePassword(request, id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
