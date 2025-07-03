package com.rtsmitia.bibliotheque.models;

public class User {
    private String numeroClient;
    private String role; // "CLIENT" or "ADMIN"
    private boolean isLoggedIn;
    private String adherentInfo; // Store full name for clients

    public User() {}

    public User(String numeroClient, String role) {
        this.numeroClient = numeroClient;
        this.role = role;
        this.isLoggedIn = true;
    }

    // Getters and setters
    public String getNumeroClient() {
        return numeroClient;
    }

    public void setNumeroClient(String numeroClient) {
        this.numeroClient = numeroClient;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        isLoggedIn = loggedIn;
    }

    public String getAdherentInfo() {
        return adherentInfo;
    }

    public void setAdherentInfo(String adherentInfo) {
        this.adherentInfo = adherentInfo;
    }

    public boolean isAdmin() {
        return "ADMIN".equals(role);
    }

    public boolean isClient() {
        return "CLIENT".equals(role);
    }

    @Override
    public String toString() {
        return "User{" +
                "numeroClient='" + numeroClient + '\'' +
                ", role='" + role + '\'' +
                ", isLoggedIn=" + isLoggedIn +
                ", adherentInfo='" + adherentInfo + '\'' +
                '}';
    }
}
