<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!-- Page Title -->
<c:set var="pageTitle" value="Liste des Livres" scope="request"/>

<!-- Custom CSS for this page -->
<style>
    .page-header {
        background: linear-gradient(135deg, #34495e 0%, #2c3e50 100%);
        color: white;
        padding: 2rem 0;
        margin-bottom: 2rem;
    }
    .book-card {
        transition: transform 0.2s, box-shadow 0.2s;
        border: 1px solid #e9ecef;
        box-shadow: 0 2px 8px rgba(0,0,0,0.08);
        background: #fff;
    }
    .book-card:hover {
        transform: translateY(-3px);
        box-shadow: 0 4px 15px rgba(0,0,0,0.12);
        border-color: #dee2e6;
    }
    .search-box {
        background: #f8f9fa;
        border: 1px solid #e9ecef;
        border-radius: 8px;
        padding: 1.5rem;
        margin-bottom: 2rem;
    }
    .book-title {
        color: #2c3e50;
        font-weight: 600;
        margin-bottom: 0.75rem;
    }
    .book-meta {
        color: #6c757d;
        font-size: 0.9rem;
        margin-bottom: 0.5rem;
    }
    .book-description {
        color: #495057;
        font-size: 0.9rem;
        line-height: 1.4;
        margin-bottom: 0.75rem;
    }
    .genre-badge {
        font-size: 0.75rem;
        margin: 0.2rem;
        background-color: #e9ecef;
        color: #495057;
        border: none;
    }
    .constraint-badge {
        font-size: 0.75rem;
        margin: 0.2rem;
        background-color: #fff3cd;
        color: #856404;
        border: 1px solid #ffeaa7;
    }
    .btn-outline-primary {
        border-color: #2c3e50;
        color: #2c3e50;
    }
    .btn-outline-primary:hover {
        background-color: #2c3e50;
        border-color: #2c3e50;
    }
    .btn-outline-warning {
        border-color: #6c757d;
        color: #6c757d;
    }
    .btn-outline-warning:hover {
        background-color: #6c757d;
        border-color: #6c757d;
    }
    .btn-outline-danger {
        border-color: #dc3545;
        color: #dc3545;
    }
    .btn-outline-danger:hover {
        background-color: #dc3545;
        border-color: #dc3545;
    }
    .stats-card {
        background: #f8f9fa;
        border: 1px solid #e9ecef;
    }
</style>

<!-- Page Header -->
    <div class="page-header">
        <div class="container">
            <div class="row align-items-center">
                <div class="col">
                    <h1><i class="fas fa-book"></i> Gestion des Livres</h1>
                    <p class="mb-0">Parcourez et gérez la collection de livres</p>
                </div>
                <div class="col-auto">
                    <a href="/livres/add" class="btn btn-light btn-lg">
                        <i class="fas fa-plus"></i> Ajouter un livre
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

        <!-- Search Box -->
        <div class="search-box">
            <form action="/livres/search" method="get" class="row g-3">
                <div class="col-md-9">
                    <input type="text" class="form-control" name="searchTerm" 
                           placeholder="Rechercher par titre ou nom d'auteur..." 
                           value="${searchTerm}">
                </div>
                <div class="col-md-3">
                    <button type="submit" class="btn btn-primary w-100">
                        <i class="fas fa-search"></i> Rechercher
                    </button>
                </div>
            </form>
            <c:if test="${not empty searchTerm}">
                <div class="mt-2">
                    <small class="text-muted">
                        Résultats de recherche pour: "<strong>${searchTerm}</strong>"
                        <a href="/livres/list" class="ms-2">Voir tous les livres</a>
                    </small>
                </div>
            </c:if>
        </div>

        <!-- Books Grid -->
        <div class="row">
            <c:forEach var="livre" items="${livres}">
                <div class="col-lg-4 col-md-6 mb-4">
                    <div class="card book-card h-100">
                        <div class="card-body">
                            <h5 class="book-title">${livre.titre}</h5>
                            <p class="book-meta">
                                <i class="fas fa-user"></i> ${livre.auteur.nom}
                            </p>
                            
                            <c:if test="${not empty livre.resume}">
                                <p class="book-description">
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

                            <p class="book-meta">
                                <i class="fas fa-calendar"></i> 
                                <fmt:formatDate value="${livre.dateSortieAsDate}" pattern="dd/MM/yyyy"/>
                            </p>

                            <!-- Genres -->
                            <c:if test="${not empty livre.genres}">
                                <div class="mb-2">
                                    <c:forEach var="genre" items="${livre.genres}">
                                        <span class="badge genre-badge">${genre.libelle}</span>
                                    </c:forEach>
                                </div>
                            </c:if>

                            <!-- Constraints -->
                            <c:if test="${not empty livre.contraintes}">
                                <div class="mb-2">
                                    <c:forEach var="contrainte" items="${livre.contraintes}">
                                        <span class="badge constraint-badge">
                                            <i class="fas fa-exclamation-triangle"></i> ${contrainte.typeContrainte}
                                        </span>
                                    </c:forEach>
                                </div>
                            </c:if>

                            <!-- Exemplaires count -->
                            <p class="book-meta">
                                <i class="fas fa-copy"></i> 
                                ${livre.exemplaires != null ? livre.exemplaires.size() : 0} exemplaire(s)
                            </p>
                        </div>
                        
                        <div class="card-footer bg-transparent">
                            <div class="btn-group w-100" role="group">
                                <a href="/livres/${livre.id}" class="btn btn-outline-primary btn-sm">
                                    <i class="fas fa-eye"></i> Voir
                                </a>
                                <a href="/livres/edit/${livre.id}" class="btn btn-outline-warning btn-sm">
                                    <i class="fas fa-edit"></i> Modifier
                                </a>
                                <button type="button" class="btn btn-outline-danger btn-sm" 
                                        data-bs-toggle="modal" data-bs-target="#deleteModal${livre.id}">
                                    <i class="fas fa-trash"></i> Supprimer
                                </button>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Delete Confirmation Modal -->
                <div class="modal fade" id="deleteModal${livre.id}" tabindex="-1">
                    <div class="modal-dialog">
                        <div class="modal-content">
                            <div class="modal-header">
                                <h5 class="modal-title">Confirmer la suppression</h5>
                                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                            </div>
                            <div class="modal-body">
                                Êtes-vous sûr de vouloir supprimer le livre "<strong>${livre.titre}</strong>" ?
                                <br><small class="text-muted">Cette action est irréversible.</small>
                            </div>
                            <div class="modal-footer">
                                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Annuler</button>
                                <form action="/livres/delete/${livre.id}" method="post" style="display: inline;">
                                    <button type="submit" class="btn btn-danger">Supprimer</button>
                                </form>
                            </div>
                        </div>
                    </div>
                </div>
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
                        <a href="/livres/list" class="btn btn-primary">Voir tous les livres</a>
                    </c:when>
                    <c:otherwise>
                        <p class="text-muted">Commencez par ajouter votre premier livre.</p>
                        <a href="/livres/add" class="btn btn-primary">
                            <i class="fas fa-plus"></i> Ajouter un livre
                        </a>
                    </c:otherwise>
                </c:choose>
            </div>
        </c:if>

        <!-- Statistics -->
        <c:if test="${not empty livres}">
            <div class="row mt-4">
                <div class="col">
                    <div class="card stats-card">
                        <div class="card-body text-center">
                            <h5 class="card-title" style="color: #2c3e50;">Statistiques</h5>
                            <p class="card-text" style="color: #6c757d;">
                                <strong>${livres.size()}</strong> livre(s) 
                                <c:if test="${not empty searchTerm}">trouvé(s)</c:if>
                                <c:if test="${empty searchTerm}">au total</c:if>
                            </p>
                        </div>
                    </div>
                </div>
            </div>
        </c:if>
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
