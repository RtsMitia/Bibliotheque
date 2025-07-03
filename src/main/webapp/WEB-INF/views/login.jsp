<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!-- Page Title -->
<c:set var="pageTitle" value="Connexion - Bibliothèque" scope="request"/>

<!-- Custom CSS for login page -->
<style>
    body {
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        min-height: 100vh;
    }
    .login-container {
        min-height: 100vh;
        display: flex;
        align-items: center;
        justify-content: center;
    }
    .login-card {
        background: white;
        border-radius: 20px;
        box-shadow: 0 20px 40px rgba(0,0,0,0.1);
        overflow: hidden;
        max-width: 800px;
        width: 100%;
    }
    .login-header {
        background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
        color: white;
        padding: 3rem 2rem;
        text-align: center;
    }
    .login-body {
        padding: 3rem 2rem;
    }
    .login-option {
        border: 2px solid #e9ecef;
        border-radius: 15px;
        padding: 2rem;
        margin-bottom: 1.5rem;
        transition: all 0.3s ease;
        background: #f8f9fa;
    }
    .login-option:hover {
        border-color: #007bff;
        background: white;
        transform: translateY(-2px);
        box-shadow: 0 8px 25px rgba(0,123,255,0.1);
    }
    .login-option.client {
        border-color: #28a745;
    }
    .login-option.client:hover {
        border-color: #28a745;
        box-shadow: 0 8px 25px rgba(40,167,69,0.1);
    }
    .login-option.admin {
        border-color: #dc3545;
    }
    .login-option.admin:hover {
        border-color: #dc3545;
        box-shadow: 0 8px 25px rgba(220,53,69,0.1);
    }
    .login-icon {
        font-size: 3rem;
        margin-bottom: 1rem;
    }
    .client-form .form-control {
        border-radius: 10px;
        padding: 0.75rem 1rem;
        border: 2px solid #e9ecef;
        transition: border-color 0.3s ease;
    }
    .client-form .form-control:focus {
        border-color: #28a745;
        box-shadow: 0 0 0 0.2rem rgba(40,167,69,0.1);
    }
    .btn-client {
        background: linear-gradient(135deg, #28a745 0%, #20c997 100%);
        border: none;
        border-radius: 10px;
        padding: 0.75rem 2rem;
        font-weight: 600;
        transition: all 0.3s ease;
    }
    .btn-client:hover {
        transform: translateY(-2px);
        box-shadow: 0 8px 25px rgba(40,167,69,0.3);
    }
    .btn-admin {
        background: linear-gradient(135deg, #dc3545 0%, #fd7e14 100%);
        border: none;
        border-radius: 10px;
        padding: 0.75rem 2rem;
        font-weight: 600;
        transition: all 0.3s ease;
    }
    .btn-admin:hover {
        transform: translateY(-2px);
        box-shadow: 0 8px 25px rgba(220,53,69,0.3);
    }
    .library-features {
        background: #f8f9fa;
        border-radius: 10px;
        padding: 1.5rem;
        margin-top: 2rem;
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
            <div class="col-lg-10">
                <div class="login-card">
                    <div class="login-header">
                        <i class="fas fa-book-open fa-3x mb-3"></i>
                        <h1 class="mb-2">Système de Bibliothèque</h1>
                        <p class="mb-0">Bienvenue dans notre système de gestion de bibliothèque</p>
                    </div>
                    
                    <div class="login-body">
                        <div class="row">
                            <!-- Client Login -->
                            <div class="col-md-6">
                                <div class="login-option client">
                                    <div class="text-center">
                                        <i class="fas fa-user login-icon text-success"></i>
                                        <h3 class="text-success mb-3">Accès Client</h3>
                                        <p class="text-muted mb-4">
                                            Connectez-vous avec votre numéro de client pour consulter 
                                            le catalogue et emprunter des livres.
                                        </p>
                                    </div>
                                    
                                    <form action="/login/client" method="post" class="client-form">
                                        <div class="mb-3">
                                            <label for="numeroClient" class="form-label fw-bold">
                                                <i class="fas fa-id-card"></i> Numéro de Client
                                            </label>
                                            <input type="text" 
                                                   class="form-control" 
                                                   id="numeroClient" 
                                                   name="numeroClient"
                                                   placeholder="Saisissez votre numéro de client"
                                                   required>
                                        </div>
                                        
                                        <div class="d-grid">
                                            <button type="submit" class="btn btn-success btn-client">
                                                <i class="fas fa-sign-in-alt"></i> Se connecter
                                            </button>
                                        </div>
                                    </form>
                                    
                                    <div class="mt-3">
                                        <small class="text-muted">
                                            <i class="fas fa-info-circle"></i> 
                                            Fonctionnalités client : Consultation du catalogue, Emprunt de livres
                                        </small>
                                    </div>
                                </div>
                            </div>

                            <!-- Admin Login -->
                            <div class="col-md-6">
                                <div class="login-option admin">
                                    <div class="text-center">
                                        <i class="fas fa-user-shield login-icon text-danger"></i>
                                        <h3 class="text-danger mb-3">Accès Administrateur</h3>
                                        <p class="text-muted mb-4">
                                            Accès complet au système de gestion de la bibliothèque 
                                            avec toutes les fonctionnalités.
                                        </p>
                                    </div>
                                    
                                    <form action="/login/admin" method="post">
                                        <div class="d-grid">
                                            <button type="submit" class="btn btn-danger btn-admin">
                                                <i class="fas fa-crown"></i> Connexion Administrateur
                                            </button>
                                        </div>
                                    </form>
                                    
                                    <div class="mt-3">
                                        <small class="text-muted">
                                            <i class="fas fa-tools"></i> 
                                            Fonctionnalités admin : Gestion complète des livres, exemplaires, utilisateurs
                                        </small>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <!-- Features Information -->
                        <div class="library-features">
                            <h5 class="text-center mb-3">
                                <i class="fas fa-star"></i> Fonctionnalités de la Bibliothèque
                            </h5>
                            <div class="row text-center">
                                <div class="col-md-3">
                                    <i class="fas fa-search fa-2x text-primary mb-2"></i>
                                    <h6>Recherche</h6>
                                    <small class="text-muted">Recherchez par titre ou auteur</small>
                                </div>
                                <div class="col-md-3">
                                    <i class="fas fa-bookmark fa-2x text-info mb-2"></i>
                                    <h6>Réservation</h6>
                                    <small class="text-muted">Réservez vos livres préférés</small>
                                </div>
                                <div class="col-md-3">
                                    <i class="fas fa-clock fa-2x text-warning mb-2"></i>
                                    <h6>Suivi</h6>
                                    <small class="text-muted">Suivez vos emprunts en temps réel</small>
                                </div>
                                <div class="col-md-3">
                                    <i class="fas fa-chart-bar fa-2x text-success mb-2"></i>
                                    <h6>Statistiques</h6>
                                    <small class="text-muted">Consultez les statistiques</small>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- Custom JavaScript for login page -->
<script>
    // Auto-hide alerts after 7 seconds
    setTimeout(function() {
        let alerts = document.querySelectorAll('.alert');
        alerts.forEach(function(alert) {
            let bsAlert = new bootstrap.Alert(alert);
            bsAlert.close();
        });
    }, 7000);

    // Add smooth animations
    document.addEventListener('DOMContentLoaded', function() {
        // Animate login options on load
        const loginOptions = document.querySelectorAll('.login-option');
        loginOptions.forEach((option, index) => {
            setTimeout(() => {
                option.style.opacity = '0';
                option.style.transform = 'translateY(20px)';
                option.style.transition = 'all 0.5s ease';
                
                setTimeout(() => {
                    option.style.opacity = '1';
                    option.style.transform = 'translateY(0)';
                }, 100);
            }, index * 200);
        });

        // Focus on client number input
        const clientInput = document.getElementById('numeroClient');
        if (clientInput) {
            clientInput.focus();
        }
    });
</script>
