<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<style>
.json-container {
    background-color: #f8f9fa;
    border: 1px solid #2c3e50;
    border-radius: 8px;
    padding: 20px;
    margin: 10px 0;
    font-family: 'Courier New', monospace;
    white-space: pre-wrap;
    overflow-x: auto;
}

.button-group {
    margin: 20px 0;
}

.btn {
    background-color: #2c3e50;
    color: white;
    border: none;
    padding: 10px 20px;
    margin: 0 10px;
    border-radius: 4px;
    cursor: pointer;
}

.btn:hover {
    background-color: #1a252f;
}

.endpoint-url {
    background-color: #e9ecef;
    padding: 10px;
    border-radius: 4px;
    font-family: 'Courier New', monospace;
    margin: 10px 0;
}
</style>

<div class="container-fluid">
    <h2 style="color: #2c3e50;">API JSON - Endpoints de Livres</h2>
    
    <div class="button-group">
        <button class="btn" onclick="loadAllLivres()">Charger tous les livres</button>
        <button class="btn" onclick="loadLivreById()">Charger livre par ID</button>
        <button class="btn" onclick="searchLivres()">Rechercher livres</button>
    </div>

    <div class="endpoint-url" id="current-endpoint">
        Endpoint: Aucun sélectionné
    </div>

    <div class="json-container" id="json-output">
        Cliquez sur un bouton pour charger les données JSON
    </div>
</div>

<script>
function loadAllLivres() {
    const endpoint = '/livres/api/all';
    document.getElementById('current-endpoint').textContent = 'Endpoint: GET ' + endpoint;
    
    fetch(endpoint)
        .then(response => response.json())
        .then(data => {
            document.getElementById('json-output').textContent = JSON.stringify(data, null, 2);
        })
        .catch(error => {
            document.getElementById('json-output').textContent = 'Erreur: ' + error.message;
        });
}

function loadLivreById() {
    const id = prompt('Entrez l\'ID du livre:');
    if (!id) return;
    
    const endpoint = '/livres/api/' + id;
    document.getElementById('current-endpoint').textContent = 'Endpoint: GET ' + endpoint;
    
    fetch(endpoint)
        .then(response => {
            if (!response.ok) {
                throw new Error('Livre non trouvé (Status: ' + response.status + ')');
            }
            return response.json();
        })
        .then(data => {
            document.getElementById('json-output').textContent = JSON.stringify(data, null, 2);
        })
        .catch(error => {
            document.getElementById('json-output').textContent = 'Erreur: ' + error.message;
        });
}

function searchLivres() {
    const searchTerm = prompt('Entrez le terme de recherche:');
    if (!searchTerm) return;
    
    const endpoint = '/livres/api/search?searchTerm=' + encodeURIComponent(searchTerm);
    document.getElementById('current-endpoint').textContent = 'Endpoint: GET ' + endpoint;
    
    fetch(endpoint)
        .then(response => response.json())
        .then(data => {
            document.getElementById('json-output').textContent = JSON.stringify(data, null, 2);
        })
        .catch(error => {
            document.getElementById('json-output').textContent = 'Erreur: ' + error.message;
        });
}

// Load all books by default when page loads
document.addEventListener('DOMContentLoaded', function() {
    loadAllLivres();
});
</script>
