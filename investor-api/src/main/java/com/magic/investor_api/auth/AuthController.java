package com.magic.investor_api.auth;

import com.magic.investor_api.user.dto.ChangeEmailRequest;
import com.magic.investor_api.user.dto.UserDTO;
import com.magic.investor_api.user.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService){
        this.userService = userService;
    }

    // Registrar user en la BD
    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody UserDTO request) {

        // Comprobar si el usuario ya existe en la BD
        if(userService.checkEmail(request.getEmail())){
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .build();
        }

        userService.regUser(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .build();
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody UserDTO request) {
        String token = userService.authUser(request);
        if (token == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        return ResponseEntity.ok(token);
    }
}
