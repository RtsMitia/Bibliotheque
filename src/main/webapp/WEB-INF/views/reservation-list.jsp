<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!-- Page Title -->
<c:set var="pageTitle" value="${empty pageTitle ? 'Liste des Réservations' : pageTitle}" scope="request"/>

<!-- Custom CSS for this page -->
<style>
    .page-header {
        background: linear-gradient(135deg, #28a745 0%, #20c997 100%);
        color: white;
        padding: 2rem 0;
        margin-bottom: 2rem;
    }
    .reservation-card {
        transition: transform 0.2s;
        border: none;
        box-shadow: 0 2px 10px rgba(0,0,0,0.1);
        margin-bottom: 1rem;
    }
    .reservation-card:hover {
        transform: translateY(-2px);
        box-shadow: 0 4px 20px rgba(0,0,0,0.15);
    }
    .status-badge {
        font-size: 0.8rem;
        padding: 0.4rem 0.8rem;
    }
    .status-attente { background-color: #ffc107; color: #000; }
    .status-confirmee { background-color: #28a745; color: #fff; }
    .status-annulee { background-color: #dc3545; color: #fff; }
    .status-expiree { background-color: #6c757d; color: #fff; }
    .search-box {
        background: white;
        border-radius: 10px;
        padding: 1.5rem;
        box-shadow: 0 2px 10px rgba(0,0,0,0.1);
        margin-bottom: 2rem;
    }
</style>

<!-- Page Header -->
<div class="page-header">
    <div class="container">
        <div class="row align-items-center">
            <div class="col">
                <h1><i class="fas fa-calendar-check"></i> ${pageTitle}</h1>
                <p class="mb-0">Gérez les réservations de la bibliothèque</p>
            </div>
            <div class="col-auto">
                <a href="/reservations/nouvelle" class="btn btn-light btn-lg">
                    <i class="fas fa-plus"></i> Nouvelle Réservation
                </a>
            </div>
        </div>
    </div>
</div>

<div class="container">
    <!-- Search Box -->
    <div class="search-box">
        <form method="get" action="/reservations/recherche">
            <div class="row">
                <div class="col-md-3">
                    <label for="startDate" class="form-label">Date début</label>
                    <input type="date" class="form-control" id="startDate" name="startDate" 
                           value="<fmt:formatDate value='${searchStartDate}' pattern='yyyy-MM-dd'/>">
                </div>
                <div class="col-md-3">
                    <label for="endDate" class="form-label">Date fin</label>
                    <input type="date" class="form-control" id="endDate" name="endDate"
                           value="<fmt:formatDate value='${searchEndDate}' pattern='yyyy-MM-dd'/>">
                </div>
                <div class="col-md-3">
                    <label for="statut" class="form-label">Statut</label>
                    <select class="form-select" id="statut" name="statut">
                        <option value="">Tous les statuts</option>
                        <option value="en attente" ${searchStatut == 'en attente' ? 'selected' : ''}>En attente</option>
                        <option value="confirmée" ${searchStatut == 'confirmée' ? 'selected' : ''}>Confirmée</option>
                        <option value="annulée" ${searchStatut == 'annulée' ? 'selected' : ''}>Annulée</option>
                        <option value="expirée" ${searchStatut == 'expirée' ? 'selected' : ''}>Expirée</option>
                    </select>
                </div>
                <div class="col-md-3 d-flex align-items-end">
                    <button type="submit" class="btn btn-primary w-100">
                        <i class="fas fa-search"></i> Rechercher
                    </button>
                </div>
            </div>
        </form>
    </div>

    <!-- Quick Links -->
    <div class="row mb-3">
        <div class="col">
            <a href="/reservations" class="btn btn-outline-primary ${empty isActiveView and empty isExpiringView ? 'active' : ''}">
                <i class="fas fa-list"></i> Toutes
            </a>
            <a href="/reservations/actives" class="btn btn-outline-success ${isActiveView ? 'active' : ''}">
                <i class="fas fa-clock"></i> Actives
            </a>
            <a href="/reservations/expirent-bientot" class="btn btn-outline-warning ${isExpiringView ? 'active' : ''}">
                <i class="fas fa-exclamation-triangle"></i> Expirent bientôt
            </a>
        </div>
    </div>

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
                    <div class="col-12">
                        <div class="card reservation-card">
                            <div class="card-body">
                                <div class="row align-items-center">
                                    <div class="col-md-2">
                                        <h6 class="card-title mb-1">Réservation #${reservation.id}</h6>
                                        <small class="text-muted">
                                            <fmt:formatDate value="${reservation.dateReservation}" pattern="dd/MM/yyyy HH:mm"/>
                                        </small>
                                    </div>
                                    <div class="col-md-3">
                                        <strong>Adhérent:</strong><br>
                                        ${reservation.adherent.prenom} ${reservation.adherent.nom}<br>
                                        <small class="text-muted">${reservation.adherent.numeroAdherent}</small>
                                    </div>
                                    <div class="col-md-3">
                                        <strong>Exemplaire:</strong><br>
                                        ${reservation.exemplaire.livre.titre}<br>
                                        <small class="text-muted">Exemplaire #${reservation.exemplaire.id}</small>
                                    </div>
                                    <div class="col-md-2">
                                        <strong>Date prêt:</strong><br>
                                        <fmt:formatDate value="${reservation.dateDebutPret}" pattern="dd/MM/yyyy HH:mm"/>
                                    </div>
                                    <div class="col-md-2 text-end">
                                        <div class="mb-2">
                                            <!-- Status badge would be shown here if we had the current status -->
                                            <span class="badge status-attente">En attente</span>
                                        </div>
                                        <div class="btn-group btn-group-sm">
                                            <a href="/reservations/${reservation.id}" class="btn btn-outline-primary">
                                                <i class="fas fa-eye"></i>
                                            </a>
                                            <a href="/reservations/${reservation.id}/modifier" class="btn btn-outline-secondary">
                                                <i class="fas fa-edit"></i>
                                            </a>
                                            <button type="button" class="btn btn-outline-danger" 
                                                    onclick="confirmDelete(${reservation.id})">
                                                <i class="fas fa-trash"></i>
                                            </button>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </c:when>
            <c:otherwise>
                <div class="col-12">
                    <div class="text-center py-5">
                        <i class="fas fa-calendar-times fa-4x text-muted mb-3"></i>
                        <h4 class="text-muted">Aucune réservation trouvée</h4>
                        <p class="text-muted">Aucune réservation ne correspond à vos critères de recherche.</p>
                        <a href="/reservations/nouvelle" class="btn btn-primary">
                            <i class="fas fa-plus"></i> Créer une nouvelle réservation
                        </a>
                    </div>
                </div>
            </c:otherwise>
        </c:choose>
    </div>

    <!-- Pagination could be added here -->
</div>

<!-- Delete Confirmation Modal -->
<div class="modal fade" id="deleteModal" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title">Confirmer la suppression</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body">
                Êtes-vous sûr de vouloir supprimer cette réservation ? Cette action est irréversible.
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Annuler</button>
                <form id="deleteForm" method="post" style="display: inline;">
                    <button type="submit" class="btn btn-danger">Supprimer</button>
                </form>
            </div>
        </div>
    </div>
</div>

<script>
function confirmDelete(reservationId) {
    document.getElementById('deleteForm').action = '/reservations/' + reservationId + '/supprimer';
    new bootstrap.Modal(document.getElementById('deleteModal')).show();
}
</script>
