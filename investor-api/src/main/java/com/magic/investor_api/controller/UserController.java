package com.magic.investor_api.controller;

import com.magic.investor_api.Auth.JwtService;
import com.magic.investor_api.dto.UserCollectionDTO;
import com.magic.investor_api.service.UserService;
import com.sun.net.httpserver.HttpServer;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
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


    // Obtener colección de cartas del usuario mediante su userId
    @GetMapping("collection")
    public List<Long> getUserCollection(HttpServletRequest httpRequest){
        String token = httpRequest.getHeader("Authorization").substring(7);
        Long userId = jwtService.extractUserId(token);
        return userService.getCollectionCards(userId);
    }

    // Recibe userId y cardId para comprobar si el usuario tiene añadida esa carta
    @GetMapping("collection/contains")
    public ResponseEntity<Boolean>  getCardCollectionById(HttpServletRequest httpRequest, @RequestParam Long cardId){
        String token = httpRequest.getHeader("Authorization").substring(7);
        Long userId = jwtService.extractUserId(token);
        boolean result = userService.getWatchlistCardId(userId, cardId);
        if(result) return ResponseEntity.ok(true);
        return ResponseEntity.ok(false);
    }

    // Ingresar carta en user_collection mediante card_id
    @PostMapping("/collection/add")
    public ResponseEntity<String> addToCollection(@RequestBody UserCollectionDTO request, HttpServletRequest httpRequest) {
        String token = httpRequest.getHeader("Authorization").substring(7);
        Long userId = jwtService.extractUserId(token);
        boolean result = userService.cardToCollection(userId, request);
        if(result) return ResponseEntity.ok("Carta añadida correctamente");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al añadir carta");
    }

    // Eliminar carta en user_collection mediante card_id
    @DeleteMapping("/collection/del")
    public ResponseEntity<String> delFromCollection(@RequestBody UserCollectionDTO request, HttpServletRequest httpRequest){
        System.out.println("ELIMINANDO CARTA DE LA COLECCIÓN.");
        String token = httpRequest.getHeader("Authorization").substring(7);
        Long userId = jwtService.extractUserId(token);
        boolean result = userService.delFromCollection(userId, request);
        if(result) return ResponseEntity.ok("Carta añadida correctamente");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al añadir carta");
    }

    @PostMapping("/watchlist/add")
    public ResponseEntity<String> addToWatchlist(@RequestBody UserCollectionDTO request, HttpServletRequest httpRequest){
        String token = httpRequest.getHeader("Authorization").substring(7);
        Long userId = jwtService.extractUserId(token);
        boolean result = userService.addToWatchlist(userId, request);
        if(result) return ResponseEntity.ok("Carta añadida correctamente");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al añadir carta");
    }

    @DeleteMapping("/watchlist/del")
    public ResponseEntity<String> delFromWatchlist(@RequestBody UserCollectionDTO request, HttpServletRequest httpRequest){
        String token = httpRequest.getHeader("Authorization").substring(7);
        Long userId = jwtService.extractUserId(token);
        boolean result = userService.delFromWatchlist(userId, request);
        if(result) return ResponseEntity.ok("Carta añadida correctamente");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al añadir carta");
    }


}
