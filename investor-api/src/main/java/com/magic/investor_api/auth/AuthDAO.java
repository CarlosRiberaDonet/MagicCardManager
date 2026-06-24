package com.magic.investor_api.auth;

import com.magic.investor_api.user.dto.UserDTO;
import com.magic.investor_api.user.model.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.concurrent.ExecutionException;

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

    // Obtener id del user a través de su email
    public Long findUserByEmail(String email){
        String sql = "SELECT id FROM user WHERE email = ?";

        return  jdbcTemplate.queryForObject(sql, Long.class, email);
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
        try{
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
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    // Cambiar email
    public boolean changeUserEmail(String email, String newEmail) {
        String sql = "UPDATE user SET email = ? WHERE email = ?";
        try{
            int rows = jdbcTemplate.update(sql, newEmail, email);
            return rows > 0;
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    // Cambiar password
    public User changeUserPassword(Long userId, String newPassword) {

        String updateSql = "UPDATE user SET password = ? WHERE id = ?";
        jdbcTemplate.update(updateSql, newPassword, userId);

        String selectSql = "SELECT id, email, role FROM user WHERE id = ?";
        return jdbcTemplate.queryForObject(
                selectSql,
                (rs, rowNum) -> {
                    User user = new User();
                    user.setId(rs.getLong("id"));
                    user.setEmail(rs.getString("email"));
                    user.setRole(rs.getString("role"));
                    return user;
                },
                userId
        );
    }

    // Obtener objeto UserDTO
    public UserDTO getUserDTOData(Long userId) {

        String sql = "SELECT id, email, password, role FROM user WHERE id = ?";

        try{
            return jdbcTemplate.queryForObject(
                    sql,
                    (rs, rowNum) -> {
                        UserDTO userDto = new UserDTO();
                        userDto.setId(rs.getLong("id"));
                        userDto.setEmail(rs.getString("email"));
                        userDto.setPassword(rs.getString("password"));
                        userDto.setRole(rs.getString("role"));
                        return userDto;
                    },
                    userId
            );
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    // Obtener objeto usuario
    public User getUser(Long userId){
        String sql = "SELECT id, email, role FROM user WHERE id = ?";

        return jdbcTemplate.queryForObject(
                sql,
                (rs, rowNum) -> {
                    User user = new User();
                    user.setId(rs.getLong("id"));
                    user.setEmail(rs.getString("email"));
                    user.setRole(rs.getString("role"));
                    return user;
                },
                userId
        );
    }

    // Eliminar cuenta de usuario de la BD
    public boolean deleteUser(Long userId){
        String sql = "DELETE FROM user WHERE id = ?";

        try{
            int filasAfectadas = jdbcTemplate.update(sql, userId);
            if(filasAfectadas > 0){
                return true;
            }
        }catch (Exception e){
            throw new RuntimeException(e);
        }
        return false;
    }
}
