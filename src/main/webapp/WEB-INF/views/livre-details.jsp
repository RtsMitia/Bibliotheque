<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!-- Page Title -->
<c:set var="pageTitle" value="Détails du Livre" scope="request"/>

<!-- Custom CSS for this page -->
<style>
    .page-header {
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        color: white;
        padding: 2rem 0;
        margin-bottom: 2rem;
    }
    .detail-card {
        border: none;
        box-shadow: 0 4px 20px rgba(0,0,0,0.1);
        border-radius: 15px;
        overflow: hidden;
    }
    .detail-header {
        background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
        color: white;
        padding: 2rem;
    }
    .detail-body {
        padding: 2rem;
    }
    .info-row {
        margin-bottom: 1.5rem;
        padding-bottom: 1rem;
        border-bottom: 1px solid #eee;
    }
    .info-row:last-child {
        border-bottom: none;
        margin-bottom: 0;
    }
    .info-label {
        font-weight: 600;
        color: #495057;
        margin-bottom: 0.5rem;
    }
    .info-value {
        font-size: 1.1rem;
        color: #212529;
    }
    .genre-badge {
        font-size: 0.9rem;
        margin: 0.3rem;
        padding: 0.5rem 1rem;
    }
    .constraint-badge {
        font-size: 0.9rem;
        margin: 0.3rem;
        padding: 0.5rem 1rem;
    }
    .action-buttons {
        background: #f8f9fa;
        padding: 1.5rem;
        border-top: 1px solid #dee2e6;
    }
    .exemplaire-card {
        border-left: 4px solid #007bff;
        margin-bottom: 1rem;
    }
    .exemplaire-card.disponible {
        border-left-color: #28a745;
    }
    .exemplaire-card.emprunte {
        border-left-color: #dc3545;
    }
    .exemplaire-card.reserve {
        border-left-color: #ffc107;
    }
</style>

