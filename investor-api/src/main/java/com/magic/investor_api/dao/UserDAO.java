package com.magic.investor_api.dao;

import com.magic.investor_api.dto.UserDTO;
import com.magic.investor_api.model.User;
import io.jsonwebtoken.security.Password;
import org.springframework.beans.factory.annotation.Autowired;
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

    public User finfByEmail(String email){
        String query = "SELECT email, password, role FROM user WHERE email = ?";
        
        try(Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)){

            stmt.setString(1, email);

            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                // Si el usuario existe
                User user = new User();
                user.setEmail(rs.getString("email"));
                user.setPassword(rs.getString("password"));
                return user;
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return null; // null si no existe
    }
}
