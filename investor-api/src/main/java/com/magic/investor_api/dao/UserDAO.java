package com.magic.investor_api.dao;

import com.magic.investor_api.dto.UserCollectionDTO;
import com.magic.investor_api.dto.UserDTO;
import com.magic.investor_api.model.User;
import org.hibernate.sql.model.PreparableMutationOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Repository

public class UserDAO {

    @Autowired
    private DataSource dataSource;
    private final PasswordEncoder passwordEncoder;

    public UserDAO(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public boolean insertNewUser(UserDTO userDTO){

        String query = "INSERT INTO user (email, password, role) VALUES(?, ?, ?)";
        try(Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)){

            stmt.setString(1, userDTO.getEmail());
            stmt.setString(2, userDTO.getPassword());
            stmt.setString(3, "ADMIN");

            int filasAfectadas = stmt.executeUpdate();
            if(filasAfectadas > 0){
                // Si el usuario se inserta correctamente en la BD
                System.out.println("Usuario: " + userDTO.getEmail() + " Registrado correctamente.");
                return true;
            }
        }catch(SQLException e){
            e.printStackTrace();
        }

        return false;
    }

    // Comprobar si el email y el password existen en la BD
    public User finfByEmail(String email){
        String query = "SELECT id, email, password, role FROM user WHERE email = ?";
        
        try(Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)){

            stmt.setString(1, email);

            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                // Si el usuario existe
                User user = new User();
                user.setId(rs.getLong("id"));
                user.setEmail(rs.getString("email"));
                user.setPassword(rs.getString("password"));
                user.setRole(rs.getString("role"));
                return user;
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return null; // null si no existe
    }

    // Insertar carta en la tabla user_collection
    public boolean insertCard(UserCollectionDTO dto){

        if (cardExistsInCollection(dto.getUserId(), dto.getCardId())) {
            return updateQuantity(dto.getUserId(), dto.getCardId(), +1);
        }

        String query = "INSERT INTO user_collection (user_id, card_id, purchase_price, quantity) " +
                "VALUES(?, ?, ?, ?)";

        try(Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)){

            stmt.setLong(1, dto.getUserId());
            stmt.setLong(2, dto.getCardId());
            stmt.setDouble(3, dto.getPurchasePrice());
            stmt.setInt(4, 1);

            int filasAfectadas = stmt.executeUpdate();
            if(filasAfectadas > 0){
                return true;
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    // Eliminar una carta de user_collection
    public boolean deleteCardById(UserCollectionDTO dto){

        int quantity = getQuantity(dto.getUserId(), dto.getCardId());

        System.out.println("Cantidad: " + quantity);
        // Si quantity > 1
        if(quantity > 1){
            updateQuantity(dto.getUserId(), dto.getCardId(), -1); // resto -1 a quantity
        }
        else if(quantity == 1) { // Si quantity == 1, elimino la fila
            String query = "DELETE FROM user_collection WHERE user_id = ? AND card_id = ?";
            try(Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setLong(1, dto.getUserId());
                stmt.setLong(2, dto.getCardId());
                stmt.executeUpdate();
            }catch (SQLException e){
                e.printStackTrace();
            }
        }
        return false;
    }

    // Comprobar si la carta ya está en user_collection
    public boolean cardExistsInCollection(Long userId, Long cardId){
        String query = "SELECT id FROM user_collection WHERE user_id = ? AND card_id = ?";
        try(Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)){
            stmt.setLong(1, userId);
            stmt.setLong(2, cardId);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                return true;
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    // Actualiza la cantidad de una carta en user_collection (positivo suma, negativo resta)
    public boolean updateQuantity(Long userId, Long cardId, int quantity){
        String query = "UPDATE user_collection SET quantity = quantity + ? WHERE user_id = ? " +
                " AND card_id = ?";

        try(Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)){
            stmt.setInt(1, quantity);
            stmt.setLong(2, userId);
            stmt.setLong(3, cardId);

            int filasAfectadas = stmt.executeUpdate();
            if(filasAfectadas > 0){
                return true;
            }
        }catch (SQLException e){
            e.printStackTrace();
        }

        return false;
    }

    // Obtener cantidad de una carta en user_collection
    public int getQuantity(Long userId, Long cardId){
        String query = "SELECT quantity FROM user_collection WHERE user_id = ? AND card_id = ?";

        try(Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)){
            stmt.setLong(1, userId);
            stmt.setLong(2, cardId);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                return rs.getInt("quantity");
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return -1;
    }
}