<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!-- Page Title -->
<c:set var="pageTitle" value="${exemplaire.id != null ? 'Modifier un Exemplaire' : 'Ajouter un Exemplaire'}" scope="request"/>

<!-- Custom CSS for this page -->
<style>
    .page-header {
        background: linear-gradient(135deg, #34495e 0%, #2c3e50 100%);
        color: white;
        padding: 2rem 0;
        margin-bottom: 2rem;
    }
    .form-card {
        border: none;
        box-shadow: 0 4px 20px rgba(0,0,0,0.1);
        border-radius: 15px;
        overflow: hidden;
    }
    .form-header {
        background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
        color: white;
        padding: 1.5rem;
    }
    .form-body {
        padding: 2rem;
    }
    .required-field::after {
        content: " *";
        color: #dc3545;
    }
    .livre-info {
        background: #f8f9fa;
        border: 1px solid #dee2e6;
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
                <nav aria-label="breadcrumb">
                    <ol class="breadcrumb mb-2">
                        <li class="breadcrumb-item"><a href="/exemplaires/list" class="text-white-50">Exemplaires</a></li>
                        <li class="breadcrumb-item active text-white" aria-current="page">
                            ${exemplaire.id != null ? 'Modifier' : 'Ajouter'}
                        </li>
                    </ol>
                </nav>
                <h1>
                    <i class="fas fa-copy"></i> 
                    ${exemplaire.id != null ? 'Modifier un Exemplaire' : 'Ajouter un Exemplaire'}
                </h1>
            </div>
            <div class="col-auto">
                <c:choose>
                    <c:when test="${not empty selectedLivre}">
                        <a href="/livres/${selectedLivre.id}" class="btn btn-light">
                            <i class="fas fa-arrow-left"></i> Retour au livre
                        </a>
                    </c:when>
                    <c:otherwise>
                        <a href="/exemplaires/list" class="btn btn-light">
                            <i class="fas fa-arrow-left"></i> Retour à la liste
                        </a>
                    </c:otherwise>
                </c:choose>
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

    <div class="row justify-content-center">
        <div class="col-lg-8">
            <div class="card form-card">
                <div class="form-header">
                    <h3 class="mb-0">
                        <i class="fas fa-copy"></i> 
                        ${exemplaire.id != null ? 'Modification de l\'exemplaire' : 'Nouvel exemplaire'}
                    </h3>
                    <c:if test="${exemplaire.id != null}">
                        <small>ID: ${exemplaire.id}</small>
                    </c:if>
                </div>
                
                <div class="form-body">
                    <!-- Book Information (if pre-selected) -->
                    <c:if test="${not empty selectedLivre}">
                        <div class="livre-info">
                            <h5 class="text-primary mb-2">
                                <i class="fas fa-book"></i> Livre sélectionné
                            </h5>
                            <p class="mb-1"><strong>${selectedLivre.titre}</strong></p>
                            <p class="text-muted mb-0">par ${selectedLivre.auteur.nom}</p>
                        </div>
                    </c:if>

                    <form action="${exemplaire.id != null ? '/exemplaires/edit/' += exemplaire.id : '/exemplaires/add'}" 
                          method="post" class="needs-validation" novalidate>
                        
                        <!-- Book Selection (if not pre-selected) -->
                        <c:if test="${empty selectedLivre}">
                            <div class="mb-3">
                                <label for="livreId" class="form-label required-field">Livre</label>
                                <select class="form-select" id="livreId" name="livreId" required>
                                    <option value="">Sélectionnez un livre...</option>
                                    <c:forEach var="livre" items="${livres}">
                                        <option value="${livre.id}" 
                                                ${exemplaire.livre != null && exemplaire.livre.id == livre.id ? 'selected' : ''}>
                                            ${livre.titre} - ${livre.auteur.nom}
                                        </option>
                                    </c:forEach>
                                </select>
                                <div class="invalid-feedback">
                                    Veuillez sélectionner un livre.
                                </div>
                            </div>
                        </c:if>

                        <!-- Hidden field for pre-selected book -->
                        <c:if test="${not empty selectedLivre}">
                            <input type="hidden" name="livreId" value="${selectedLivre.id}">
                        </c:if>

                        <!-- Arrival Date -->
                        <div class="mb-3">
                            <label for="dateArrivee" class="form-label">Date d'arrivée</label>
                            <input type="datetime-local" 
                                   class="form-control" 
                                   id="dateArrivee" 
                                   name="dateArrivee"
                                   value="<fmt:formatDate value='${exemplaire.dateArrivee}' pattern='yyyy-MM-dd\'T\'HH:mm'/>"
                                   max="<fmt:formatDate value='${now}' pattern='yyyy-MM-dd\'T\'HH:mm'/>">
                            <div class="form-text">
                                <i class="fas fa-info-circle"></i> 
                                Laissez vide pour utiliser la date et l'heure actuelles.
                            </div>
                        </div>

                        <!-- Current Information (for edit mode) -->
                        <c:if test="${exemplaire.id != null}">
                            <div class="mb-3">
                                <div class="card bg-light">
                                    <div class="card-body">
                                        <h6 class="card-title">Informations actuelles</h6>
                                        <p class="card-text">
                                            <strong>Livre:</strong> ${exemplaire.livre.titre}<br>
                                            <strong>Auteur:</strong> ${exemplaire.livre.auteur.nom}<br>
                                            <strong>Date d'arrivée:</strong> 
                                            <fmt:formatDate value="${exemplaire.dateArrivee}" pattern="dd/MM/yyyy à HH:mm"/>
                                        </p>
                                    </div>
                                </div>
                            </div>
                        </c:if>

                        <!-- Form Actions -->
                        <div class="d-grid gap-2 d-md-flex justify-content-md-end">
                            <c:choose>
                                <c:when test="${not empty selectedLivre}">
                                    <a href="/livres/${selectedLivre.id}" class="btn btn-secondary me-md-2">
                                        <i class="fas fa-times"></i> Annuler
                                    </a>
                                </c:when>
                                <c:otherwise>
                                    <a href="/exemplaires/list" class="btn btn-secondary me-md-2">
                                        <i class="fas fa-times"></i> Annuler
                                    </a>
                                </c:otherwise>
                            </c:choose>
                            <button type="submit" class="btn btn-primary">
                                <i class="fas fa-save"></i> 
                                ${exemplaire.id != null ? 'Mettre à jour' : 'Ajouter'} l'exemplaire
                            </button>
                        </div>
                    </form>
                </div>
            </div>

            <!-- Help Card -->
            <div class="card mt-3">
                <div class="card-header">
                    <h6 class="mb-0"><i class="fas fa-question-circle"></i> Aide</h6>
                </div>
                <div class="card-body">
                    <p class="card-text mb-2">
                        <strong>Date d'arrivée:</strong> 
                        Représente quand cet exemplaire a été ajouté à la bibliothèque. 
                        Si vous laissez ce champ vide, la date et l'heure actuelles seront utilisées.
                    </p>
                    <p class="card-text mb-0">
                        <strong>Livre:</strong> 
                        Chaque exemplaire doit être associé à un livre existant. 
                        Vous pouvez avoir plusieurs exemplaires du même livre.
                    </p>
                </div>
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

    // Form validation
    (function() {
        'use strict';
        window.addEventListener('load', function() {
            var forms = document.getElementsByClassName('needs-validation');
            var validation = Array.prototype.filter.call(forms, function(form) {
                form.addEventListener('submit', function(event) {
                    if (form.checkValidity() === false) {
                        event.preventDefault();
                        event.stopPropagation();
                    }
                    form.classList.add('was-validated');
                }, false);
            });
        }, false);
    })();

    // Set default date to now if field is empty
    document.addEventListener('DOMContentLoaded', function() {
        const dateInput = document.getElementById('dateArrivee');
        if (dateInput && !dateInput.value) {
            const now = new Date();
            now.setMinutes(now.getMinutes() - now.getTimezoneOffset());
            dateInput.value = now.toISOString().slice(0, 16);
        }
    });
</script>
