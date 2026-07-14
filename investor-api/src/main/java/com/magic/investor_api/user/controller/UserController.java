package com.magic.investor_api.user.controller;

import com.magic.investor_api.auth.JwtService;
import com.magic.investor_api.user.dto.ModifyUserRequest;
import com.magic.investor_api.user.dto.UserDTO;
import com.magic.investor_api.user.dto.UserWatchlistDTO;
import com.magic.investor_api.user.service.UserService;
import com.magic.investor_api.user.dto.UserCollectionDTO;
import jakarta.servlet.http.HttpServletRequest;
import com.magic.investor_api.user.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    UserService userService;
    JwtService jwtService;
    public UserController(UserService userService, JwtService jwtService){
        this.userService = userService;
        this.jwtService = jwtService;
    }

    // Comprueba si el usuario tiene añadida la carta en user_collection
    @GetMapping("/collection/contains")
    public ResponseEntity<Integer> collectionQuantity(
            @RequestParam Long cardId,
            @RequestParam String condition,
            @RequestParam String lang,
            @RequestParam boolean foil,
            HttpServletRequest httpRequest) {

        String token = httpRequest.getHeader("Authorization").substring(7);
        Long userId = jwtService.extractUserId(token);

        UserCollectionDTO dto = new UserCollectionDTO();
        dto.setUserId(userId);
        dto.setCardId(cardId);
        dto.setCondition(condition);
        dto.setLang(lang);
        dto.setFoil(foil);
        int quantity = userService.getCardQuantity(dto);
        System.out.println(quantity);
        return ResponseEntity.ok(quantity);
    }

    // Comprueba si el usuario tiene añadida la carta en user_watchlist
    @GetMapping("/watchlist/contains")
    public ResponseEntity<Boolean>  getCardWatchlistById(HttpServletRequest httpRequest,
                                                         @RequestParam Long cardId,
                                                         @RequestParam String condition,
                                                         @RequestParam Double lastPrice,
                                                         @RequestParam boolean isFoil){
        String token = httpRequest.getHeader("Authorization").substring(7);
        Long userId = jwtService.extractUserId(token);
        UserWatchlistDTO dto = new UserWatchlistDTO(userId, cardId, lastPrice, condition, isFoil);
        boolean result = userService.getWatchlistCardId(dto);
        if(result) return ResponseEntity.ok(true);
        return ResponseEntity.ok(false);
    }

    // Añadir carta en user_collection
    @PostMapping("/collection/add")
    public ResponseEntity<String> addToCollection(@RequestBody UserCollectionDTO request,
                                                  HttpServletRequest httpRequest) {
        String token = httpRequest.getHeader("Authorization").substring(7);
        Long userId = jwtService.extractUserId(token);
        boolean result = userService.addToCollection(userId, request);
        if(result) return ResponseEntity.ok("Carta añadida correctamente");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al añadir carta");
    }

    // Eliminar carta en user_collection
    @DeleteMapping("/collection/del")
    public ResponseEntity<Boolean> delFromCollection(
            HttpServletRequest httpRequest,
            @RequestBody UserCollectionDTO request) {

        String token = httpRequest.getHeader("Authorization").substring(7);
        Long userId = jwtService.extractUserId(token);

        boolean result = userService.delFromCollection(userId, request);

        return ResponseEntity.ok(result);
    }

    // Añadir carta en user_watchlist
    @PostMapping("/watchlist/add")
    public ResponseEntity<String> addToWatchlist(HttpServletRequest httpRequest,
                                                 @RequestBody UserWatchlistDTO request){
        String token = httpRequest.getHeader("Authorization").substring(7);
        Long userId = jwtService.extractUserId(token);
        boolean result = userService.addToWatchlist(userId, request);
        if(result) return ResponseEntity.ok("Carta añadida correctamente");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al añadir carta");
    }
    // Eliminar carta en user_watchlist
    @DeleteMapping("/watchlist/del")
    public ResponseEntity<String> delFromWatchlist(HttpServletRequest httpRequest,
                                                   @RequestBody UserWatchlistDTO request){
        String token = httpRequest.getHeader("Authorization").substring(7);
        Long userId = jwtService.extractUserId(token);
        boolean result = userService.delFromWatchlist(userId, request);
        if(result) return ResponseEntity.ok("Carta eliminada correctamente");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al eliminar carta");
    }
    // Obtener colección de cartas del user
    @GetMapping("/mycollection")
    public List<UserCollectionDTO> getUserCards(HttpServletRequest httpRequest) {
        String token = httpRequest.getHeader("Authorization").substring(7);
        Long userId = jwtService.extractUserId(token);
        List<UserCollectionDTO> dto = userService.getMyCollection(userId);
        System.out.println(dto);
        return dto;
    }

    // Obtener watchlist del user
    @GetMapping("/mywatchlist")
    public List<UserWatchlistDTO> getWatchlistCards(HttpServletRequest httpRequest){
        String token = httpRequest.getHeader("Authorization").substring(7);
        Long userId = jwtService.extractUserId(token);
        return userService.getMyWatchlist(userId);
    }
    // Obtener datos del user
    @GetMapping("profile")
    public UserDTO loadProfile(HttpServletRequest httpRequest){
        String token = httpRequest.getHeader("Authorization").substring(7);
        Long userId = jwtService.extractUserId(token);
        return userService.getUserDTO(userId);
    }

    // Cambiar email
    @PostMapping("/email")
    public ResponseEntity<String> changeEmail(HttpServletRequest  httpRequest,@RequestBody ModifyUserRequest request) {


        String token = httpRequest.getHeader("Authorization").substring(7);
        Long userId = jwtService.extractUserId(token);
        // Cambiar email del user y crear objeto UserDTO(para recuperar el userId de la sesión)
        User user = userService.changeUserEmail(userService.getUserDTO(userId).getEmail(), request);
        System.out.println(user);
        if(user == null){
            return ResponseEntity.internalServerError().build();
        }
        // Generar nuevo token de sesión
        String newToken = jwtService.generateToken(user);
        return ResponseEntity.ok(newToken);
    }

    // Cambiar contraseña
    @PostMapping("password")
    public ResponseEntity<String> changePassword(HttpServletRequest httpRequest, @RequestBody ModifyUserRequest request){
        String token = httpRequest.getHeader("Authorization").substring(7);
        Long userId = jwtService.extractUserId(token);
        // Cambiar contraseña del user y obtener nuevas credenciales
        User user = userService.changePassword(userId, request);
        // Si user == null, la contraseña es antigua es incorrecta
        if(user == null){
            return ResponseEntity.badRequest().body("La contraseña actual no es correcta");
        }
        String newToken = jwtService.generateToken(user);
        return ResponseEntity.ok(newToken);
    }

    @DeleteMapping("delete")
    public ResponseEntity<String> deleteAccount(HttpServletRequest httpRequest){
        String token = httpRequest.getHeader("Authorization").substring(7);
        Long userId = jwtService.extractUserId(token);
        if(!userService.deleteUserAccount(userId)){
            return ResponseEntity.badRequest().body("La contraseña actual no es correcta");
        }
        return ResponseEntity.ok("Usuario eliminado.");
    }
}
