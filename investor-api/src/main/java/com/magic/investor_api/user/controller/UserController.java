package com.magic.investor_api.user.controller;

import com.magic.investor_api.auth.JwtService;
import com.magic.investor_api.user.service.UserService;
import com.magic.investor_api.user.dto.UserCollectionDTO;
import jakarta.servlet.http.HttpServletRequest;
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

    // Recibe userId y cardId para comprobar si el usuario tiene añadida esa carta en user_watchlist
    @GetMapping("/collection/contains")
    public ResponseEntity<Integer> collectionQuantity(HttpServletRequest httpRequest, @RequestParam Long cardId) {
        String token = httpRequest.getHeader("Authorization").substring(7);
        Long userId = jwtService.extractUserId(token);
        int quantity = userService.getCardQuantity(userId, cardId);
        return ResponseEntity.ok(quantity); // 0 si no tiene la carta, > 0 si la tiene
    }

    // Recibe userId y cardId para comprobar si el usuario tiene añadida esa carta
    @GetMapping("/watchlist/contains")
    public ResponseEntity<Boolean>  getCardWatchlistById(HttpServletRequest httpRequest, @RequestParam Long cardId){
        String token = httpRequest.getHeader("Authorization").substring(7);
        Long userId = jwtService.extractUserId(token);
        boolean result = userService.getWatchlistCardId(userId, cardId);
        if(result) return ResponseEntity.ok(true);
        return ResponseEntity.ok(false);
    }

    // Añadir carta en user_collection mediante card_id
    @PostMapping("/collection/add")
    public ResponseEntity<String> addToCollection(@RequestBody UserCollectionDTO request, HttpServletRequest httpRequest) {
        String token = httpRequest.getHeader("Authorization").substring(7);
        Long userId = jwtService.extractUserId(token);
        boolean result = userService.addToCollection(userId, request);
        if(result) return ResponseEntity.ok("Carta añadida correctamente");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al añadir carta");
    }

    // Eliminar carta en user_collection mediante card_id
    @DeleteMapping("/collection/del")
    public ResponseEntity<String> delFromCollection(@RequestBody UserCollectionDTO request, HttpServletRequest httpRequest){
        String token = httpRequest.getHeader("Authorization").substring(7);
        Long userId = jwtService.extractUserId(token);
        boolean result = userService.delFromCollection(userId, request);
        if(result) return ResponseEntity.ok("Carta añadida correctamente");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al añadir carta");
    }

    // Añadir carta en user_watchlist mediante card_id
    @PostMapping("/watchlist/add")
    public ResponseEntity<String> addToWatchlist(@RequestBody UserCollectionDTO request, HttpServletRequest httpRequest){
        String token = httpRequest.getHeader("Authorization").substring(7);
        Long userId = jwtService.extractUserId(token);
        boolean result = userService.addToWatchlist(userId, request);
        if(result) return ResponseEntity.ok("Carta añadida correctamente");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al añadir carta");
    }

    // Eliminar carta en user_watchlist mediante card_id
    @DeleteMapping("/watchlist/del")
    public ResponseEntity<String> delFromWatchlist(@RequestBody UserCollectionDTO request, HttpServletRequest httpRequest){
        String token = httpRequest.getHeader("Authorization").substring(7);
        Long userId = jwtService.extractUserId(token);
        boolean result = userService.delFromWatchlist(userId, request);
        if(result) return ResponseEntity.ok("Carta añadida correctamente");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al añadir carta");
    }

    // Obtener colección de cartas del usuario
    @GetMapping("/mycollection")
    public List<UserCollectionDTO> getUserCards(HttpServletRequest httpRequest) {
        String token = httpRequest.getHeader("Authorization").substring(7);
        Long userId = jwtService.extractUserId(token);
        return userService.getMyCollection(userId);
    }
}
