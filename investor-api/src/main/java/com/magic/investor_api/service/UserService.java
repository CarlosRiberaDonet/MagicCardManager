package com.magic.investor_api.service;

import com.magic.investor_api.dao.UserDAO;
import com.magic.investor_api.dto.UserDTO;
import com.magic.investor_api.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserDAO userDAO;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserDAO userDAO, PasswordEncoder passwordEncoder) {
        this.userDAO = userDAO;
        this.passwordEncoder = passwordEncoder;
    }

    public String authUser(UserDTO userDTO){

        User user = userDAO.finfByEmail(userDTO.getEmail());

        if(user == null) return "Usuario o contraseña incorrectos."; // Usuario no existe

        if (!passwordEncoder.matches(userDTO.getPassword(), user.getPassword())) return null; // password incorrecta

        return "TOKEN";
    }
}
