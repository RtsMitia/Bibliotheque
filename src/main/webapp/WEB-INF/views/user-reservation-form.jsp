<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!-- Page Title -->
<c:set var="pageTitle" value="Faire une Réservation" scope="request"/>

<!-- Custom CSS for this page -->
<style>
    .page-header {
        background: linear-gradient(135deg, #34495e 0%, #2c3e50 100%);
        color: white;
        padding: 3rem 0;
        margin-bottom: 2rem;
    }
    .form-container {
        background: #f8f9fa;
        border: 1px solid #e9ecef;
        border-radius: 8px;
        padding: 2rem;
        margin-bottom: 2rem;
    }
    .book-selection {
        max-height: 400px;
        overflow-y: auto;
        border: 1px solid #e9ecef;
        border-radius: 6px;
        padding: 1rem;
        background: white;
    }
    .book-item {
        border: 1px solid #e9ecef;
        border-radius: 6px;
        padding: 1rem;
        margin-bottom: 0.5rem;
        cursor: pointer;
        transition: all 0.2s ease;
        background: white;
    }
    .book-item:hover {
        background-color: #f8f9fa;
        border-color: #2c3e50;
        transform: translateY(-1px);
    }
    .book-item.selected {
        background-color: #e3f2fd;
        border-color: #2c3e50;
        border-width: 2px;
    }
    .book-item.disabled {
        opacity: 0.6;
        cursor: not-allowed;
        background-color: #f8f9fa;
    }
    .book-item.disabled:hover {
        background-color: #f8f9fa;
        border-color: #e9ecef;
        transform: none;
    }
    .book-title {
        color: #2c3e50;
        font-weight: 600;
        margin-bottom: 0.5rem;
    }
    .book-meta {
        color: #6c757d;
        font-size: 0.9rem;
    }
    .btn-outline-light {
        border-color: rgba(255,255,255,0.3);
        color: white;
    }
    .btn-outline-light:hover {
        background-color: rgba(255,255,255,0.1);
        border-color: rgba(255,255,255,0.5);
        color: white;
    }
</style>

<!-- Page Header -->
<div class="page-header">
    <div class="container">
        <div class="row align-items-center">
            <div class="col">
                <h1><i class="fas fa-calendar-plus"></i> Faire une Réservation</h1>
                <p class="mb-0">Réservez facilement vos livres préférés</p>
            </div>
            <div class="col-auto">
                <a href="/reservations/mes-reservations" class="btn btn-outline-light btn-lg">
                    <i class="fas fa-list"></i> Mes Réservations
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

    <!-- Reservation Form -->
    <div class="form-container">
        <form method="post" action="/reservations/faire-reservation" id="reservationForm">
            <div class="row">
                <!-- Step 1: Dates -->
                <div class="col-md-6">
                    <h4><i class="fas fa-calendar"></i> Dates de réservation</h4>
                    
                    <div class="mb-3">
                        <label for="dateReservation" class="form-label">
                            <i class="fas fa-calendar-plus"></i> Date de réservation
                        </label>
                        <input type="datetime-local" 
                               class="form-control form-control-lg" 
                               id="dateReservation" 
                               name="dateReservation">
                        <div class="form-text">Laissez vide pour utiliser la date actuelle</div>
                    </div>

                    <div class="mb-3">
                        <label for="dateDebutPret" class="form-label">
                            <i class="fas fa-calendar"></i> Date souhaitée pour le prêt <span class="text-danger">*</span>
                        </label>
                        <input type="datetime-local" 
                               class="form-control form-control-lg" 
                               id="dateDebutPret" 
                               name="dateDebutPret"
                               required>
                        <div class="form-text">Quand souhaitez-vous récupérer le livre ?</div>
                    </div>
                </div>

                <!-- Step 2: Book Selection -->
                <div class="col-md-6">
                    <h4><i class="fas fa-book"></i> Choisir un livre</h4>
                    <div class="mb-3">
                        <label class="form-label">
                            <i class="fas fa-search"></i> Rechercher un livre
                        </label>
                        <input type="text" 
                               class="form-control" 
                               id="bookSearch" 
                               placeholder="Tapez le titre ou l'auteur..."
                               onkeyup="filterBooks()">
                    </div>

                    <div class="book-selection" id="bookSelection">
                        <c:choose>
                            <c:when test="${not empty exemplaires}">
                                <c:forEach var="exemplaire" items="${exemplaires}">
                                    <div class="book-item ${exemplaire.isAvailable() ? '' : 'disabled'}" 
                                         data-title="${exemplaire.livre.titre}" 
                                         data-author="${exemplaire.livre.auteur.nom}"
                                         data-available="${exemplaire.isAvailable()}"
                                         onclick="selectBook('${exemplaire.id}', '${fn:escapeXml(exemplaire.livre.titre)}', '${fn:escapeXml(exemplaire.livre.auteur.nom)}', '${exemplaire.isAvailable()}')">
                                        <div class="row">
                                            <div class="col">
                                                <h6 class="book-title">${exemplaire.livre.titre}</h6>
                                                <p class="book-meta mb-1">
                                                    <i class="fas fa-user"></i> ${exemplaire.livre.auteur.nom}
                                                </p>
                                                <small class="book-meta">
                                                    <i class="fas fa-barcode"></i> Exemplaire #${exemplaire.id}
                                                    <c:if test="${not empty exemplaire.livre.dateSortieAsDate}">
                                                        | <i class="fas fa-calendar"></i> 
                                                        <fmt:formatDate value="${exemplaire.livre.dateSortieAsDate}" pattern="yyyy"/>
                                                    </c:if>
                                                </small>
                                            </div>
                                            <div class="col-auto">
                                                <c:choose>
                                                    <c:when test="${exemplaire.isAvailable()}">
                                                        <span class="badge bg-success">Disponible</span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="badge bg-danger">Indisponible</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </div>
                                        </div>
                                    </div>
                                </c:forEach>
                            </c:when>
                            <c:otherwise>
                                <div class="text-center text-muted py-4">
                                    <i class="fas fa-book-open fa-3x mb-2"></i>
                                    <p>Aucun exemplaire disponible pour le moment</p>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>

                    <input type="hidden" id="exemplaireId" name="exemplaireId">
                    <div id="selectedBookInfo" class="mt-2" style="display: none;">
                        <div class="alert alert-info">
                            <i class="fas fa-check"></i> <strong>Livre sélectionné:</strong>
                            <span id="selectedBookTitle"></span> par <span id="selectedBookAuthor"></span>
                        </div>
                    </div>
                </div>
            </div>
           

            <!-- Submit Buttons -->
            <div class="row mt-4">
                <div class="col-12 text-center">
                    <button type="button" class="btn btn-secondary btn-lg me-3" onclick="window.history.back()">
                        <i class="fas fa-arrow-left"></i> Retour
                    </button>
                    <button type="submit" class="btn btn-primary btn-lg" id="submitBtn" disabled>
                        <i class="fas fa-paper-plane"></i> Soumettre la Réservation
                    </button>
                </div>
            </div>
        </form>
    </div>
</div>

<script>
let selectedExemplaireId = null;

function selectBook(exemplaireId, titre, auteur, isAvailableStr) {
    // Convert string to boolean
    const isAvailable = isAvailableStr === 'true';
    
    // Don't allow selection of unavailable books
    if (!isAvailable) {
        alert('Ce livre n\'est pas disponible pour le moment.');
        return;
    }
    
    // Remove previous selection
    document.querySelectorAll('.book-item').forEach(item => {
        item.classList.remove('selected');
    });
    
    // Mark current selection
    event.currentTarget.classList.add('selected');
    
    // Store selection
    selectedExemplaireId = exemplaireId;
    document.getElementById('exemplaireId').value = exemplaireId;
    
    // Show selected book info
    document.getElementById('selectedBookTitle').textContent = titre;
    document.getElementById('selectedBookAuthor').textContent = auteur;
    document.getElementById('selectedBookInfo').style.display = 'block';
    
    // Enable submit button if date is also filled
    checkFormValidity();
}

function filterBooks() {
    const searchTerm = document.getElementById('bookSearch').value.toLowerCase();
    const bookItems = document.querySelectorAll('.book-item');
    
    bookItems.forEach(item => {
        const title = item.getAttribute('data-title').toLowerCase();
        const author = item.getAttribute('data-author').toLowerCase();
        
        if (title.includes(searchTerm) || author.includes(searchTerm)) {
            item.style.display = 'block';
        } else {
            item.style.display = 'none';
        }
    });
}

function checkFormValidity() {
    const dateDebutPret = document.getElementById('dateDebutPret').value;
    const submitBtn = document.getElementById('submitBtn');
    
    if (dateDebutPret && selectedExemplaireId) {
        submitBtn.disabled = false;
        submitBtn.classList.remove('btn-secondary');
        submitBtn.classList.add('btn-success');
    } else {
        submitBtn.disabled = true;
        submitBtn.classList.remove('btn-success');
        submitBtn.classList.add('btn-primary');
    }
}

// Event listeners
document.getElementById('dateDebutPret').addEventListener('change', function() {
    checkFormValidity();
});

// Set minimum date to today for both date fields
document.getElementById('dateDebutPret').min = new Date().toISOString().slice(0, 16);
if (document.getElementById('dateReservation')) {
    document.getElementById('dateReservation').min = new Date().toISOString().slice(0, 16);
}

// Form validation
document.getElementById('reservationForm').addEventListener('submit', function(e) {
    if (!selectedExemplaireId) {
        e.preventDefault();
        alert('Veuillez sélectionner un livre');
        return false;
    }
    
    const dateDebutPret = document.getElementById('dateDebutPret').value;
    if (!dateDebutPret) {
        e.preventDefault();
        alert('Veuillez sélectionner une date de début de prêt');
        return false;
    }
    
    // Show loading state
    const submitBtn = document.getElementById('submitBtn');
    submitBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Envoi en cours...';
    submitBtn.disabled = true;
});
</script>
