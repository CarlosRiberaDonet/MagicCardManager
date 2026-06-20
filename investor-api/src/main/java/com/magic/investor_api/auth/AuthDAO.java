package com.magic.investor_api.auth;

import com.magic.investor_api.user.dto.UserCollectionDTO;
import com.magic.investor_api.user.dto.UserDTO;
import com.magic.investor_api.user.model.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AuthDAO {

    private final JdbcTemplate jdbcTemplate;

    public AuthDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // Registrar usuario
    public int insertNewUser(UserDTO userDTO){

        String sql = "INSERT INTO user (email, password, role) VALUES(?, ?, ?)";

        return jdbcTemplate.update(sql, userDTO.getEmail(), userDTO.getPassword(), "ADMIN");
    }

    // Obtener email del user a través de userId
    public String findUserById(Long userId) {

        String sql = "SELECT email FROM user WHERE id = ?";

        return jdbcTemplate.queryForObject(
                sql,
                String.class,
                userId
        );
    }

    // Comprobar si el email existe en la BD
    public boolean checkEmail(String email) {

        String sql = "SELECT EXISTS (SELECT 1 FROM user WHERE email = ?)";

        return Boolean.TRUE.equals(
                jdbcTemplate.queryForObject(sql, Boolean.class, email)
        );
    }

    // Comprobar si el email y el password existen y coinciden en la BD
    public User findByEmail(String email) {

        String sql = "SELECT id, email, password, role FROM user WHERE email = ?";

        List<User> users = jdbcTemplate.query(
                sql,
                (rs, rowNum) -> {
                    User user = new User();
                    user.setId(rs.getLong("id"));
                    user.setEmail(rs.getString("email"));
                    user.setPassword(rs.getString("password"));
                    user.setRole(rs.getString("role"));
                    return user;
                },
                email
        );

        return users.isEmpty() ? null : users.get(0);
    }

    // Cambiar email
    public boolean changeUserEmail(String email, String newEmail) {

        String sql = "UPDATE user SET email = ? WHERE email = ?";

        int rows = jdbcTemplate.update(sql, newEmail, email);

        return rows > 0;
    }

    // Obtener datos del usuario mediante su id
    public UserDTO getUserData(Long userId) {

        String sql = "SELECT id, email, role FROM user WHERE id = ?";

        return jdbcTemplate.queryForObject(
                sql,
                (rs, rowNum) -> {
                    UserDTO userDto = new UserDTO();
                    userDto.setId(rs.getLong("id"));
                    userDto.setEmail(rs.getString("email"));
                    userDto.setRole(rs.getString("role"));
                    return userDto;
                },
                userId
        );
    }
}
