<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<style>
    /* Page title */
    .page-title {
        color: #2c3e50;
        margin-bottom: 2rem;
        padding-bottom: 0.5rem;
        border-bottom: 2px solid #3498db;
        font-size: 2rem;
    }
    
    /* Table styles */
    .adherents-table {
        width: 100%;
        background: white;
        border-radius: 8px;
        box-shadow: 0 2px 10px rgba(0,0,0,0.1);
        overflow: hidden;
    }
    
    .adherents-table table {
        width: 100%;
        border-collapse: collapse;
    }
    
    .adherents-table th {
        background-color: #34495e;
        color: white;
        padding: 1rem;
        text-align: left;
        font-weight: 600;
        border-bottom: 2px solid #2c3e50;
    }
    
    .adherents-table td {
        padding: 1rem;
        border-bottom: 1px solid #ecf0f1;
        vertical-align: middle;
    }
    
    .adherents-table tr:hover {
        background-color: #f8f9fa;
    }
    
    .adherents-table tr:last-child td {
        border-bottom: none;
    }
    
    /* Status badge */
    .status-badge {
        display: inline-block;
        padding: 0.25rem 0.75rem;
        border-radius: 20px;
        font-size: 0.875rem;
        font-weight: 500;
        background-color: #f39c12;
        color: white;
    }
    
    /* Action buttons */
    .action-buttons {
        display: flex;
        gap: 0.5rem;
    }
    
    .btn {
        padding: 0.5rem 1rem;
        border: none;
        border-radius: 4px;
        cursor: pointer;
        text-decoration: none;
        display: inline-block;
        font-size: 0.875rem;
        font-weight: 500;
        transition: background-color 0.3s;
    }
    
    .btn-approve {
        background-color: #27ae60;
        color: white;
    }
    
    .btn-approve:hover {
        background-color: #219a52;
    }
    
    .btn-reject {
        background-color: #e74c3c;
        color: white;
    }
    
    .btn-reject:hover {
        background-color: #c0392b;
    }
    
    .btn-view {
        background-color: #3498db;
        color: white;
    }
    
    .btn-view:hover {
        background-color: #2980b9;
    }
    
    /* Empty state */
    .empty-state {
        text-align: center;
        padding: 3rem;
        color: #7f8c8d;
    }
    
    .empty-state h3 {
        margin-bottom: 1rem;
        color: #95a5a6;
    }
    
    .empty-state p {
        font-size: 1.1rem;
    }
    
    /* Responsive */
    @media (max-width: 768px) {
        .adherents-table {
            overflow-x: auto;
        }
        
        .action-buttons {
            flex-direction: column;
        }
        
        .btn {
            text-align: center;
        }
    }
</style>

<div>
    <h1 class="page-title">Inscriptions à Valider</h1>
    
    <c:choose>
        <c:when test="${not empty adherentsAValider}">
            <div class="adherents-table">
                <table>
                    <thead>
                        <tr>
                            <th>Numéro Adhérent</th>
                            <th>Nom</th>
                            <th>Prénom</th>
                            <th>Email</th>
                            <th>Téléphone</th>
                            <th>Type</th>
                            <th>Date Inscription</th>
                            <th>Date Changement</th>
                            <th>Statut</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="adherent" items="${adherentsAValider}">
                            <tr>
                                <td>${adherent.numeroAdherent}</td>
                                <td>${adherent.nom}</td>
                                <td>${adherent.prenom}</td>
                                <td>${adherent.email}</td>
                                <td>${adherent.telephone != null ? adherent.telephone : '-'}</td>
                                <td>${adherent.typeAdherent.libelle}</td>
                                <td>${adherent.dateInscriptionAsDate}</td>
                                <td>
                                    <input type="datetime-local" 
                                           id="dateChangement_${adherent.id}"
                                           value="${adherent.formattedDateInscription}" 
                                           style="padding: 0.25rem; border: 1px solid #ddd; border-radius: 4px; font-size: 0.875rem; width: 100%;" />
                                </td>
                                <td>
                                    <span class="status-badge">En demande</span>
                                </td>
                                <td>
                                    <div class="action-buttons">
                                        <form method="post" 
                                              action="${pageContext.request.contextPath}/adherents/approve-adherent/${adherent.id}" 
                                              style="display: inline-block; margin: 0;">
                                            <input type="hidden" name="dateChangement" value="" />
                                            <button type="submit" 
                                                    class="btn btn-approve" 
                                                    title="Approuver l'inscription"
                                                    onclick="this.form.dateChangement.value = document.getElementById('dateChangement_${adherent.id}').value;">
                                                Approuver
                                            </button>
                                        </form>
                                        <a href="#" class="btn btn-reject" title="Rejeter l'inscription">Rejeter</a>
                                    </div>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </c:when>
        <c:otherwise>
            <div class="empty-state">
                <h3>Aucune inscription en attente</h3>
                <p>Il n'y a actuellement aucune inscription à valider.</p>
            </div>
        </c:otherwise>
    </c:choose>
</div>
