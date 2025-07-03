<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!-- Page Title -->
<c:set var="pageTitle" value="${livre.id != null ? 'Modifier' : 'Ajouter'} un Livre" scope="request"/>

<!-- Custom CSS for this page -->
<style>
    .form-container {
        max-width: 800px;
        margin: 2rem auto;
        padding: 2rem;
        box-shadow: 0 0 15px rgba(0,0,0,0.1);
        border-radius: 10px;
        background: white;
    }
    .form-header {
        text-align: center;
        margin-bottom: 2rem;
        color: #2c3e50;
    }
    .form-section {
        margin-bottom: 2rem;
    }
    .form-section-title {
        font-size: 1.1rem;
        font-weight: 600;
        color: #34495e;
        margin-bottom: 1rem;
        border-bottom: 2px solid #3498db;
        padding-bottom: 0.5rem;
    }
    .checkbox-group {
        max-height: 200px;
        overflow-y: auto;
        border: 1px solid #dee2e6;
        border-radius: 5px;
        padding: 10px;
        background-color: #f8f9fa;
    }
    .checkbox-item {
        margin-bottom: 5px;
    }
    .btn-primary {
        background-color: #3498db;
        border-color: #3498db;
    }
    .btn-primary:hover {
        background-color: #2980b9;
        border-color: #2980b9;
    }
</style>

<div class="container">
    <div class="form-container">
            <!-- Header -->
            <div class="form-header">
                <h2><i class="fas fa-book"></i> ${livre.id != null ? 'Modifier' : 'Ajouter'} un Livre</h2>
            </div>

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

            <!-- Form -->
            <form action="${livre.id != null ? '/livres/edit/' += livre.id : '/livres/add'}" method="post">
                <!-- Basic Information Section -->
                <div class="form-section">
                    <div class="form-section-title">
                        <i class="fas fa-info-circle"></i> Informations de base
                    </div>
                    
                    <div class="row">
                        <div class="col-md-8">
                            <div class="mb-3">
                                <label for="titre" class="form-label">Titre <span class="text-danger">*</span></label>
                                <input type="text" class="form-control" id="titre" name="titre" 
                                       value="${livre.titre}" required maxlength="255">
                            </div>
                        </div>
                        <div class="col-md-4">
                            <div class="mb-3">
                                <label for="dateSortie" class="form-label">Date de sortie <span class="text-danger">*</span></label>
                                <input type="datetime-local" class="form-control" id="dateSortie" name="dateSortie" 
                                       value="<fmt:formatDate value='${livre.dateSortieAsDate}' pattern='yyyy-MM-dd\'T\'HH:mm'/>" required>
                            </div>
                        </div>
                    </div>

                    <div class="mb-3">
                        <label for="resume" class="form-label">Résumé</label>
                        <textarea class="form-control" id="resume" name="resume" rows="4" 
                                  maxlength="255" placeholder="Description du livre...">${livre.resume}</textarea>
                        <div class="form-text">Maximum 255 caractères</div>
                    </div>

                    <div class="mb-3">
                        <label for="auteurId" class="form-label">Auteur <span class="text-danger">*</span></label>
                        <select class="form-select" id="auteurId" name="auteurId" required>
                            <option value="">Sélectionner un auteur</option>
                            <c:forEach var="auteur" items="${auteurs}">
                                <option value="${auteur.id}" 
                                        ${livre.auteur != null && livre.auteur.id == auteur.id ? 'selected' : ''}>
                                    ${auteur.nom}
                                </option>
                            </c:forEach>
                        </select>
                    </div>
                </div>

                <!-- Genres Section -->
                <div class="form-section">
                    <div class="form-section-title">
                        <i class="fas fa-tags"></i> Genres
                    </div>
                    <div class="checkbox-group">
                        <c:forEach var="genre" items="${genres}">
                            <div class="form-check checkbox-item">
                                <input class="form-check-input" type="checkbox" id="genre_${genre.id}" 
                                       name="genreIds" value="${genre.id}"
                                       <c:forEach var="livreGenre" items="${livre.genres}">
                                           ${livreGenre.id == genre.id ? 'checked' : ''}
                                       </c:forEach>>
                                <label class="form-check-label" for="genre_${genre.id}">
                                    ${genre.libelle}
                                </label>
                            </div>
                        </c:forEach>
                        <c:if test="${empty genres}">
                            <p class="text-muted mb-0">Aucun genre disponible</p>
                        </c:if>
                    </div>
                </div>

                <!-- Constraints Section -->
                <div class="form-section">
                    <div class="form-section-title">
                        <i class="fas fa-exclamation-triangle"></i> Contraintes
                    </div>
                    <div class="checkbox-group">
                        <c:forEach var="contrainte" items="${contraintes}">
                            <div class="form-check checkbox-item">
                                <input class="form-check-input" type="checkbox" id="contrainte_${contrainte.id}" 
                                       name="contraintIds" value="${contrainte.id}"
                                       <c:forEach var="livreContrainte" items="${livre.contraintes}">
                                           ${livreContrainte.id == contrainte.id ? 'checked' : ''}
                                       </c:forEach>>
                                <label class="form-check-label" for="contrainte_${contrainte.id}">
                                    ${contrainte.typeContrainte}
                                </label>
                            </div>
                        </c:forEach>
                        <c:if test="${empty contraintes}">
                            <p class="text-muted mb-0">Aucune contrainte disponible</p>
                        </c:if>
                    </div>
                </div>

                <!-- Form Actions -->
                <div class="d-flex justify-content-between">
                    <a href="/livres/list" class="btn btn-secondary">
                        <i class="fas fa-arrow-left"></i> Retour à la liste
                    </a>
                    <div>
                        <button type="reset" class="btn btn-outline-secondary me-2">
                            <i class="fas fa-undo"></i> Réinitialiser
                        </button>
                        <button type="submit" class="btn btn-primary">
                            <i class="fas fa-save"></i> ${livre.id != null ? 'Modifier' : 'Ajouter'} le livre
                        </button>
                    </div>
                </div>
            </form>
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

        // Character counter for resume
        document.getElementById('resume').addEventListener('input', function() {
            let text = this.value;
            let maxLength = 255;
            let remaining = maxLength - text.length;
            
            // Find or create character counter
            let counter = document.getElementById('resume-counter');
            if (!counter) {
                counter = document.createElement('div');
                counter.id = 'resume-counter';
                counter.className = 'form-text';
                this.parentNode.appendChild(counter);
            }
            
            counter.textContent = remaining + ' caractères restants';
            counter.className = remaining < 50 ? 'form-text text-warning' : 'form-text';
            
            if (remaining < 0) {
                counter.className = 'form-text text-danger';
                counter.textContent = 'Limite de caractères dépassée de ' + Math.abs(remaining) + ' caractères';
            }
        });
    </script>
