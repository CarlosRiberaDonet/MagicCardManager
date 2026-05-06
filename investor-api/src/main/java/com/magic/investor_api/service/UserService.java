package com.magic.investor_api.service;

import com.magic.investor_api.Auth.JwtService;
import com.magic.investor_api.dao.UserDAO;
import com.magic.investor_api.dto.UserDTO;
import com.magic.investor_api.model.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserDAO userDAO;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public UserService(UserDAO userDAO, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userDAO = userDAO;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }


    // registrar nuevo usuario en la BD
    public boolean regUser(UserDTO userDTO){
        userDTO.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        return userDAO.insertNewUser(userDTO);
    }

    // autenticar usuario y contraseña
    public String authUser(UserDTO userDTO){

        User user = userDAO.finfByEmail(userDTO.getEmail());
        System.out.println("Usuario encontrado: " + user);

        if(user == null) return "Usuario o contraseña incorrectos."; // Usuario no existe

        if (!passwordEncoder.matches(userDTO.getPassword(), user.getPassword())) {
            System.out.println("Password incorrecta");
            return "Contraseña incorrecta.";
        }

        System.out.println("Password correcta, generando token...");

        return jwtService.generateToken(user);
    }
}