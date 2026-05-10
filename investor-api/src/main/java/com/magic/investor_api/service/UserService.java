package com.magic.investor_api.service;

import com.magic.investor_api.Auth.JwtService;
import com.magic.investor_api.dao.UserDAO;
import com.magic.investor_api.dto.UserCollectionDTO;
import com.magic.investor_api.dto.UserDTO;
import com.magic.investor_api.model.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

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


    // Registrar nuevo usuario en la BD
    public boolean regUser(UserDTO userDTO){
        userDTO.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        return userDAO.insertNewUser(userDTO);
    }

    // Autenticar usuario y contraseña
    public String authUser(UserDTO userDTO){

        User user = userDAO.finfByEmail(userDTO.getEmail());

        if(user == null) return null; // Usuario no existe

        if (!passwordEncoder.matches(userDTO.getPassword(), user.getPassword())) return null;

        return jwtService.generateToken(user);
    }

    //Obtener lista de user_collection mediante userId
    public List<Long> getCollectionCards(Long userId){
        return userDAO.selectCollectionCards(userId);
    }

    // Comprobar si el usuario tiene la carta en user_card
    public int getCardQuantity(Long userId, Long cardId){
        return userDAO.selectCollectionCardQuantity(userId, cardId);
    }

    // Comprobar si el usuario tiene la carta en user_watchlist
    public boolean getWatchlistCardId(Long userId, Long cardId){
       return userDAO.selectWatchlistCardId(userId, cardId);
    }

    // Añadir carta a tabla user_collection
    public boolean addToCollection(Long userId, UserCollectionDTO userCollectionDTO) {
        userCollectionDTO.setUserId(userId);
        return userDAO.insertCollectionCard(userCollectionDTO);
    }

    // Eliminar carta de tabla user_collection
    public boolean delFromCollection(Long userId, UserCollectionDTO userCollectionDTO){
        userCollectionDTO.setUserId(userId);
        return userDAO.deleteCollectionCard(userCollectionDTO);
    }

    // Añadir carta a tabla user_watchlist
    public boolean addToWatchlist(Long userId, UserCollectionDTO userCollectionDTO){
        userCollectionDTO.setUserId(userId);
        return userDAO.insertWatchlistCard(userCollectionDTO);
    }

    // Eliminar carta de tabla user_watchlist
    public boolean delFromWatchlist(Long userId, UserCollectionDTO userCollectionDTO){
        userCollectionDTO.setUserId(userId);
        return userDAO.deleteWatchlistCard(userCollectionDTO);
    }
}