<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!-- Page Title -->
<c:set var="pageTitle" value="Mes Emprunts" scope="request"/>

<!-- Custom CSS for loans view -->
<style>
    .page-header {
        background: linear-gradient(135deg, #dc3545 0%, #fd7e14 100%);
        color: white;
        padding: 2rem 0;
        margin-bottom: 2rem;
    }
    .client-info {
        background: rgba(255,255,255,0.1);
        border-radius: 10px;
        padding: 1rem;
        margin-bottom: 1rem;
    }
    .loan-card {
        transition: transform 0.2s;
        border: none;
        box-shadow: 0 2px 10px rgba(0,0,0,0.1);
        margin-bottom: 1rem;
    }
    .loan-card:hover {
        transform: translateY(-2px);
        box-shadow: 0 4px 20px rgba(0,0,0,0.15);
    }
    .status-badge {
        font-size: 0.75rem;
    }
    .overdue {
        border-left: 4px solid #dc3545;
    }
    .due-soon {
        border-left: 4px solid #ffc107;
    }
    .normal {
        border-left: 4px solid #28a745;
    }
    .return-btn {
        background: linear-gradient(135deg, #dc3545 0%, #fd7e14 100%);
        border: none;
        border-radius: 8px;
        transition: all 0.3s ease;
    }
    .return-btn:hover {
        transform: translateY(-2px);
        box-shadow: 0 4px 15px rgba(220,53,69,0.3);
    }
    .extension-btn {
        background: linear-gradient(135deg, #17a2b8 0%, #138496 100%);
        border: none;
        border-radius: 8px;
        transition: all 0.3s ease;
        color: white;
    }
    .extension-btn:hover {
        transform: translateY(-2px);
        box-shadow: 0 4px 15px rgba(23,162,184,0.3);
        color: white;
    }
</style>

<!-- Page Header -->
<div class="page-header">
    <div class="container">
        <div class="row align-items-center">
            <div class="col">
                <div class="client-info">
                    <div class="d-flex align-items-center">
                        <i class="fas fa-user-circle fa-2x me-3"></i>
                        <div>
                            <h6 class="mb-0">Client connecté</h6>
                            <small>N° ${currentUser.numeroClient}</small>
                        </div>
                    </div>
                </div>
                <h1><i class="fas fa-book-reader"></i> Mes Emprunts</h1>
                <p class="mb-0">Gérez vos livres empruntés</p>
            </div>
            <div class="col-auto">
                <a href="/client/livres" class="btn btn-light me-2">
                    <i class="fas fa-book"></i> Catalogue
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

    <!-- Active Loans -->
    <c:choose>
        <c:when test="${not empty activeLoans}">
            <div class="row">
                <c:forEach var="pret" items="${activeLoans}">
                    <div class="col-lg-6 mb-3">
                        <c:set var="daysDiff" value="${pret.daysDifference}" />
                        <c:set var="cardClass" value="normal" />
                        <c:if test="${daysDiff < 0}">
                            <c:set var="cardClass" value="overdue" />
                        </c:if>
                        <c:if test="${daysDiff >= 0 && daysDiff <= 2}">
                            <c:set var="cardClass" value="due-soon" />
                        </c:if>
                        
                        <div class="card loan-card ${cardClass}">
                            <div class="card-body">
                                <div class="d-flex justify-content-between align-items-start mb-2">
                                    <h5 class="card-title mb-0">${pret.exemplaire.livre.titre}</h5>
                                    <div class="d-flex flex-column align-items-end">
                                        <c:choose>
                                            <c:when test="${daysDiff < 0}">
                                                <span class="badge bg-danger status-badge">
                                                    En retard (${Math.abs(daysDiff)} jour(s))
                                                </span>
                                            </c:when>
                                            <c:when test="${daysDiff >= 0 && daysDiff <= 2}">
                                                <span class="badge bg-warning status-badge">
                                                    À rendre dans ${daysDiff} jour(s)
                                                </span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge bg-success status-badge">
                                                    ${daysDiff} jour(s) restant(s)
                                                </span>
                                            </c:otherwise>
                                        </c:choose>
                                        <c:if test="${pendingExtensions[pret.id]}">
                                            <span class="badge bg-info status-badge mt-1">
                                                <i class="fas fa-hourglass-half"></i> Prolongement demandé
                                            </span>
                                        </c:if>
                                    </div>
                                </div>
                                
                                <p class="card-text">
                                    <small class="text-muted">
                                        <i class="fas fa-user"></i> ${pret.exemplaire.livre.auteur.nom}
                                    </small>
                                </p>
                                
                                <div class="row">
                                    <div class="col-6">
                                        <small class="text-muted">
                                            <i class="fas fa-calendar-plus"></i> Emprunté le:
                                        </small>
                                        <br>
                                        <fmt:formatDate value="${pret.dateDebutAsDate}" pattern="dd/MM/yyyy"/>
                                    </div>
                                    <div class="col-6">
                                        <small class="text-muted">
                                            <i class="fas fa-calendar-times"></i> À rendre le:
                                        </small>
                                        <br>
                                        <fmt:formatDate value="${pret.dateFinAsDate}" pattern="dd/MM/yyyy"/>
                                    </div>
                                </div>
                                
                                <div class="mt-3">
                                    <small class="text-muted">
                                        <i class="fas fa-copy"></i> Exemplaire #${pret.exemplaire.id}
                                        <span class="ms-3">
                                            <i class="fas fa-bookmark"></i> ${pret.typePret.libelle}
                                        </span>
                                    </small>
                                </div>
                            </div>
                            
                            <div class="card-footer bg-transparent">
                                <div class="row g-2">
                                    <div class="col-6">
                                        <c:choose>
                                            <c:when test="${pendingExtensions[pret.id]}">
                                                <button type="button" class="btn btn-secondary w-100" disabled 
                                                        title="Une demande de prolongement est déjà en cours">
                                                    <i class="fas fa-hourglass-half"></i> En attente
                                                </button>
                                            </c:when>
                                            <c:otherwise>
                                                <button type="button" class="btn extension-btn w-100" 
                                                        data-bs-toggle="modal" data-bs-target="#extensionModal${pret.id}">
                                                    <i class="fas fa-clock"></i> Prolonger
                                                </button>
                                            </c:otherwise>
                                        </c:choose>
                                    </div>
                                    <div class="col-6">
                                        <button type="button" class="btn btn-danger return-btn w-100" 
                                                data-bs-toggle="modal" data-bs-target="#returnModal${pret.id}">
                                            <i class="fas fa-undo"></i> Rendre
                                        </button>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- Extension Request Modal (only if no pending request) -->
                    <c:if test="${not pendingExtensions[pret.id]}">
                    <div class="modal fade" id="extensionModal${pret.id}" tabindex="-1">
                        <div class="modal-dialog">
                            <div class="modal-content">
                                <div class="modal-header">
                                    <h5 class="modal-title">Demande de prolongement</h5>
                                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                                </div>
                                <form action="/client/prolongement" method="post">
                                    <div class="modal-body">
                                        <div class="text-center mb-4">
                                            <i class="fas fa-clock fa-3x text-info mb-3"></i>
                                            <h5>Prolonger la durée d'emprunt ?</h5>
                                            <p>Livre: "<strong>${pret.exemplaire.livre.titre}</strong>"</p>
                                            <p>Exemplaire: <strong>#${pret.exemplaire.id}</strong></p>
                                        </div>
                                        
                                        <input type="hidden" name="pretId" value="${pret.id}">
                                        
                                        <!-- Current loan info -->
                                        <div class="alert alert-info">
                                            <h6><i class="fas fa-info-circle"></i> Informations actuelles:</h6>
                                            <small>
                                                Date d'emprunt: <fmt:formatDate value="${pret.dateDebutAsDate}" pattern="dd/MM/yyyy"/><br>
                                                Date de retour prévue: <fmt:formatDate value="${pret.dateFinAsDate}" pattern="dd/MM/yyyy"/>
                                                <c:if test="${daysDiff < 0}">
                                                    <br><span class="text-danger">En retard de ${Math.abs(daysDiff)} jour(s)</span>
                                                </c:if>
                                                <c:if test="${daysDiff >= 0}">
                                                    <br><span class="text-success">${daysDiff} jour(s) restant(s)</span>
                                                </c:if>
                                            </small>
                                        </div>
                                        
                                        <div class="alert alert-success">
                                            <h6><i class="fas fa-calendar-plus"></i> Prolongement automatique:</h6>
                                            <small>
                                                La durée de prolongement sera déterminée automatiquement selon votre type d'adhérent.
                                            </small>
                                        </div>
                                        
                                        <div class="alert alert-warning">
                                            <small>
                                                <i class="fas fa-exclamation-triangle"></i>
                                                Cette demande sera examinée par un administrateur. Vous recevrez une réponse dans les plus brefs délais.
                                            </small>
                                        </div>
                                    </div>
                                    <div class="modal-footer">
                                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">
                                            <i class="fas fa-times"></i> Annuler
                                        </button>
                                        <button type="submit" class="btn btn-info">
                                            <i class="fas fa-paper-plane"></i> Envoyer la demande
                                        </button>
                                    </div>
                                </form>
                            </div>
                        </div>
                    </div>
                    </c:if>

                    <!-- Return Book Modal -->
                    <div class="modal fade" id="returnModal${pret.id}" tabindex="-1">
                        <div class="modal-dialog">
                            <div class="modal-content">
                                <div class="modal-header">
                                    <h5 class="modal-title">Retour de livre</h5>
                                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                                </div>
                                <form action="/client/retour" method="post">
                                    <div class="modal-body">
                                        <div class="text-center mb-4">
                                            <i class="fas fa-undo fa-3x text-danger mb-3"></i>
                                            <h5>Rendre ce livre ?</h5>
                                            <p>Livre: "<strong>${pret.exemplaire.livre.titre}</strong>"</p>
                                            <p>Exemplaire: <strong>#${pret.exemplaire.id}</strong></p>
                                        </div>
                                        
                                        <input type="hidden" name="pretId" value="${pret.id}">
                                        
                                        <!-- Date de retour (pour simulation) -->
                                        <div class="mb-3">
                                            <label for="dateRetour${pret.id}" class="form-label">
                                                <i class="fas fa-calendar"></i> Date de retour (simulation):
                                            </label>
                                            <input type="date" class="form-control" id="dateRetour${pret.id}" 
                                                   name="dateRetour" required
                                                   value="<fmt:formatDate value='${currentDate}' pattern='yyyy-MM-dd'/>">
                                            <small class="form-text text-muted">
                                                Choisissez la date pour simuler le retour
                                            </small>
                                        </div>
                                        
                                        <!-- Warning for late return -->
                                        <c:if test="${daysDiff < 0}">
                                            <div class="alert alert-warning">
                                                <h6><i class="fas fa-exclamation-triangle"></i> Retour en retard!</h6>
                                                <small>
                                                    Ce livre est en retard de ${Math.abs(daysDiff)} jour(s). 
                                                    Une pénalité pourrait être appliquée.
                                                </small>
                                            </div>
                                        </c:if>
                                        
                                        <div class="alert alert-info">
                                            <small>
                                                <i class="fas fa-info-circle"></i>
                                                Date limite de retour: <fmt:formatDate value="${pret.dateFinAsDate}" pattern="dd/MM/yyyy"/>
                                            </small>
                                        </div>
                                    </div>
                                    <div class="modal-footer">
                                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">
                                            <i class="fas fa-times"></i> Annuler
                                        </button>
                                        <button type="submit" class="btn btn-danger">
                                            <i class="fas fa-check"></i> Confirmer le retour
                                        </button>
                                    </div>
                                </form>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </div>
        </c:when>
        <c:otherwise>
            <div class="text-center py-5">
                <i class="fas fa-book-reader fa-3x text-muted mb-3"></i>
                <h4 class="text-muted">Aucun emprunt en cours</h4>
                <p class="text-muted">Vous n'avez aucun livre emprunté actuellement.</p>
                <a href="/client/livres" class="btn btn-success">
                    <i class="fas fa-book"></i> Parcourir le catalogue
                </a>
            </div>
        </c:otherwise>
    </c:choose>

    <!-- Statistics -->
    <c:if test="${not empty activeLoans}">
        <div class="row mt-4">
            <div class="col-md-4">
                <div class="card text-center">
                    <div class="card-body">
                        <h5 class="card-title">Total emprunts</h5>
                        <h3 class="text-primary">${activeLoans.size()}</h3>
                    </div>
                </div>
            </div>
            <div class="col-md-4">
                <div class="card text-center">
                    <div class="card-body">
                        <h5 class="card-title">En retard</h5>
                        <h3 class="text-danger">
                            <c:set var="overdueCount" value="0" />
                            <c:forEach var="pret" items="${activeLoans}">
                                <c:if test="${pret.daysDifference < 0}">
                                    <c:set var="overdueCount" value="${overdueCount + 1}" />
                                </c:if>
                            </c:forEach>
                            ${overdueCount}
                        </h3>
                    </div>
                </div>
            </div>
            <div class="col-md-4">
                <div class="card text-center">
                    <div class="card-body">
                        <h5 class="card-title">À rendre bientôt</h5>
                        <h3 class="text-warning">
                            <c:set var="dueSoonCount" value="0" />
                            <c:forEach var="pret" items="${activeLoans}">
                                <c:if test="${pret.daysDifference >= 0 && pret.daysDifference <= 2}">
                                    <c:set var="dueSoonCount" value="${dueSoonCount + 1}" />
                                </c:if>
                            </c:forEach>
                            ${dueSoonCount}
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

    // Add loading state to return buttons
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
