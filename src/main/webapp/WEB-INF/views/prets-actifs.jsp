<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!-- Page Title -->
<c:set var="pageTitle" value="Prêts Actifs" scope="request"/>

<!-- Custom CSS for loans management view -->
<style>
    .page-header {
        background: linear-gradient(135deg, #dc3545 0%, #fd7e14 100%);
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
    .action-btn {
        margin: 0.2rem;
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
                            <small>Gestion des prêts actifs</small>
                        </div>
                    </div>
                </div>
                <h1><i class="fas fa-book-reader"></i> Prêts Actifs</h1>
                <p class="mb-0">Visualisation et gestion des prêts en cours</p>
            </div>
            <div class="col-auto">
                <a href="/prets/prolongements" class="btn btn-light me-2">
                    <i class="fas fa-clock"></i> Demandes de prolongement
                </a>
                <a href="/prets/demandes" class="btn btn-light me-2">
                    <i class="fas fa-hourglass-half"></i> Demandes en attente
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
                                </div>
                                
                                <p class="card-text">
                                    <small class="text-muted">
                                        <i class="fas fa-user"></i> ${pret.exemplaire.livre.auteur.nom}
                                    </small>
                                </p>
                                
                                <div class="row">
                                    <div class="col-6">
                                        <small class="text-muted">
                                            <i class="fas fa-user-circle"></i> Adhérent:
                                        </small>
                                        <br>
                                        <strong>${pret.adherent.prenom} ${pret.adherent.nom}</strong>
                                        <br>
                                        <small class="text-muted">N° ${pret.adherent.numeroAdherent}</small>
                                    </div>
                                    <div class="col-6">
                                        <small class="text-muted">
                                            <i class="fas fa-calendar-plus"></i> Emprunté le:
                                        </small>
                                        <br>
                                        <fmt:formatDate value="${pret.dateDebutAsDate}" pattern="dd/MM/yyyy"/>
                                        <br>
                                        <small class="text-muted">
                                            <i class="fas fa-calendar-times"></i> À rendre le:
                                            <fmt:formatDate value="${pret.dateFinAsDate}" pattern="dd/MM/yyyy"/>
                                        </small>
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
                                <div class="d-flex flex-wrap">
                                    <button type="button" class="btn btn-success btn-sm action-btn" 
                                            data-bs-toggle="modal" data-bs-target="#returnModal${pret.id}">
                                        <i class="fas fa-undo"></i> Marquer comme rendu
                                    </button>
                                    <a href="/adherents/${pret.adherent.id}" class="btn btn-outline-primary btn-sm action-btn">
                                        <i class="fas fa-user"></i> Voir adhérent
                                    </a>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- Return Book Modal -->
                    <div class="modal fade" id="returnModal${pret.id}" tabindex="-1">
                        <div class="modal-dialog">
                            <div class="modal-content">
                                <div class="modal-header">
                                    <h5 class="modal-title">Retour de livre</h5>
                                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                                </div>
                                <form action="/prets/${pret.id}/return" method="post">
                                    <div class="modal-body">
                                        <div class="text-center mb-4">
                                            <i class="fas fa-undo fa-3x text-success mb-3"></i>
                                            <h5>Marquer ce livre comme rendu ?</h5>
                                            <p>Livre: "<strong>${pret.exemplaire.livre.titre}</strong>"</p>
                                            <p>Adhérent: <strong>${pret.adherent.prenom} ${pret.adherent.nom}</strong></p>
                                        </div>
                                        
                                        <div class="mb-3">
                                            <label for="returnDate${pret.id}" class="form-label">
                                                <i class="fas fa-calendar"></i> Date de retour:
                                            </label>
                                            <input type="date" class="form-control" id="returnDate${pret.id}" 
                                                   name="returnDate" required
                                                   value="<fmt:formatDate value='${currentDate}' pattern='yyyy-MM-dd'/>">
                                        </div>
                                        
                                        <c:if test="${daysDiff < 0}">
                                            <div class="alert alert-warning">
                                                <h6><i class="fas fa-exclamation-triangle"></i> Retour en retard!</h6>
                                                <small>
                                                    Ce livre était attendu depuis ${Math.abs(daysDiff)} jour(s). 
                                                    Une pénalité sera automatiquement appliquée.
                                                </small>
                                            </div>
                                        </c:if>
                                        
                                        <div class="alert alert-info">
                                            <small>
                                                <i class="fas fa-info-circle"></i>
                                                Le livre sera marqué comme disponible après validation.
                                            </small>
                                        </div>
                                    </div>
                                    <div class="modal-footer">
                                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">
                                            <i class="fas fa-times"></i> Annuler
                                        </button>
                                        <button type="submit" class="btn btn-success">
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
                <h4 class="text-muted">Aucun prêt actif</h4>
                <p class="text-muted">Il n'y a actuellement aucun prêt en cours.</p>
                <a href="/prets/demandes" class="btn btn-primary">
                    <i class="fas fa-clock"></i> Voir les demandes en attente
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
                        <h5 class="card-title">Total prêts actifs</h5>
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
