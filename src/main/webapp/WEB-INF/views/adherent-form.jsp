<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<h2>Create New Adherent</h2>

<form:form method="post" action="${pageContext.request.contextPath}/adherents/save" modelAttribute="adherent">
    <label>Prénom:</label>
    <form:input path="prenom" /><br/>

    <label>Nom:</label>
    <form:input path="nom" /><br/>

    <label>Email:</label>
    <form:input path="email" /><br/>

    <label>Type d'Adhérent:</label>
    <form:select path="typeAdherent.id">
        <form:option value="">-- Choisir --</form:option>
        <form:options items="${typesAdherents}" itemValue="id" itemLabel="libelle" />
    </form:select><br/>

    <label>Téléphone:</label>
    <form:input path="telephone" /><br/>

    <label>Adresse:</label>
    <form:textarea path="adresse" /><br/>

    <button type="submit">Save</button>
</form:form>
