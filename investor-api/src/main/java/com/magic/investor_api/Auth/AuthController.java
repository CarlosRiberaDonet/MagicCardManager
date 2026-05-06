package com.magic.investor_api.Auth;

import com.magic.investor_api.dto.UserDTO;
import com.magic.investor_api.service.UserService;
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
    public String login(@RequestBody UserDTO request) {

        return userService.authUser(request);
    }
}
