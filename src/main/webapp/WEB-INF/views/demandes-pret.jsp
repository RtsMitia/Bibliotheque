<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!-- Page Title -->
<c:set var="pageTitle" value="Demandes de Prêt" scope="request"/>

<!-- Custom CSS -->
<style>
    .page-header {
        background: linear-gradient(135deg, #007bff 0%, #0056b3 100%);
        color: white;
        padding: 2rem 0;
        margin-bottom: 2rem;
    }
    .request-card {
        border: none;
        box-shadow: 0 4px 20px rgba(0,0,0,0.1);
        border-radius: 15px;
        margin-bottom: 1.5rem;
        overflow: hidden;
    }
    .status-badge {
        font-size: 0.8rem;
        padding: 0.5rem 1rem;
        border-radius: 20px;
    }
    .status-demande {
        background: linear-gradient(135deg, #ffc107 0%, #e0a800 100%);
        color: #212529;
    }
    .status-en-attente {
        background: linear-gradient(135deg, #17a2b8 0%, #138496 100%);
        color: white;
    }
    .btn-action {
        margin: 0.2rem;
        border-radius: 20px;
        padding: 0.5rem 1rem;
        font-size: 0.9rem;
    }
    .book-info {
        background: #f8f9fa;
        padding: 1rem;
        border-radius: 10px;
        margin-bottom: 1rem;
    }
    .adherent-info {
        background: #e9ecef;
        padding: 1rem;
        border-radius: 10px;
        margin-bottom: 1rem;
    }
</style>

<!-- Page Header -->
<div class="page-header">
    <div class="container">
        <div class="row align-items-center">
            <div class="col">
                <h1><i class="fas fa-clipboard-list"></i> Demandes de Prêt</h1>
                <p class="mb-0">Gérez les demandes d'emprunt des adhérents</p>
            </div>
            <div class="col-auto">
                <a href="/prets/actifs" class="btn btn-light">
                    <i class="fas fa-book-open"></i> Prêts Actifs
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

    <!-- Pending Requests -->
    <div class="row">
        <div class="col-md-6">
            <h3><i class="fas fa-clock text-warning"></i> Demandes en Attente</h3>
            <c:if test="${empty pendingRequests}">
                <div class="alert alert-info">
                    <i class="fas fa-info-circle"></i> Aucune demande en attente.
                </div>
            </c:if>
            
            <c:forEach items="${pendingRequests}" var="pret">
                <div class="request-card">
                    <div class="card-header d-flex justify-content-between align-items-center">
                        <h6 class="mb-0">Demande #${pret.id}</h6>
                        <span class="status-badge status-demande">
                            <i class="fas fa-exclamation"></i> Nouvelle Demande
                        </span>
                    </div>
                    <div class="card-body">
                        <!-- Adherent Info -->
                        <div class="adherent-info">
                            <h6><i class="fas fa-user"></i> Adhérent</h6>
                            <p class="mb-1"><strong>Nom:</strong> ${pret.adherent.prenom} ${pret.adherent.nom}</p>
                            <p class="mb-1"><strong>N° Adhérent:</strong> ${pret.adherent.numeroAdherent}</p>
                            <p class="mb-0"><strong>Email:</strong> ${pret.adherent.email}</p>
                        </div>

                        <!-- Book Info -->
                        <div class="book-info">
                            <h6><i class="fas fa-book"></i> Livre Demandé</h6>
                            <p class="mb-1"><strong>Titre:</strong> ${pret.exemplaire.livre.titre}</p>
                            <p class="mb-1"><strong>Auteur:</strong> ${pret.exemplaire.livre.auteur.nom}</p>
                            <p class="mb-1"><strong>Exemplaire:</strong> #${pret.exemplaire.id}</p>
                            <p class="mb-0">
                                <strong>Type de prêt:</strong> 
                                <span class="badge ${pret.typePret.libelle == 'lire sur place' ? 'bg-warning text-dark' : 'bg-info'}">
                                    <c:choose>
                                        <c:when test="${pret.typePret.libelle == 'lire sur place'}">
                                            <i class="fas fa-clock"></i> ${pret.typePret.libelle} (retour le jour même)
                                        </c:when>
                                        <c:otherwise>
                                            <i class="fas fa-home"></i> ${pret.typePret.libelle}
                                        </c:otherwise>
                                    </c:choose>
                                </span>
                            </p>
                        </div>

                        <!-- Actions -->
                        <div class="text-center">
                            <form method="post" action="/prets/approve/${pret.id}" class="d-inline">
                                <button type="submit" class="btn btn-success btn-action">
                                    <i class="fas fa-check"></i> Approuver
                                </button>
                            </form>
                            <form method="post" action="/prets/hold/${pret.id}" class="d-inline">
                                <button type="submit" class="btn btn-info btn-action">
                                    <i class="fas fa-pause"></i> Mettre en Attente
                                </button>
                            </form>
                            <form method="post" action="/prets/reject/${pret.id}" class="d-inline">
                                <button type="submit" class="btn btn-danger btn-action" 
                                        onclick="return confirm('Êtes-vous sûr de vouloir rejeter cette demande ?')">
                                    <i class="fas fa-times"></i> Rejeter
                                </button>
                            </form>
                        </div>
                    </div>
                </div>
            </c:forEach>
        </div>

        <!-- On Hold Requests -->
        <div class="col-md-6">
            <h3><i class="fas fa-pause-circle text-info"></i> Demandes en Attente</h3>
            <c:if test="${empty onHoldRequests}">
                <div class="alert alert-info">
                    <i class="fas fa-info-circle"></i> Aucune demande en attente de traitement.
                </div>
            </c:if>
            
            <c:forEach items="${onHoldRequests}" var="pret">
                <div class="request-card">
                    <div class="card-header d-flex justify-content-between align-items-center">
                        <h6 class="mb-0">Demande #${pret.id}</h6>
                        <span class="status-badge status-en-attente">
                            <i class="fas fa-pause"></i> En Attente
                        </span>
                    </div>
                    <div class="card-body">
                        <!-- Adherent Info -->
                        <div class="adherent-info">
                            <h6><i class="fas fa-user"></i> Adhérent</h6>
                            <p class="mb-1"><strong>Nom:</strong> ${pret.adherent.prenom} ${pret.adherent.nom}</p>
                            <p class="mb-1"><strong>N° Adhérent:</strong> ${pret.adherent.numeroAdherent}</p>
                            <p class="mb-0"><strong>Email:</strong> ${pret.adherent.email}</p>
                        </div>

                        <!-- Book Info -->
                        <div class="book-info">
                            <h6><i class="fas fa-book"></i> Livre Demandé</h6>
                            <p class="mb-1"><strong>Titre:</strong> ${pret.exemplaire.livre.titre}</p>
                            <p class="mb-1"><strong>Auteur:</strong> ${pret.exemplaire.livre.auteur.nom}</p>
                            <p class="mb-1"><strong>Exemplaire:</strong> #${pret.exemplaire.id}</p>
                            <p class="mb-0">
                                <strong>Type de prêt:</strong> 
                                <span class="badge ${pret.typePret.libelle == 'lire sur place' ? 'bg-warning text-dark' : 'bg-info'}">
                                    <c:choose>
                                        <c:when test="${pret.typePret.libelle == 'lire sur place'}">
                                            <i class="fas fa-clock"></i> ${pret.typePret.libelle} (retour le jour même)
                                        </c:when>
                                        <c:otherwise>
                                            <i class="fas fa-home"></i> ${pret.typePret.libelle}
                                        </c:otherwise>
                                    </c:choose>
                                </span>
                            </p>
                        </div>

                        <!-- Actions -->
                        <div class="text-center">
                            <form method="post" action="/prets/approve/${pret.id}" class="d-inline">
                                <button type="submit" class="btn btn-success btn-action">
                                    <i class="fas fa-check"></i> Approuver
                                </button>
                            </form>
                            <form method="post" action="/prets/reject/${pret.id}" class="d-inline">
                                <button type="submit" class="btn btn-danger btn-action" 
                                        onclick="return confirm('Êtes-vous sûr de vouloir rejeter cette demande ?')">
                                    <i class="fas fa-times"></i> Rejeter
                                </button>
                            </form>
                        </div>
                    </div>
                </div>
            </c:forEach>
        </div>
    </div>
</div>

<!-- Bootstrap JS -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
