<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!-- Page Title -->
<c:set var="pageTitle" value="Demandes de Prolongement" scope="request"/>

<!-- Custom CSS for prolongement requests view -->
<style>
    .page-header {
        background: linear-gradient(135deg, #34495e 0%, #2c3e50 100%);
        color: white;
        padding: 2rem 0;
        margin-bottom: 2rem;
    }
    .admin-info {
        background: rgba(255,255,255,0.1);
        border-radius: 10px;
        padding: 1rem;
        margin-bottom: 1rem;
    }
    .request-card {
        transition: transform 0.2s;
        border: none;
        box-shadow: 0 2px 10px rgba(0,0,0,0.1);
        margin-bottom: 1rem;
    }
    .request-card:hover {
        transform: translateY(-2px);
        box-shadow: 0 4px 20px rgba(0,0,0,0.15);
    }
    .status-badge {
        font-size: 0.75rem;
    }
    .urgent {
        border-left: 4px solid #dc3545;
    }
    .normal {
        border-left: 4px solid #17a2b8;
    }
    .action-btn {
        margin: 0.2rem;
    }
    .validation-warning {
        background-color: #fff3cd;
        border: 1px solid #ffeaa7;
        border-radius: 8px;
        padding: 1rem;
        margin-bottom: 1rem;
    }
</style>

<!-- Page Header -->
<div class="page-header">
    <div class="container">
        <div class="row align-items-center">
            <div class="col">
                <div class="admin-info">
                    <div class="d-flex align-items-center">
                        <i class="fas fa-user-shield fa-2x me-3"></i>
                        <div>
                            <h6 class="mb-0">Administrateur</h6>
                            <small>Gestion des demandes de prolongement</small>
                        </div>
                    </div>
                </div>
                <h1><i class="fas fa-clock"></i> Demandes de Prolongement</h1>
                <p class="mb-0">Approuver ou rejeter les demandes d'extension de prêt</p>
            </div>
            <div class="col-auto">
                <a href="/prets/actifs" class="btn btn-light me-2">
                    <i class="fas fa-book-reader"></i> Prêts actifs
                </a>
                <a href="/prets/demandes" class="btn btn-outline-light me-2">
                    <i class="fas fa-hourglass-half"></i> Demandes d'emprunt
                </a>
                <a href="/admin/dashboard" class="btn btn-outline-light me-2">
                    <i class="fas fa-tachometer-alt"></i> Tableau de bord
                </a>
                <form action="/logout" method="post" style="display: inline;">
                    <button type="submit" class="btn btn-light">
                        <i class="fas fa-sign-out-alt"></i> Se déconnecter
                    </button>
                </form>
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

    <!-- Pending Prolongement Requests -->
    <c:choose>
        <c:when test="${not empty pendingRequests}">
            <div class="row">
                <c:forEach var="prolongement" items="${pendingRequests}">
                    <div class="col-lg-6 mb-3">
                        <c:set var="pret" value="${prolongement.pret}" />
                        <c:set var="adherent" value="${pret.adherent}" />
                        <c:set var="livre" value="${pret.exemplaire.livre}" />
                        
                        <!-- Check if request is urgent (due date passed) -->
                        <c:set var="isUrgent" value="${prolongement.dateProlongementAsDate.time > pret.dateFinAsDate.time}" />
                        <c:set var="cardClass" value="${isUrgent ? 'urgent' : 'normal'}" />
                        
                        <div class="card request-card ${cardClass}">
                            <div class="card-body">
                                <div class="d-flex justify-content-between align-items-start mb-2">
                                    <h5 class="card-title mb-0">${livre.titre}</h5>
                                    <c:choose>
                                        <c:when test="${isUrgent}">
                                            <span class="badge bg-danger status-badge">
                                                <i class="fas fa-exclamation-triangle"></i> Urgent
                                            </span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="badge bg-info status-badge">
                                                <i class="fas fa-clock"></i> En attente
                                            </span>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                                
                                <p class="card-text">
                                    <small class="text-muted">
                                        <i class="fas fa-user"></i> ${livre.auteur.nom}
                                    </small>
                                </p>
                                
                                <div class="row">
                                    <div class="col-6">
                                        <small class="text-muted">
                                            <i class="fas fa-user-circle"></i> Adhérent:
                                        </small>
                                        <br>
                                        <strong>${adherent.prenom} ${adherent.nom}</strong>
                                        <br>
                                        <small class="text-muted">N° ${adherent.numeroAdherent}</small>
                                        <br>
                                        <small class="text-muted">Type: ${adherent.typeAdherent.libelle}</small>
                                    </div>
                                    <div class="col-6">
                                        <small class="text-muted">
                                            <i class="fas fa-calendar-plus"></i> Demande le:
                                        </small>
                                        <br>
                                        <fmt:formatDate value="${prolongement.dateProlongementAsDate}" pattern="dd/MM/yyyy HH:mm"/>
                                        <br>
                                        <small class="text-muted">
                                            <i class="fas fa-calendar-times"></i> Date échéance:
                                            <fmt:formatDate value="${pret.dateFinAsDate}" pattern="dd/MM/yyyy"/>
                                        </small>
                                    </div>
                                </div>
                                
                                <div class="mt-3">
                                    <small class="text-muted">
                                        <i class="fas fa-calendar-check"></i> Nouvelle date proposée:
                                        <fmt:formatDate value="${prolongement.nouvelleDateRetourAsDate}" pattern="dd/MM/yyyy"/>
                                    </small>
                                    <br>
                                    <small class="text-muted">
                                        <i class="fas fa-copy"></i> Exemplaire #${pret.exemplaire.id}
                                        <span class="ms-3">
                                            <i class="fas fa-bookmark"></i> ${pret.typePret.libelle}
                                        </span>
                                    </small>
                                </div>
                                
                                <!-- Validation warnings -->
                                <c:if test="${isUrgent}">
                                    <div class="validation-warning mt-3">
                                        <h6><i class="fas fa-exclamation-triangle text-danger"></i> Attention !</h6>
                                        <small>Cette demande a été faite après la date d'échéance du prêt. Vérifiez les pénalités avant d'approuver.</small>
                                    </div>
                                </c:if>
                            </div>
                            
                            <div class="card-footer bg-transparent">
                                <div class="d-flex flex-wrap justify-content-between">
                                    <div>
                                        <button type="button" class="btn btn-success btn-sm action-btn" 
                                                data-bs-toggle="modal" data-bs-target="#approveModal${prolongement.id}">
                                            <i class="fas fa-check"></i> Approuver
                                        </button>
                                        <button type="button" class="btn btn-danger btn-sm action-btn" 
                                                data-bs-toggle="modal" data-bs-target="#rejectModal${prolongement.id}">
                                            <i class="fas fa-times"></i> Rejeter
                                        </button>
                                    </div>
                                    <div>
                                        <a href="/adherents/${adherent.id}" class="btn btn-outline-primary btn-sm action-btn">
                                            <i class="fas fa-user"></i> Voir adhérent
                                        </a>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- Approve Modal -->
                    <div class="modal fade" id="approveModal${prolongement.id}" tabindex="-1">
                        <div class="modal-dialog">
                            <div class="modal-content">
                                <div class="modal-header">
                                    <h5 class="modal-title">Approuver la demande</h5>
                                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                                </div>
                                <div class="modal-body">
                                    <div class="text-center mb-4">
                                        <i class="fas fa-check-circle fa-3x text-success mb-3"></i>
                                        <h5>Approuver cette demande de prolongement ?</h5>
                                        <p>Livre: "<strong>${livre.titre}</strong>"</p>
                                        <p>Adhérent: <strong>${adherent.prenom} ${adherent.nom}</strong></p>
                                    </div>
                                    
                                    <div class="alert alert-info">
                                        <h6><i class="fas fa-info-circle"></i> Nouvelle date de retour:</h6>
                                        <strong><fmt:formatDate value="${prolongement.nouvelleDateRetourAsDate}" pattern="dd/MM/yyyy"/></strong>
                                    </div>
                                    
                                    <c:if test="${isUrgent}">
                                        <div class="alert alert-warning">
                                            <h6><i class="fas fa-exclamation-triangle"></i> Attention !</h6>
                                            <small>Cette demande a été faite après l'échéance. Assurez-vous que les pénalités ont été traitées.</small>
                                        </div>
                                    </c:if>
                                </div>
                                <div class="modal-footer">
                                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">
                                        <i class="fas fa-arrow-left"></i> Annuler
                                    </button>
                                    <form action="/prets/prolongements/${prolongement.id}/approve" method="post" style="display: inline;">
                                        <button type="submit" class="btn btn-success">
                                            <i class="fas fa-check"></i> Confirmer l'approbation
                                        </button>
                                    </form>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- Reject Modal -->
                    <div class="modal fade" id="rejectModal${prolongement.id}" tabindex="-1">
                        <div class="modal-dialog">
                            <div class="modal-content">
                                <div class="modal-header">
                                    <h5 class="modal-title">Rejeter la demande</h5>
                                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                                </div>
                                <div class="modal-body">
                                    <div class="text-center mb-4">
                                        <i class="fas fa-times-circle fa-3x text-danger mb-3"></i>
                                        <h5>Rejeter cette demande de prolongement ?</h5>
                                        <p>Livre: "<strong>${livre.titre}</strong>"</p>
                                        <p>Adhérent: <strong>${adherent.prenom} ${adherent.nom}</strong></p>
                                    </div>
                                    
                                    <div class="alert alert-warning">
                                        <h6><i class="fas fa-info-circle"></i> Conséquences du rejet:</h6>
                                        <ul class="mb-0">
                                            <li>L'adhérent sera notifié du rejet</li>
                                            <li>La date d'échéance originale reste inchangée</li>
                                            <li>Des pénalités peuvent s'appliquer si le retour est en retard</li>
                                        </ul>
                                    </div>
                                </div>
                                <div class="modal-footer">
                                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">
                                        <i class="fas fa-arrow-left"></i> Annuler
                                    </button>
                                    <form action="/prets/prolongements/${prolongement.id}/reject" method="post" style="display: inline;">
                                        <button type="submit" class="btn btn-danger">
                                            <i class="fas fa-times"></i> Confirmer le rejet
                                        </button>
                                    </form>
                                </div>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </div>
        </c:when>
        <c:otherwise>
            <div class="text-center py-5">
                <i class="fas fa-clock fa-3x text-muted mb-3"></i>
                <h4 class="text-muted">Aucune demande de prolongement</h4>
                <p class="text-muted">Il n'y a actuellement aucune demande en attente.</p>
                <a href="/prets/actifs" class="btn btn-primary">
                    <i class="fas fa-book-reader"></i> Voir les prêts actifs
                </a>
            </div>
        </c:otherwise>
    </c:choose>

    <!-- Statistics -->
    <c:if test="${not empty pendingRequests}">
        <div class="row mt-4">
            <div class="col-md-4">
                <div class="card text-center">
                    <div class="card-body">
                        <h5 class="card-title">Total demandes</h5>
                        <h3 class="text-primary">${pendingRequests.size()}</h3>
                    </div>
                </div>
            </div>
            <div class="col-md-4">
                <div class="card text-center">
                    <div class="card-body">
                        <h5 class="card-title">Urgentes</h5>
                        <h3 class="text-danger">
                            <c:set var="urgentCount" value="0" />
                            <c:forEach var="prolongement" items="${pendingRequests}">
                                <c:if test="${prolongement.dateProlongementAsDate.time > prolongement.pret.dateFinAsDate.time}">
                                    <c:set var="urgentCount" value="${urgentCount + 1}" />
                                </c:if>
                            </c:forEach>
                            ${urgentCount}
                        </h3>
                    </div>
                </div>
            </div>
            <div class="col-md-4">
                <div class="card text-center">
                    <div class="card-body">
                        <h5 class="card-title">Normales</h5>
                        <h3 class="text-info">
                            ${pendingRequests.size() - urgentCount}
                        </h3>
                    </div>
                </div>
            </div>
        </div>
    </c:if>
</div>

<!-- Custom JavaScript -->
<script>
    // Auto-hide alerts after 5 seconds
    setTimeout(function() {
        let alerts = document.querySelectorAll('.alert');
        alerts.forEach(function(alert) {
            let bsAlert = new bootstrap.Alert(alert);
            bsAlert.close();
        });
    }, 5000);

    // Add loading state to form buttons
    document.querySelectorAll('form').forEach(form => {
        form.addEventListener('submit', function() {
            const submitBtn = form.querySelector('button[type="submit"]');
            if (submitBtn) {
                submitBtn.disabled = true;
                submitBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Traitement...';
            }
        });
    });
</script>
