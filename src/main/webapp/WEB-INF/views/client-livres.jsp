<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!-- Page Title -->
<c:set var="pageTitle" value="Catalogue des Livres" scope="request"/>

<!-- Custom CSS for client view -->
<style>
    .page-header {
        background: linear-gradient(135deg, #28a745 0%, #20c997 100%);
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
    .book-card {
        transition: transform 0.2s;
        border: none;
        box-shadow: 0 2px 10px rgba(0,0,0,0.1);
        height: 100%;
    }
    .book-card:hover {
        transform: translateY(-5px);
        box-shadow: 0 4px 20px rgba(0,0,0,0.15);
    }
    .search-box {
        background: white;
        border-radius: 10px;
        padding: 1.5rem;
        box-shadow: 0 2px 10px rgba(0,0,0,0.1);
        margin-bottom: 2rem;
    }
    .genre-badge {
        font-size: 0.75rem;
        margin: 0.2rem;
    }
    .available-badge {
        position: absolute;
        top: 10px;
        right: 10px;
        z-index: 1;
    }
    .book-card-body {
        position: relative;
    }
    .borrow-btn {
        background: linear-gradient(135deg, #28a745 0%, #20c997 100%);
        border: none;
        border-radius: 8px;
        transition: all 0.3s ease;
    }
    .borrow-btn:hover {
        transform: translateY(-2px);
        box-shadow: 0 4px 15px rgba(40,167,69,0.3);
    }
    .borrow-btn:disabled {
        background: #6c757d;
        cursor: not-allowed;
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
                <h1><i class="fas fa-book"></i> Catalogue des Livres</h1>
                <p class="mb-0">
                    Découvrez et empruntez nos livres
                    <small class="ms-3">
                        <i class="fas fa-flask"></i> Mode test activé - dates flexibles pour simulation
                    </small>
                </p>
            </div>
            <div class="col-auto">
                <a href="/client/mes-emprunts" class="btn btn-outline-light me-2">
                    <i class="fas fa-book-reader"></i> Mes Emprunts
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

    <!-- Search Box -->
    <div class="search-box">
        <form action="/client/livres/search" method="get" class="row g-3">
            <div class="col-md-9">
                <input type="text" class="form-control" name="searchTerm" 
                       placeholder="Rechercher par titre ou nom d'auteur..." 
                       value="${searchTerm}">
            </div>
            <div class="col-md-3">
                <button type="submit" class="btn btn-success w-100">
                    <i class="fas fa-search"></i> Rechercher
                </button>
            </div>
        </form>
        <c:if test="${not empty searchTerm}">
            <div class="mt-2">
                <small class="text-muted">
                    Résultats de recherche pour: "<strong>${searchTerm}</strong>"
                    <a href="/client/livres" class="ms-2">Voir tous les livres</a>
                </small>
            </div>
        </c:if>
    </div>

    <!-- Books Grid -->
    <div class="row">
        <c:forEach var="livre" items="${livres}">
            <div class="col-lg-4 col-md-6 mb-4">
                <div class="card book-card">
                    <div class="book-card-body">
                        <!-- Availability badge -->
                        <c:choose>
                            <c:when test="${livre.exemplaires != null && livre.exemplaires.size() > 0}">
                                <span class="badge bg-success available-badge">
                                    ${livre.exemplaires.size()} disponible(s)
                                </span>
                            </c:when>
                            <c:otherwise>
                                <span class="badge bg-danger available-badge">Non disponible</span>
                            </c:otherwise>
                        </c:choose>
                    </div>
                    
                    <div class="card-body">
                        <h5 class="card-title text-success">${livre.titre}</h5>
                        <p class="card-text">
                            <small class="text-muted">
                                <i class="fas fa-user"></i> ${livre.auteur.nom}
                            </small>
                        </p>
                        
                        <c:if test="${not empty livre.resume}">
                            <p class="card-text">
                                <c:choose>
                                    <c:when test="${livre.resume.length() > 100}">
                                        ${livre.resume.substring(0, 100)}...
                                    </c:when>
                                    <c:otherwise>
                                        ${livre.resume}
                                    </c:otherwise>
                                </c:choose>
                            </p>
                        </c:if>

                        <p class="card-text">
                            <small class="text-muted">
                                <i class="fas fa-calendar"></i> 
                                <fmt:formatDate value="${livre.dateSortieAsDate}" pattern="dd/MM/yyyy"/>
                            </small>
                        </p>

                        <!-- Genres -->
                        <c:if test="${not empty livre.genres}">
                            <div class="mb-2">
                                <c:forEach var="genre" items="${livre.genres}">
                                    <span class="badge bg-info genre-badge">${genre.libelle}</span>
                                </c:forEach>
                            </div>
                        </c:if>

                        <!-- Constraints warning -->
                        <c:if test="${not empty livre.contraintes}">
                            <div class="mb-2">
                                <c:forEach var="contrainte" items="${livre.contraintes}">
                                    <span class="badge bg-warning text-dark genre-badge">
                                        <i class="fas fa-exclamation-triangle"></i> ${contrainte.typeContrainte}
                                    </span>
                                </c:forEach>
                            </div>
                        </c:if>

                        <!-- Available copies -->
                        <p class="card-text">
                            <small class="text-muted">
                                <i class="fas fa-copy"></i> 
                                ${livre.exemplaires != null ? livre.exemplaires.size() : 0} exemplaire(s) disponible(s)
                            </small>
                        </p>
                    </div>
                    
                    <div class="card-footer bg-transparent">
                        <c:choose>
                            <c:when test="${livre.exemplaires != null && livre.exemplaires.size() > 0}">
                                <button type="button" class="btn btn-success borrow-btn w-100" 
                                        data-bs-toggle="modal" data-bs-target="#borrowModal${livre.id}">
                                    <i class="fas fa-hand-holding"></i> Emprunter ce livre
                                </button>
                            </c:when>
                            <c:otherwise>
                                <button type="button" class="btn btn-secondary w-100" disabled>
                                    <i class="fas fa-times"></i> Non disponible
                                </button>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </div>

            <!-- Borrow Confirmation Modal -->
            <c:if test="${livre.exemplaires != null && livre.exemplaires.size() > 0}">
                <div class="modal fade" id="borrowModal${livre.id}" tabindex="-1">
                    <div class="modal-dialog">
                        <div class="modal-content">
                            <div class="modal-header">
                                <h5 class="modal-title">Demande d'emprunt</h5>
                                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                            </div>
                            <form action="/prets/request" method="post">
                                <div class="modal-body">
                                    <div class="text-center mb-4">
                                        <i class="fas fa-book fa-3x text-success mb-3"></i>
                                        <h5>Demander l'emprunt de ce livre ?</h5>
                                        <p>Livre: "<strong>${livre.titre}</strong>"</p>
                                        <p>Auteur: <strong>${livre.auteur.nom}</strong></p>
                                    </div>
                                    
                                    <!-- Exemplaire Selection -->
                                    <div class="mb-3">
                                        <label for="exemplaireId${livre.id}" class="form-label">
                                            <i class="fas fa-copy"></i> Choisir un exemplaire:
                                        </label>
                                        <select class="form-select" id="exemplaireId${livre.id}" name="exemplaireId" required>
                                            <option value="">Sélectionner un exemplaire...</option>
                                            <c:forEach var="exemplaire" items="${livre.exemplaires}">
                                                <option value="${exemplaire.id}">
                                                    Exemplaire #${exemplaire.id}
                                                </option>
                                            </c:forEach>
                                        </select>
                                    </div>
                                    
                                    <!-- Type de Prêt Selection -->
                                    <div class="mb-3">
                                        <label for="typePretId${livre.id}" class="form-label">
                                            <i class="fas fa-clock"></i> Type de prêt:
                                        </label>
                                        <select class="form-select" id="typePretId${livre.id}" name="typePretId" required>
                                            <option value="">Sélectionner le type de prêt...</option>
                                            <c:forEach var="typePret" items="${typesPret}">
                                                <option value="${typePret.id}">${typePret.libelle}</option>
                                            </c:forEach>
                                        </select>
                                        <small class="form-text text-muted">
                                            Votre durée d'emprunt dépend de votre type d'adhérent
                                        </small>
                                    </div>

                                    <!-- Date d'emprunt Selection -->
                                    <div class="mb-3">
                                        <label for="dateEmprunt${livre.id}" class="form-label">
                                            <i class="fas fa-calendar-plus"></i> Date d'emprunt:
                                        </label>
                                        <input type="date" class="form-control" id="dateEmprunt${livre.id}" 
                                               name="dateEmprunt" required
                                               value="<fmt:formatDate value='${currentDate}' pattern='yyyy-MM-dd'/>">
                                        <small class="form-text text-muted">
                                            <i class="fas fa-flask"></i> <strong>Mode test:</strong> Vous pouvez choisir n'importe quelle date pour simuler différents scénarios (emprunts passés, futurs, etc.)
                                        </small>
                                    </div>
                                    
                                    <!-- Show constraints if any -->
                                    <c:if test="${not empty livre.contraintes}">
                                        <div class="alert alert-warning">
                                            <h6><i class="fas fa-exclamation-triangle"></i> Contraintes d'emprunt:</h6>
                                            <c:forEach var="contrainte" items="${livre.contraintes}">
                                                <small class="d-block">• ${contrainte.typeContrainte}</small>
                                            </c:forEach>
                                        </div>
                                    </c:if>
                                    
                                    <div class="alert alert-info">
                                        <small>
                                            <i class="fas fa-info-circle"></i>
                                            Votre demande sera soumise pour validation par un administrateur.
                                            <br>
                                            <strong>Mode test:</strong> Vous pouvez choisir n'importe quelle date pour tester différents scénarios. La date de fin sera calculée automatiquement selon votre type d'adhérent une fois approuvée.
                                        </small>
                                    </div>
                                </div>
                                <div class="modal-footer">
                                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">
                                        <i class="fas fa-times"></i> Annuler
                                    </button>
                                    <button type="submit" class="btn btn-success">
                                        <i class="fas fa-paper-plane"></i> Envoyer la demande
                                    </button>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>
            </c:if>
        </c:forEach>
    </div>

    <!-- No books message -->
    <c:if test="${empty livres}">
        <div class="text-center py-5">
            <i class="fas fa-book-open fa-3x text-muted mb-3"></i>
            <h4 class="text-muted">Aucun livre trouvé</h4>
            <c:choose>
                <c:when test="${not empty searchTerm}">
                    <p class="text-muted">Aucun résultat pour votre recherche.</p>
                    <a href="/client/livres" class="btn btn-success">Voir tous les livres</a>
                </c:when>
                <c:otherwise>
                    <p class="text-muted">Le catalogue est temporairement vide.</p>
                </c:otherwise>
            </c:choose>
        </div>
    </c:if>

    <!-- Statistics for client -->
    <c:if test="${not empty livres}">
        <div class="row mt-4">
            <div class="col">
                <div class="card">
                    <div class="card-body text-center">
                        <h5 class="card-title">Catalogue</h5>
                        <p class="card-text">
                            <strong>${livres.size()}</strong> livre(s) 
                            <c:if test="${not empty searchTerm}">trouvé(s)</c:if>
                            <c:if test="${empty searchTerm}">disponible(s)</c:if>
                        </p>
                    </div>
                </div>
            </div>
        </div>
    </c:if>
</div>

<!-- Custom JavaScript for this page -->
<script>
    // Debug function to check if modals are working
    document.addEventListener('DOMContentLoaded', function() {
        console.log('Page loaded, checking modals...');
        
        // Check if Bootstrap is loaded
        if (typeof bootstrap !== 'undefined') {
            console.log('Bootstrap is loaded');
        } else {
            console.error('Bootstrap is not loaded!');
        }
        
        // Add event listeners to borrow buttons
        document.querySelectorAll('.borrow-btn').forEach(function(button) {
            button.addEventListener('click', function() {
                console.log('Borrow button clicked:', this.getAttribute('data-bs-target'));
            });
        });
    });

    // Auto-hide alerts after 5 seconds
    setTimeout(function() {
        let alerts = document.querySelectorAll('.alert');
        alerts.forEach(function(alert) {
            let bsAlert = new bootstrap.Alert(alert);
            bsAlert.close();
        });
    }, 5000);

    // Add loading state to borrow buttons
    document.querySelectorAll('form').forEach(form => {
        form.addEventListener('submit', function() {
            const submitBtn = form.querySelector('button[type="submit"]');
            if (submitBtn) {
                submitBtn.disabled = true;
                submitBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Traitement...';
            }
        });
    });

    // Add date validation and feedback for testing scenarios
    document.querySelectorAll('input[type="date"]').forEach(dateInput => {
        dateInput.addEventListener('change', function() {
            const selectedDate = new Date(this.value);
            const today = new Date();
            const diffTime = selectedDate - today;
            const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
            
            // Find the closest info alert to update
            const modal = this.closest('.modal');
            const infoAlert = modal.querySelector('.alert-info small');
            
            if (diffDays === 0) {
                infoAlert.innerHTML = '<i class="fas fa-info-circle"></i> <strong>Test:</strong> Emprunt commençant aujourd\'hui.<br><strong>Note:</strong> La date de fin sera calculée automatiquement selon votre type d\'adhérent une fois approuvée.';
            } else if (diffDays > 0) {
                infoAlert.innerHTML = '<i class="fas fa-info-circle"></i> <strong>Test:</strong> Emprunt futur commençant dans ' + diffDays + ' jour(s).<br><strong>Note:</strong> La date de fin sera calculée automatiquement selon votre type d\'adhérent une fois approuvée.';
            } else {
                infoAlert.innerHTML = '<i class="fas fa-flask text-warning"></i> <strong>Mode test:</strong> Emprunt dans le passé (il y a ' + Math.abs(diffDays) + ' jour(s)).<br><strong>Utile pour:</strong> Tester les retards, pénalités, et restrictions d\'emprunt. La date de fin sera calculée automatiquement.';
            }
        });
    });
</script>
