<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<style>
    /* Form container */
    .form-container {
        max-width: 900px;
        margin: 0 auto;
        background: white;
        padding: 2rem;
        border-radius: 8px;
        box-shadow: 0 2px 10px rgba(0,0,0,0.1);
    }
    
    /* Form title */
    .form-title {
        color: #2c3e50;
        margin-bottom: 1.5rem;
        padding-bottom: 0.5rem;
        border-bottom: 2px solid #3498db;
        font-size: 1.8rem;
        grid-column: 1 / -1;
    }
    
    /* Form grid layout */
    .form-grid {
        display: grid;
        grid-template-columns: repeat(3, 1fr);
        gap: 1.5rem;
    }
    
    /* Form elements */
    .form-group {
        margin-bottom: 0;
    }
    
    .form-group label {
        display: block;
        margin-bottom: 0.5rem;
        font-weight: 500;
        color: #2c3e50;
    }
    
    .form-control {
        width: 100%;
        padding: 0.75rem;
        border: 1px solid #ddd;
        border-radius: 4px;
        font-size: 1rem;
        transition: border-color 0.3s;
    }
    
    .form-control:focus {
        border-color: #3498db;
        outline: none;
        box-shadow: 0 0 0 3px rgba(52,152,219,0.2);
    }
    
    /* Full-width elements */
    .form-group.full-width {
        grid-column: 1 / -1;
    }
    
    textarea.form-control {
        min-height: 100px;
        resize: vertical;
    }
    
    /* Custom select arrow */
    select.form-control {
        background:
            linear-gradient(45deg, transparent 50%, #555 50%),
            linear-gradient(135deg, #555 50%, transparent 50%);
        background-position:
            calc(100% - 20px) calc(1em + 2px),
            calc(100% - 15px) calc(1em + 2px);
        background-size:
            5px 5px,
            5px 5px;
        background-repeat: no-repeat;
        padding-right: 30px;
    }
    
    /* Button styles */
    .btn-container {
        grid-column: 1 / -1;
        text-align: right;
        margin-top: 1rem;
    }
    
    /* Checkbox styles */
    .checkbox-group {
        display: flex;
        flex-wrap: wrap;
        gap: 1rem;
        margin-top: 0.5rem;
    }
    
    .checkbox-item {
        display: flex;
        align-items: center;
        gap: 0.5rem;
    }
    
    .checkbox-item input[type="checkbox"] {
        width: auto;
        margin: 0;
    }
    
    .checkbox-label {
        margin: 0 !important;
        font-weight: normal !important;
        cursor: pointer;
        user-select: none;
    }
    
    .checkbox-label:hover {
        color: #3498db;
    }
    
    .btn {
        display: inline-block;
        background-color: #3498db;
        color: white;
        padding: 0.75rem 1.5rem;
        border: none;
        border-radius: 4px;
        cursor: pointer;
        font-size: 1rem;
        font-weight: 500;
        transition: background-color 0.3s;
    }
    
    .btn:hover {
        background-color: #2980b9;
    }
    
    /* Responsive adjustments */
    @media (max-width: 768px) {
        .form-grid {
            grid-template-columns: 1fr;
        }
        
        .form-container {
            padding: 1.5rem;
        }
    }
</style>

<div class="form-container">
    <form:form method="post" action="${pageContext.request.contextPath}/adherents/save" modelAttribute="adherent" class="form-grid">
        <h2 class="form-title">Create New Adherent</h2>

        <div class="form-group">
            <label>Prénom:</label>
            <form:input path="prenom" cssClass="form-control" />
        </div>

        <div class="form-group">
            <label>Nom:</label>
            <form:input path="nom" cssClass="form-control" />
        </div>

        <div class="form-group">
            <label>Email:</label>
            <form:input path="email" cssClass="form-control" />
        </div>

        <div class="form-group">
            <label>Type d'Adhérent:</label>
            <form:select path="typeAdherent.id" cssClass="form-control">
                <form:option value="">-- Choisir --</form:option>
                <form:options items="${typesAdherents}" itemValue="id" itemLabel="libelle" />
            </form:select>
        </div>

        <div class="form-group">
            <label>Téléphone:</label>
            <form:input path="telephone" cssClass="form-control" />
        </div>

        <div class="form-group full-width">
            <label>Adresse:</label>
            <form:textarea path="adresse" cssClass="form-control" />
        </div>
        
        <div class="form-group">
            <label>Date d'inscription:</label>
            <form:input path="dateInscription" type="datetime-local" cssClass="form-control" />
        </div>

        <div class="form-group full-width">
            <label>Contraintes:</label>
            <div class="checkbox-group">
                <c:forEach var="contrainte" items="${contraintes}">
                    <div class="checkbox-item">
                        <form:checkbox path="contraintes" value="${contrainte.id}" id="contrainte_${contrainte.id}" />
                        <label for="contrainte_${contrainte.id}" class="checkbox-label">${contrainte.typeContrainte}</label>
                    </div>
                </c:forEach>
            </div>
        </div>
        
        <div class="btn-container">
            <button type="submit" class="btn">Save</button>
        </div>
    </form:form>
</div>