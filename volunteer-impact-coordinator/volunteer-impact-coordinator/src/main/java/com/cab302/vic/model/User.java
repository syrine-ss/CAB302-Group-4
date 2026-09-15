package com.cab302.vic.model;

public class User {

    public enum Role { COORDINATOR, VOLUNTEER }

    private int id;
    private String username;
    private String passwordHash;
    private String fullName;
    private String email;
    private Role role;

    public User(int id, String username, String passwordHash,
                String fullName, String email, Role role) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.fullName = fullName;
        this.email = email;
        this.role = role;
    }

    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getPasswordHash() { return passwordHash; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public Role getRole() { return role; }

    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setEmail(String email) { this.email = email; }
}
