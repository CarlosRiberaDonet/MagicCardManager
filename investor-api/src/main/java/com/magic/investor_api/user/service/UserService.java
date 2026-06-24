package com.magic.investor_api.user.service;

import com.magic.investor_api.auth.JwtService;
import com.magic.investor_api.user.dao.UserDAO;
import com.magic.investor_api.auth.AuthDAO;
import com.magic.investor_api.user.dto.ModifyUserRequest;
import com.magic.investor_api.user.dto.UserCollectionDTO;
import com.magic.investor_api.user.dto.UserDTO;
import com.magic.investor_api.user.model.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserDAO userDAO;
    private final AuthDAO authDAO;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public UserService(UserDAO userDAO, AuthDAO authDAO, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    // Registrar nuevo usuario en la BD
    public void regUser(UserDTO userDTO){

        userDTO.setPassword(passwordEncoder.encode(userDTO.getPassword()));

        int rows = authDAO.insertNewUser(userDTO);

        if (rows != 1) {
            throw new RuntimeException("Error creando usuario");
        }
    }

    // Obtener datos del user
    public UserDTO getUserDTO(Long userId){
        return authDAO.getUserDTOData(userId);
    }

    // Comprobar si el email ya está registrado
    public boolean checkEmail(String email){
        // Comprobar si el usuario ya existe en la BD
        return authDAO.checkEmail(email);
    }

    // Autenticar usuario y contraseña
    public String authUser(UserDTO userDTO){

        User user = authDAO.findByEmail(userDTO.getEmail());

        if(user == null) return null; // Usuario no existe

        if (!passwordEncoder.matches(userDTO.getPassword(), user.getPassword())) return null;

        return jwtService.generateToken(user);
    }

    // Modificar email del user
    public User changeUserEmail(String email, ModifyUserRequest request){
        // Comprobar si el email existe en la BD
        if(authDAO.checkEmail(email)){
            // Cambiar el email del user
            authDAO.changeUserEmail(email, request.getNewEmail());
            // Obtener usuario
            System.out.println("Obteniendo datos del user");
            User user = authDAO.getUser(authDAO.findUserByEmail(request.getNewEmail()));
            System.out.println(user);
            return user;
            // return authDAO.getUser(authDAO.findUserByEmail(email));
        }
        return null;
    }

    // Modificar contraseña del user
    public User changePassword(Long userId, ModifyUserRequest request){
        // Obtengo los datos del usuario
        UserDTO userDTO =  authDAO.getUserDTOData(userId);
        // Verificar si la contraseña antigua es correcta
        if(!passwordEncoder.matches(request.getOldPassword(), userDTO.getPassword())) {
            return null;
        }
        // Encripto la nueva contraseña
        return authDAO.changeUserPassword(userId, passwordEncoder.encode(request.getNewPassword()));
    }

    // Eliminar cuenta de usuario de la BD
    public boolean deleteUserAccount(Long userId){
        return authDAO.deleteUser(userId);
    }

    //Obtener ids de user_collection mediante userId
    public List<UserCollectionDTO> getCollectionCards(Long userId){
        return userDAO.selectCollectionCards(userId);
    }

    // Comprobar si el usuario tiene la carta en user_collection
    public int getCardQuantity(Long userId, Long cardId){
        UserCollectionDTO dto = new UserCollectionDTO(userId, cardId);
        return userDAO.selectCollectionCardQuantity(dto);
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

    // Obtener lista de cartas de la colección del usuario
    public List<UserCollectionDTO> getMyCollection(Long userId){
        return userDAO.selectMyCollection(userId);
    }
}