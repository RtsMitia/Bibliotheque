<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!-- Page Title -->
<c:set var="pageTitle" value="Gestion des Réservations - Admin" scope="request"/>

<!-- Custom CSS for this page -->
<style>
    .page-header {
        background: linear-gradient(135deg, #34495e 0%, #2c3e50 100%);
        color: white;
        padding: 2rem 0;
        margin-bottom: 2rem;
    }
    .admin-card {
        border: 1px solid #e9ecef;
        border-radius: 8px;
        box-shadow: 0 2px 8px rgba(0,0,0,0.08);
        margin-bottom: 2rem;
        background: #fff;
    }
    .pending-card {
        border-left: 3px solid #f39c12;
    }
    .confirmed-card {
        border-left: 3px solid #27ae60;
    }
    .action-btn {
        margin: 0.2rem;
        font-size: 0.875rem;
    }
    .reservation-item {
        border: 1px solid #e9ecef;
        border-radius: 6px;
        padding: 1rem;
        margin-bottom: 1rem;
        transition: all 0.2s ease;
        background: #f8f9fa;
    }
    .reservation-item:hover {
        background-color: #fff;
        border-color: #2c3e50;
        transform: translateY(-1px);
    }
    .status-badge {
        font-size: 0.8rem;
        padding: 0.4rem 0.8rem;
    }

    .card-header {
        border-bottom: 1px solid #e9ecef;
        border-radius: 6px 6px 0 0 !important;
    }
    .pending-header {
        background-color: #fff3cd !important;
        color: #856404 !important;
        border-bottom: 1px solid #f39c12;
    }
    .confirmed-header {
        background-color: #d4edda !important;
        color: #155724 !important;
        border-bottom: 1px solid #27ae60;
    }
    .reservation-title {
        color: #2c3e50;
        font-weight: 600;
    }
    .reservation-meta {
        color: #6c757d;
        font-size: 0.9rem;
    }
</style>

<!-- Page Header -->
<div class="page-header">
    <div class="container">
        <div class="row align-items-center">
            <div class="col">
                <h1><i class="fas fa-shield-alt"></i> Panneau d'Administration</h1>
                <p class="mb-0">Gérez et approuvez les réservations</p>
            </div>
            <div class="col-auto">
                <a href="/reservations" class="btn btn-light btn-lg">
                    <i class="fas fa-list"></i> Toutes les Réservations
                </a>
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

    <!-- Pending Reservations -->
    <div class="admin-card pending-card">
        <div class="card-header pending-header">
            <h5 class="mb-0">
                <i class="fas fa-clock"></i> Réservations en attente d'approbation
                <span class="badge bg-secondary">${pendingReservations.size()}</span>
            </h5>
        </div>
        <div class="card-body">
            <c:choose>
                <c:when test="${not empty pendingReservations}">
                    <c:forEach var="reservation" items="${pendingReservations}">
                        <div class="reservation-item">
                            <div class="row align-items-center">
                                <div class="col-md-2">
                                    <h6 class="reservation-title mb-1">Réservation #${reservation.id}</h6>
                                    <small class="reservation-meta">
                                        <fmt:formatDate value="${reservation.dateReservationAsDate}" pattern="dd/MM/yyyy HH:mm"/>
                                    </small>
                                </div>
                                <div class="col-md-3">
                                    <strong>Adhérent:</strong><br>
                                    ${reservation.adherent.prenom} ${reservation.adherent.nom}<br>
                                    <small class="reservation-meta">${reservation.adherent.numeroAdherent}</small>
                                </div>
                                <div class="col-md-3">
                                    <strong>Livre:</strong><br>
                                    ${reservation.exemplaire.livre.titre}<br>
                                    <small class="reservation-meta">
                                        Par: ${reservation.exemplaire.livre.auteur.nom}<br>
                                        Exemplaire #${reservation.exemplaire.id}
                                    </small>
                                </div>
                                <div class="col-md-2">
                                    <strong>Date souhaitée:</strong><br>
                                    <fmt:formatDate value="${reservation.dateDebutPretAsDate}" pattern="dd/MM/yyyy HH:mm"/>
                                </div>
                                <div class="col-md-2">
                                    <div class="btn-group-vertical w-100">
                                        <button class="btn btn-success btn-sm action-btn" 
                                                onclick="approveReservation('${reservation.id}')"
                                                title="Approuver cette réservation">
                                            <i class="fas fa-check"></i> Approuver
                                        </button>
                                        <button class="btn btn-danger btn-sm action-btn" 
                                                onclick="rejectReservation('${reservation.id}')"
                                                title="Rejeter cette réservation">
                                            <i class="fas fa-times"></i> Rejeter
                                        </button>
                                        
                                    </div>
                                </div>
                            </div>
                        </div>
                    </c:forEach>
                </c:when>
                <c:otherwise>
                    <div class="text-center text-muted py-4">
                        <i class="fas fa-check-circle fa-3x mb-2"></i>
                        <h5>Aucune réservation en attente</h5>
                        <p>Toutes les réservations ont été traitées!</p>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </div>

    <!-- Confirmed Reservations -->
    <div class="admin-card confirmed-card">
        <div class="card-header confirmed-header">
            <h5 class="mb-0">
                <i class="fas fa-check-circle"></i> Réservations confirmées
                <span class="badge bg-secondary">${confirmedReservations.size()}</span>
            </h5>
        </div>
        <div class="card-body">
            <c:choose>
                <c:when test="${not empty confirmedReservations}">
                    <c:forEach var="reservation" items="${confirmedReservations}">
                        <div class="reservation-item">
                            <div class="row align-items-center">
                                <div class="col-md-2">
                                    <h6 class="reservation-title mb-1">Réservation #${reservation.id}</h6>
                                    <small class="reservation-meta">
                                        <fmt:formatDate value="${reservation.dateReservationAsDate}" pattern="dd/MM/yyyy HH:mm"/>
                                    </small>
                                </div>
                                <div class="col-md-3">
                                    <strong>Adhérent:</strong><br>
                                    ${reservation.adherent.prenom} ${reservation.adherent.nom}<br>
                                    <small class="reservation-meta">${reservation.adherent.numeroAdherent}</small>
                                </div>
                                <div class="col-md-3">
                                    <strong>Livre:</strong><br>
                                    ${reservation.exemplaire.livre.titre}<br>
                                    <small class="reservation-meta">
                                        Par: ${reservation.exemplaire.livre.auteur.nom}<br>
                                        Exemplaire #${reservation.exemplaire.id}
                                    </small>
                                </div>
                                <div class="col-md-2">
                                    <strong>Date prêt:</strong><br>
                                    <fmt:formatDate value="${reservation.dateDebutPretAsDate}" pattern="dd/MM/yyyy HH:mm"/>
                                    <br>
                                    <span class="badge bg-success">Confirmée</span>
                                </div>
                                <!-- <div class="col-md-2">
                                    <div class="btn-group-vertical w-100">
                                        <button class="btn btn-primary btn-sm action-btn" 
                                                onclick="convertToPret('${reservation.id}')"
                                                title="Convertir en prêt">
                                            <i class="fas fa-arrow-right"></i> Prêt
                                        </button>
                                        <button class="btn btn-warning btn-sm action-btn" 
                                                onclick="cancelReservation('${reservation.id}')"
                                                title="Annuler la réservation">
                                            <i class="fas fa-ban"></i> Annuler
                                        </button>
                               
                                    </div>
                                </div> -->
                            </div>
                        </div>
                    </c:forEach>
                </c:when>
                <c:otherwise>
                    <div class="text-center text-muted py-4">
                        <i class="fas fa-calendar-check fa-3x mb-2"></i>
                        <h5>Aucune réservation confirmée</h5>
                        <p>Les réservations confirmées apparaîtront ici.</p>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</div>

<!-- Confirmation Modals -->
<div class="modal fade" id="approveModal" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title">Approuver la réservation</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body">
                Êtes-vous sûr de vouloir approuver cette réservation ? Les règles de gestion seront vérifiées.
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Annuler</button>
                <form id="approveForm" method="post" style="display: inline;">
                    <button type="submit" class="btn btn-success">Approuver</button>
                </form>
            </div>
        </div>
    </div>
</div>

<div class="modal fade" id="rejectModal" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title">Rejeter la réservation</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body">
                <p>Voulez-vous rejeter cette réservation ?</p>
                <div class="mb-3">
                    <label for="rejectReason" class="form-label">Raison (optionnel)</label>
                    <textarea class="form-control" id="rejectReason" name="reason" rows="3"
                              placeholder="Expliquez pourquoi cette réservation est rejetée..."></textarea>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Annuler</button>
                <form id="rejectForm" method="post" style="display: inline;">
                    <input type="hidden" name="reason" id="rejectReasonHidden">
                    <button type="submit" class="btn btn-danger">Rejeter</button>
                </form>
            </div>
        </div>
    </div>
</div>

<div class="modal fade" id="convertModal" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title">Convertir en prêt</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body">
                <div class="alert alert-warning">
                    <i class="fas fa-exclamation-triangle"></i>
                    <strong>Attention!</strong> Cette action va vérifier toutes les règles de gestion (client et admin) 
                    et créer un prêt effectif si tout est conforme.
                </div>
                Êtes-vous sûr de vouloir convertir cette réservation en prêt ?
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Annuler</button>
                <form id="convertForm" method="post" style="display: inline;">
                    <button type="submit" class="btn btn-primary">Convertir en prêt</button>
                </form>
            </div>
        </div>
    </div>
</div>

<script>
function approveReservation(reservationId) {
    document.getElementById('approveForm').action = '/reservations/admin/' + reservationId + '/approuver';
    new bootstrap.Modal(document.getElementById('approveModal')).show();
}

function rejectReservation(reservationId) {
    document.getElementById('rejectForm').action = '/reservations/admin/' + reservationId + '/rejeter';
    new bootstrap.Modal(document.getElementById('rejectModal')).show();
}

function convertToPret(reservationId) {
    document.getElementById('convertForm').action = '/reservations/admin/' + reservationId + '/convertir-pret';
    new bootstrap.Modal(document.getElementById('convertModal')).show();
}

function cancelReservation(reservationId) {
    if (confirm('Êtes-vous sûr de vouloir annuler cette réservation ?')) {
        const form = document.createElement('form');
        form.method = 'POST';
        form.action = '/reservations/' + reservationId + '/annuler';
        document.body.appendChild(form);
        form.submit();
    }
}

function refreshData() {
    window.location.reload();
}

function approveAllPending() {
    if (confirm('Êtes-vous sûr de vouloir approuver TOUTES les réservations en attente ? Cette action ne peut pas être annulée.')) {
        // This would need to be implemented as a bulk operation endpoint
        alert('Fonctionnalité à implémenter : approbation en masse');
    }
}

function showFilterOptions() {
    alert('Fonctionnalité à implémenter : options de filtrage avancées');
}

function exportData() {
    alert('Fonctionnalité à implémenter : export des données');
}

function showSettings() {
    alert('Fonctionnalité à implémenter : paramètres de gestion');
}

// Handle reject reason
document.getElementById('rejectModal').addEventListener('show.bs.modal', function() {
    document.getElementById('rejectReason').value = '';
});

document.getElementById('rejectForm').addEventListener('submit', function() {
    const reason = document.getElementById('rejectReason').value;
    document.getElementById('rejectReasonHidden').value = reason || 'Rejeté par l\'administrateur';
});

// Auto-refresh every 60 seconds
setTimeout(function() {
    window.location.reload();
}, 60000);

// Show loading state for action buttons
document.querySelectorAll('.action-btn').forEach(btn => {
    btn.addEventListener('click', function() {
        if (this.tagName === 'BUTTON') {
            const originalHtml = this.innerHTML;
            this.innerHTML = '<i class="fas fa-spinner fa-spin"></i>';
            this.disabled = true;
            
            setTimeout(() => {
                this.innerHTML = originalHtml;
                this.disabled = false;
            }, 3000);
        }
    });
});
</script>
