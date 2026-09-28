package com.tiaprende.backend.user.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.tiaprende.backend.user.dto.UpdateUserRequest;
import com.tiaprende.backend.user.dto.UserResponse;
import com.tiaprende.backend.user.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(
            UserService userService
    ) {
        this.userService = userService;
    }

    /*
     * GET /api/users
     */
    @GetMapping
    public List<UserResponse> listarTodos() {

        return userService.listarTodos();
    }

    /*
     * GET /api/users/1
     */
    @GetMapping("/{id}")
    public UserResponse buscarPorId(
            @PathVariable Long id
    ) {

        return userService.buscarPorId(id);
    }

    /*
     * PATCH /api/users/1
     */
    @PatchMapping("/{id}")
    public UserResponse atualizar(
            @PathVariable Long id,
            @RequestBody UpdateUserRequest request
    ) {

        return userService.atualizar(
                id,
                request
        );
    }
}