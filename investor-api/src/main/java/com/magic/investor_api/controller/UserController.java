package com.magic.investor_api.controller;

import com.magic.investor_api.Auth.JwtService;
import com.magic.investor_api.dto.UserCollectionDTO;
import com.magic.investor_api.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    UserService userService;
    JwtService jwtService;
    public UserController(UserService userService, JwtService jwtService){
        this.userService = userService;
        this.jwtService = jwtService;
    }

    // Ingresar carta en user_collection mediante card_id
    @PostMapping("/add")
    public boolean saveCard(@RequestBody UserCollectionDTO request, HttpServletRequest httpRequest) {
        String token = httpRequest.getHeader("Authorization").substring(7);
        Long userId = jwtService.extractUserId(token);
        return userService.saveUserCard(userId, request);
    }

    // Eliminar carta en user_collection mediante card_id
    @DeleteMapping("/del")
    public boolean deleteCard(@RequestBody UserCollectionDTO request, HttpServletRequest httpRequest){
        String token = httpRequest.getHeader("Authorization").substring(7);
        Long userId = jwtService.extractUserId(token);
        return userService.removeCard(userId, request);
    }
}