<!-- Page Header -->
<div class="page-header">
    <div class="container">
        <div class="row align-items-center">
            <div class="col">
                <nav aria-label="breadcrumb">
                    <ol class="breadcrumb mb-2">
                        <li class="breadcrumb-item"><a href="/livres/list" class="text-white-50">Livres</a></li>
                        <li class="breadcrumb-item active text-white" aria-current="page">Détails</li>
                    </ol>
                </nav>
                <h1><i class="fas fa-book-open"></i> Détails du Livre</h1>
            </div>
            <div class="col-auto">
                <a href="/livres/list" class="btn btn-light">
                    <i class="fas fa-arrow-left"></i> Retour à la liste
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
        <!-- Main Book Details -->
        <div class="col-lg-8">
            <div class="card detail-card">
                <div class="detail-header">
                    <h2 class="mb-1">${livre.titre}</h2>
                    <p class="mb-0 h5">
                        <i class="fas fa-user"></i> ${livre.auteur.nom}
                    </p>
                </div>
                
                <div class="detail-body">
                    <!-- Date de sortie -->
                    <div class="info-row">
                        <div class="info-label">
                            <i class="fas fa-calendar"></i> Date de sortie
                        </div>
                        <div class="info-value">
                            <fmt:formatDate value="${livre.dateSortieAsDate}" pattern="dd MMMM yyyy"/>
                        </div>
                    </div>

                    <!-- Résumé -->
                    <c:if test="${not empty livre.resume}">
                        <div class="info-row">
                            <div class="info-label">
                                <i class="fas fa-align-left"></i> Résumé
                            </div>
                            <div class="info-value">
                                ${livre.resume}
                            </div>
                        </div>
                    </c:if>

                    <!-- Genres -->
                    <c:if test="${not empty livre.genres}">
                        <div class="info-row">
                            <div class="info-label">
                                <i class="fas fa-tags"></i> Genres
                            </div>
                            <div class="info-value">
                                <c:forEach var="genre" items="${livre.genres}">
                                    <span class="badge bg-info genre-badge">${genre.libelle}</span>
                                </c:forEach>
                            </div>
                        </div>
                    </c:if>

                    <!-- Contraintes -->
                    <c:if test="${not empty livre.contraintes}">
                        <div class="info-row">
                            <div class="info-label">
                                <i class="fas fa-exclamation-triangle"></i> Contraintes d'emprunt
                            </div>
                            <div class="info-value">
                                <c:forEach var="contrainte" items="${livre.contraintes}">
                                    <span class="badge bg-warning text-dark constraint-badge">
                                        <i class="fas fa-exclamation-triangle"></i> ${contrainte.typeContrainte}
                                    </span>
                                </c:forEach>
                            </div>
                        </div>
                    </c:if>

                    <!-- Statistiques -->
                    <div class="info-row">
                        <div class="info-label">
                            <i class="fas fa-chart-bar"></i> Statistiques
                        </div>
                        <div class="info-value">
                            <div class="row text-center">
                                <div class="col-4">
                                    <div class="border rounded p-2">
                                        <div class="h4 text-primary mb-1">
                                            ${livre.exemplaires != null ? livre.exemplaires.size() : 0}
                                        </div>
                                        <small class="text-muted">Total exemplaires</small>
                                    </div>
                                </div>
                                <div class="col-4">
                                    <div class="border rounded p-2">
                                        <div class="h4 text-success mb-1">
                                            <!-- Count available exemplaires here if status field exists -->
                                            ${livre.exemplaires != null ? livre.exemplaires.size() : 0}
                                        </div>
                                        <small class="text-muted">Disponibles</small>
                                    </div>
                                </div>
                                <div class="col-4">
                                    <div class="border rounded p-2">
                                        <div class="h4 text-danger mb-1">0</div>
                                        <small class="text-muted">Empruntés</small>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Action Buttons -->
                <div class="action-buttons">
                    <div class="row">
                        <div class="col">
                            <a href="/livres/edit/${livre.id}" class="btn btn-warning">
                                <i class="fas fa-edit"></i> Modifier ce livre
                            </a>
                            <button type="button" class="btn btn-danger" data-bs-toggle="modal" data-bs-target="#deleteModal">
                                <i class="fas fa-trash"></i> Supprimer ce livre
                            </button>
                        </div>
                        <div class="col-auto">
                            <a href="/livres/list" class="btn btn-secondary">
                                <i class="fas fa-list"></i> Retour à la liste
                            </a>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Sidebar with Exemplaires -->
        <div class="col-lg-4">
            <div class="card">
                <div class="card-header bg-primary text-white">
                    <h5 class="mb-0">
                        <i class="fas fa-copy"></i> Exemplaires 
                        <span class="badge bg-light text-primary ms-2">
                            ${livre.exemplaires != null ? livre.exemplaires.size() : 0}
                        </span>
                    </h5>
                </div>
                <div class="card-body">
                    <c:choose>
                        <c:when test="${not empty livre.exemplaires}">
                            <c:forEach var="exemplaire" items="${livre.exemplaires}" varStatus="status">
                                <div class="card exemplaire-card disponible">
                                    <div class="card-body py-2">
                                        <div class="d-flex justify-content-between align-items-center">
                                            <div>
                                                <strong>Exemplaire #${status.index + 1}</strong>
                                                <br>
                                                <small class="text-muted">ID: ${exemplaire.id}</small>
                                            </div>
                                            <span class="badge bg-success">Disponible</span>
                                        </div>
                                    </div>
                                </div>
                            </c:forEach>
                        </c:when>
                        <c:otherwise>
                            <div class="text-center text-muted py-3">
                                <i class="fas fa-copy fa-2x mb-2"></i>
                                <p>Aucun exemplaire disponible</p>
                                <small>Ajoutez des exemplaires pour permettre l'emprunt de ce livre.</small>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>
                <div class="card-footer">
                    <a href="/exemplaires/add?livreId=${livre.id}" class="btn btn-primary btn-sm w-100">
                        <i class="fas fa-plus"></i> Ajouter un exemplaire
                    </a>
                </div>
            </div>

            
        </div>
    </div>
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
                <div class="text-center">
                    <i class="fas fa-exclamation-triangle fa-3x text-warning mb-3"></i>
                    <h5>Attention !</h5>
                    <p>Êtes-vous sûr de vouloir supprimer le livre "<strong>${livre.titre}</strong>" ?</p>
                    <div class="alert alert-warning">
                        <small>
                            <i class="fas fa-info-circle"></i>
                            Cette action supprimera également tous les exemplaires associés et ne peut pas être annulée.
                        </small>
                    </div>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">
                    <i class="fas fa-times"></i> Annuler
                </button>
                <form action="/livres/delete/${livre.id}" method="post" style="display: inline;">
                    <button type="submit" class="btn btn-danger">
                        <i class="fas fa-trash"></i> Supprimer définitivement
                    </button>
                </form>
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

    // Add smooth scrolling for internal links
    document.querySelectorAll('a[href^="#"]').forEach(anchor => {
        anchor.addEventListener('click', function (e) {
            e.preventDefault();
            document.querySelector(this.getAttribute('href')).scrollIntoView({
                behavior: 'smooth'
            });
        });
    });
</script>
