package com.magic.investor_api.dao;

import com.magic.investor_api.dto.UserDTO;
import com.magic.investor_api.model.User;
import org.springframework.beans.factory.annotation.Autowired;
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
}
