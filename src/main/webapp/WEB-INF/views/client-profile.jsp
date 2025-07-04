<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!-- Page Title -->
<c:set var="pageTitle" value="Mon Profil" scope="request"/>

<!-- Custom CSS for client profile -->
<style>
    .page-header {
        background: linear-gradient(135deg, #28a745 0%, #20c997 100%);
        color: white;
        padding: 2rem 0;
        margin-bottom: 2rem;
    }
    .profile-card {
        border: none;
        box-shadow: 0 4px 20px rgba(0,0,0,0.1);
        border-radius: 15px;
        overflow: hidden;
    }
    .profile-header {
        background: linear-gradient(135deg, #28a745 0%, #20c997 100%);
        color: white;
        padding: 2rem;
        text-align: center;
    }
    .profile-avatar {
        width: 80px;
        height: 80px;
        background: rgba(255,255,255,0.2);
        border-radius: 50%;
        display: flex;
        align-items: center;
        justify-content: center;
        margin: 0 auto 1rem;
        font-size: 2rem;
    }
    .profile-body {
        padding: 2rem;
    }
    .feature-card {
        border: 1px solid #dee2e6;
        border-radius: 10px;
        padding: 1.5rem;
        text-align: center;
        transition: all 0.3s ease;
        height: 100%;
    }
    .feature-card:hover {
        transform: translateY(-5px);
        box-shadow: 0 8px 25px rgba(0,0,0,0.1);
        border-color: #28a745;
    }
    .feature-icon {
        font-size: 2.5rem;
        color: #28a745;
        margin-bottom: 1rem;
    }
</style>

<!-- Page Header -->
<div class="page-header">
    <div class="container">
        <div class="row align-items-center">
            <div class="col">
                <h1><i class="fas fa-user"></i> Mon Profil</h1>
                <p class="mb-0">Gérez votre compte et consultez vos informations</p>
            </div>
            <div class="col-auto">
                <a href="/client/livres" class="btn btn-light">
                    <i class="fas fa-book"></i> Retour au catalogue
                </a>
            </div>
        </div>
    </div>
</div>

<div class="container">
    <!-- Success/Error Messages -->
    <c:if test="${not empty successMessage}">
        <div class="alert alert-success alert-dismissible fade show" role="alert">
            <i class="fas fa-check-circle"></i> ${successMessage}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    </c:if>
    <c:if test="${not empty errorMessage}">
        <div class="alert alert-danger alert-dismissible fade show" role="alert">
            <i class="fas fa-exclamation-circle"></i> ${errorMessage}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    </c:if>

    <div class="row">
        <!-- Profile Information -->
        <div class="col-lg-4">
            <div class="card profile-card">
                <div class="profile-header">
                    <div class="profile-avatar">
                        <i class="fas fa-user"></i>
                    </div>
                    <h4>
                        <c:choose>
                            <c:when test="${not empty currentUser.adherentInfo}">
                                ${currentUser.adherentInfo}
                            </c:when>
                            <c:otherwise>
                                Client de la Bibliothèque
                            </c:otherwise>
                        </c:choose>
                    </h4>
                    <p class="mb-0">N° ${currentUser.numeroClient}</p>
                </div>
                
                <div class="profile-body">
                    <h6 class="mb-3">Informations du compte</h6>
                    <c:if test="${not empty currentUser.adherentInfo}">
                        <div class="mb-3">
                            <label class="form-label"><strong>Nom complet:</strong></label>
                            <div class="form-control-plaintext">${currentUser.adherentInfo}</div>
                        </div>
                    </c:if>
                    <div class="mb-3">
                        <label class="form-label"><strong>Numéro de client:</strong></label>
                        <div class="form-control-plaintext">${currentUser.numeroClient}</div>
                    </div>
                    <div class="mb-3">
                        <label class="form-label"><strong>Type de compte:</strong></label>
                        <div class="form-control-plaintext">
                            <span class="badge bg-success">Client</span>
                        </div>
                    </div>
                    <div class="mb-3">
                        <label class="form-label"><strong>Statut:</strong></label>
                        <div class="form-control-plaintext">
                            <span class="badge bg-success">
                                <i class="fas fa-check-circle"></i> Connecté
                            </span>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Quick Actions -->
        <div class="col-lg-8">
            <h5 class="mb-4">Actions disponibles</h5>
            
            <div class="row">
                <div class="col-md-6 mb-4">
                    <div class="feature-card">
                        <i class="fas fa-book feature-icon"></i>
                        <h6>Catalogue des Livres</h6>
                        <p class="text-muted mb-3">Consultez tous les livres disponibles dans notre bibliothèque</p>
                        <a href="/client/livres" class="btn btn-success">
                            <i class="fas fa-arrow-right"></i> Consulter
                        </a>
                    </div>
                </div>
                
                <div class="col-md-6 mb-4">
                    <div class="feature-card">
                        <i class="fas fa-search feature-icon"></i>
                        <h6>Rechercher un Livre</h6>
                        <p class="text-muted mb-3">Trouvez rapidement un livre par titre ou auteur</p>
                        <form action="/client/livres/search" method="get">
                            <div class="input-group">
                                <input type="text" class="form-control" name="searchTerm" placeholder="Rechercher...">
                                <button class="btn btn-success" type="submit">
                                    <i class="fas fa-search"></i>
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
                
                <div class="col-md-6 mb-4">
                    <div class="feature-card">
                        <i class="fas fa-hand-holding feature-icon"></i>
                        <h6>Emprunter un Livre</h6>
                        <p class="text-muted mb-3">Empruntez vos livres préférés directement depuis le catalogue</p>
                        <span class="text-success">
                            <i class="fas fa-check"></i> Fonctionnalité active
                        </span>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- Custom JavaScript for this page -->
<script>
    // Auto-hide alerts after 5 seconds
    setTimeout(function() {
        let alerts = document.querySelectorAll('.alert');
        alerts.forEach(function(alert) {
            let bsAlert = new bootstrap.Alert(alert);
            bsAlert.close();
        });
    }, 5000);
</script>
