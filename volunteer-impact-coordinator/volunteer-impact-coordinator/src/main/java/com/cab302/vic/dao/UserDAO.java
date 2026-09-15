package com.cab302.vic.dao;

import com.cab302.vic.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    private final Connection connection;

    public UserDAO() throws SQLException {
        connection = DatabaseManager.getConnection();
    }

    public void insert(User user) {
        try {
            PreparedStatement insertUser = connection.prepareStatement(
                    "INSERT INTO users "
                            + "(username, password_hash, full_name, email, role) VALUES (?, ?, ?, ?, ?)"
            );
            insertUser.setString(1, user.getUsername());
            insertUser.setString(2, user.getPasswordHash());
            insertUser.setString(3, user.getFullName());
            insertUser.setString(4, user.getEmail());
            insertUser.setString(5, user.getRole().toString());
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to insert into users table", ex);
        }
    }

    public void update(User user) {
        try {
            PreparedStatement updateUser = connection.prepareStatement(
                    "UPDATE users SET "
                            + "username = ?, password_hash = ?, full_name = ?, email = ?, role = ? WHERE id = ?"
            );
            updateUser.setString(1, user.getUsername());
            updateUser.setString(2, user.getPasswordHash());
            updateUser.setString(3, user.getFullName());
            updateUser.setString(4, user.getEmail());
            updateUser.setString(5, user.getRole().toString());
            updateUser.setInt(5, user.getId());
            updateUser.execute();
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to update users table", ex);
        }
    }
    public void delete(int id){
        try {
            PreparedStatement deleteUser = connection.prepareStatement("DELETE FROM users WHERE id = ?");
            deleteUser.setInt(1, id);
            deleteUser.execute();
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to delete user from users table", ex);
        }
    }

    public List<User> getAllUsers() {
        List<User> allUsers = new ArrayList<>();
        try {
            Statement getAll = connection.createStatement();
            ResultSet rs = getAll.executeQuery("SELECT * FROM users");
            while (rs.next()) {
                allUsers.add(
                        new User(
                                rs.getInt("id"),
                                rs.getString("username"),
                                rs.getString("password_hash"),
                                rs.getString("full_name"),
                                rs.getString("email"),
                                User.Role.valueOf(rs.getString("role"))
                        )
                );
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to retrieved users table", ex);
        }
        return allUsers;
    }

    public List<User> getUsersByRole(User.Role role) {
        List<User> roleUsers = new ArrayList<>();

        String sql = "SELECT * FROM users WHERE role = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, role.toString());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                roleUsers.add(
                        new User(
                                rs.getInt("id"),
                                rs.getString("username"),
                                rs.getString("password_hash"),
                                rs.getString("full_name"),
                                rs.getString("email"),
                                User.Role.valueOf(rs.getString("role"))
                        )
                );
            }

        } catch (SQLException ex) {
            throw new RuntimeException("Failed to retrieved users table", ex);
        }

        return roleUsers;
    }

    public User getById(int id) {
        try {
            PreparedStatement getUser = connection.prepareStatement("SELECT * FROM users WHERE id = ?");
            getUser.setInt(1, id);
            ResultSet rs = getUser.executeQuery();
            if (rs.next()) {
                return new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password_hash"),
                        rs.getString("full_name"),
                        rs.getString("email"),
                        User.Role.valueOf(rs.getString("role"))
                );
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to retrieved users by id", ex);
        }
        return null;
    }
}
