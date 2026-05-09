package com.magic.investor_api.service;

import com.magic.investor_api.Auth.JwtService;
import com.magic.investor_api.dao.UserDAO;
import com.magic.investor_api.dto.UserCollectionDTO;
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

        if(user == null) return null; // Usuario no existe

        if (!passwordEncoder.matches(userDTO.getPassword(), user.getPassword())) return null;

        return jwtService.generateToken(user);
    }

    // Insertar carta en la tabla user_collection
    public boolean saveUserCard(Long userId, UserCollectionDTO userCollectionDTO) {
        userCollectionDTO.setUserId(userId);
        return userDAO.insertCard(userCollectionDTO);
    }

    // Eliminar carta de la tabla user_collection
    public boolean removeCard(Long userId, UserCollectionDTO userCollectionDTO){
        userCollectionDTO.setUserId(userId);
        return userDAO.deleteCardById(userCollectionDTO);
    }
}