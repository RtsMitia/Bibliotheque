<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!-- Page Title -->
<c:set var="pageTitle" value="Mes Réservations" scope="request"/>

<!-- Custom CSS for this page -->
<style>
    .page-header {
        background: linear-gradient(135deg, #34495e 0%, #2c3e50 100%);
        color: white;
        padding: 2rem 0;
        margin-bottom: 2rem;
    }
    .reservation-card {
        border: none;
        border-radius: 15px;
        box-shadow: 0 4px 15px rgba(0,0,0,0.1);
        margin-bottom: 1.5rem;
        overflow: hidden;
        transition: transform 0.3s ease;
    }
    .reservation-card:hover {
        transform: translateY(-3px);
        box-shadow: 0 6px 25px rgba(0,0,0,0.15);
    }
    .status-header {
        padding: 1rem;
        font-weight: bold;
        text-align: center;
    }
    /*.status-attente {
        background: linear-gradient(135deg, #ffc107 0%, #ffca2c 100%);
        color: #000;
    }
    .status-confirmee {
        background: linear-gradient(135deg, #28a745 0%, #34ce57 100%);
        color: #fff;
    }
    .status-annulee {
        background: linear-gradient(135deg, #dc3545 0%, #e55564 100%);
        color: #fff;
    }
    .status-expiree {
        background: linear-gradient(135deg, #6c757d 0%, #7c848d 100%);
        color: #fff;
    }*/

   
</style>

<!-- Page Header -->
<div class="page-header">
    <div class="container">
        <div class="row align-items-center">
            <div class="col">
                <h1><i class="fas fa-user-clock"></i> Mes Réservations</h1>
                <p class="mb-0">Suivez l'état de vos réservations</p>
                <c:if test="${not empty numeroAdherent}">
                    <small>Adhérent: ${numeroAdherent}</small>
                </c:if>
            </div>
            <div class="col-auto">
                <a href="/reservations/faire-reservation" class="btn btn-light btn-lg">
                    <i class="fas fa-plus"></i> Nouvelle Réservation
                </a>
            </div>
            <div class="col-auto">
                <button type="button" class="btn btn-outline-primary" onclick="refreshPage()">
                    <i class="fas fa-sync-alt"></i> Actualiser
                </button>
            </div>
        </div>
    </div>
</div>

<div class="container">
    <!-- Success/Error Messages -->
    <c:if test="${not empty success}">
        <div class="alert alert-success alert-dismissible fade show" role="alert">
            <i class="fas fa-check-circle"></i> ${success}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    </c:if>
    
    <c:if test="${not empty error}">
        <div class="alert alert-danger alert-dismissible fade show" role="alert">
            <i class="fas fa-exclamation-circle"></i> ${error}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    </c:if>

    

    <!-- Reservations List -->
    <div class="row">
        <c:choose>
            <c:when test="${not empty reservations}">
                <c:forEach var="reservation" items="${reservations}">
                    <div class="col-lg-6 col-xl-4">
                        <div class="card reservation-card">
                            <!-- Status Header -->
                            <div class="status-header status-attente">
                                <!--<i class="fas fa-clock"></i> En attente d'approbation -->
                            </div>
                            
                            <!-- Reservation Details -->
                            <div class="card-body">
                                <h6 class="card-title">
                                    <i class="fas fa-book"></i> ${reservation.exemplaire.livre.titre}
                                </h6>
                                <p class="text-muted mb-2">
                                    <small>
                                        <i class="fas fa-user"></i> ${reservation.exemplaire.livre.auteur.nom}<br>
                                        <i class="fas fa-barcode"></i> Exemplaire #${reservation.exemplaire.id}
                                    </small>
                                </p>
                                
                                <div class="row text-center mb-3">
                                    <div class="col-6">
                                        <small class="text-muted">Réservé le</small><br>
                                        <strong>
                                            <fmt:formatDate value="${reservation.dateReservationAsDate}" pattern="dd/MM/yyyy"/>
                                        </strong><br>
                                        <small>
                                            <fmt:formatDate value="${reservation.dateReservationAsDate}" pattern="HH:mm"/>
                                        </small>
                                    </div>
                                    <div class="col-6">
                                        <small class="text-muted">Prêt souhaité le</small><br>
                                        <strong>
                                            <fmt:formatDate value="${reservation.dateDebutPretAsDate}" pattern="dd/MM/yyyy"/>
                                        </strong><br>
                                        <small>
                                            <fmt:formatDate value="${reservation.dateDebutPretAsDate}" pattern="HH:mm"/>
                                        </small>
                                    </div>
                                </div>

                                
                            </div>
                            
                            <!-- Actions -->
                            <div class="card-footer bg-light">
                                <div class="row">
                                    <div class="col">
                                        <small class="text-muted">
                                            <i class="fas fa-hashtag"></i> Réservation #${reservation.id}
                                        </small>
                                    </div>
                        
                                </div>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </c:when>
            <c:otherwise>
                <div class="col-12">
                    <div class="empty-state">
                        <i class="fas fa-calendar-times fa-4x mb-3"></i>
                        <h4>Aucune réservation trouvée</h4>
                        <p class="mb-4">
                            Vous n'avez pas encore de réservations.
                        </p>
                        <a href="/reservations/faire-reservation" class="btn btn-primary btn-lg">
                            <i class="fas fa-plus"></i> Faire ma première réservation
                        </a>
                    </div>
                </div>
            </c:otherwise>
        </c:choose>
    </div>

   
</div>

<script>
// Handle form submission with loading state
var form = document.querySelector('form');
if (form) {
    form.addEventListener('submit', function() {
        var submitBtn = document.querySelector('button[type="submit"]');
        if (submitBtn) {
            submitBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Chargement...';
            submitBtn.disabled = true;
        }
    });
}

// Add refresh button handler
function refreshPage() {
    location.reload();
}
</script>
