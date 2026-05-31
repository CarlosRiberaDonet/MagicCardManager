package com.magic.investor_api.auth;

import com.magic.investor_api.dto.UserDTO;
import com.magic.investor_api.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("auth")
public class AuthController {


    private final UserService userService;

    public AuthController(UserService userService){

        this.userService = userService;
    }

    // Crear endpoint para registrarme en la BD
    @PostMapping("/register")
    public boolean  register(@RequestBody UserDTO request){

        return userService.regUser(request);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody UserDTO request) {
        String token = userService.authUser(request);
        if (token == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        return ResponseEntity.ok(token);
    }
}
