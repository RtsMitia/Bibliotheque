<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!-- Page Title -->
<c:set var="pageTitle" value="Connexion - Bibliothèque" scope="request"/>

<!-- Custom CSS for login page -->
<style>
    body {
        background-color: #f8f9fa;
        min-height: 100vh;
    }
    .login-container {
        min-height: 100vh;
        display: flex;
        align-items: center;
        justify-content: center;
        padding: 2rem 0;
    }
    .login-card {
        background: white;
        border-radius: 8px;
        box-shadow: 0 2px 10px rgba(0,0,0,0.1);
        max-width: 600px;
        width: 100%;
    }
    .login-header {
        background-color: #007bff;
        color: white;
        padding: 2rem;
        text-align: center;
        border-radius: 8px 8px 0 0;
    }
    .login-body {
        padding: 2rem;
    }
    .login-option {
        border: 1px solid #dee2e6;
        border-radius: 6px;
        padding: 1.5rem;
        margin-bottom: 1rem;
        background: white;
    }
    .login-option:hover {
        border-color: #007bff;
        box-shadow: 0 2px 8px rgba(0,123,255,0.1);
    }
    .login-icon {
        font-size: 2rem;
        margin-bottom: 0.5rem;
    }
</style>

<div class="login-container">
    <div class="container">
        <!-- Success/Error Messages -->
        <c:if test="${not empty successMessage}">
            <div class="alert alert-success alert-dismissible fade show mb-4" role="alert">
                <i class="fas fa-check-circle"></i> ${successMessage}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        </c:if>
        <c:if test="${not empty errorMessage}">
            <div class="alert alert-danger alert-dismissible fade show mb-4" role="alert">
                <i class="fas fa-exclamation-circle"></i> ${errorMessage}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        </c:if>

        <div class="row justify-content-center">
            <div class="col-lg-8">
                <div class="login-card">
                    <div class="login-header">
                        <i class="fas fa-book fa-2x mb-2"></i>
                        <h2 class="mb-1">Bibliothèque</h2>
                        <p class="mb-0">Système de gestion</p>
                    </div>
                    
                    <div class="login-body">
                        <!-- Client Login -->
                        <div class="login-option">
                            <div class="text-center mb-3">
                                <i class="fas fa-user login-icon text-primary"></i>
                                <h4 class="mb-2">Accès Client</h4>
                                <p class="text-muted">Connectez-vous avec votre numéro de client</p>
                            </div>
                            
                            <form action="/login/client" method="post">
                                <div class="mb-3">
                                    <label for="numeroClient" class="form-label">
                                        Numéro de Client
                                    </label>
                                    <input type="text" 
                                           class="form-control" 
                                           id="numeroClient" 
                                           name="numeroClient"
                                           placeholder="Votre numéro de client"
                                           required>
                                </div>
                                
                                <div class="d-grid">
                                    <button type="submit" class="btn btn-primary">
                                        <i class="fas fa-sign-in-alt"></i> Se connecter
                                    </button>
                                </div>
                            </form>
                        </div>

                        <!-- Admin Login -->
                        <div class="login-option">
                            <div class="text-center mb-3">
                                <i class="fas fa-user-shield login-icon text-secondary"></i>
                                <h4 class="mb-2">Accès Administrateur</h4>
                                <p class="text-muted">Accès complet au système de gestion</p>
                            </div>
                            
                            <form action="/login/admin" method="post">
                                <div class="d-grid">
                                    <button type="submit" class="btn btn-secondary">
                                        <i class="fas fa-cog"></i> Connexion Admin
                                    </button>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- Custom JavaScript for login page -->
<script>
    // Auto-hide alerts after 5 seconds
    setTimeout(function() {
        let alerts = document.querySelectorAll('.alert');
        alerts.forEach(function(alert) {
            let bsAlert = new bootstrap.Alert(alert);
            bsAlert.close();
        });
    }, 5000);

    // Focus on client number input when page loads
    document.addEventListener('DOMContentLoaded', function() {
        const clientInput = document.getElementById('numeroClient');
        if (clientInput) {
            clientInput.focus();
        }
    });
</script>
