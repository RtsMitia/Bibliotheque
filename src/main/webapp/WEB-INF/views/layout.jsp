<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
<head>
    <title>${not empty pageTitle ? pageTitle : 'My Library App'}</title>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    
    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- FontAwesome Icons -->
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    
    <style>
        /* Reset and base styles */
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
        }
        
        body {
            background-color: #f5f5f5;
            color: #333;
            line-height: 1.6;
            display: flex;
            min-height: 100vh;
        }
        
        /* Sidebar styles */
        .sidebar {
            width: 250px;
            background-color: #2c3e50;
            color: white;
            padding: 1.5rem 1rem;
            position: sticky;
            top: 0;
            height: 100vh;
            transition: all 0.3s;
            box-shadow: 2px 0 5px rgba(0,0,0,0.1);
        }
        
        .sidebar h1 {
            color: white;
            margin-bottom: 2rem;
            padding-bottom: 1rem;
            border-bottom: 1px solid rgba(255,255,255,0.1);
            font-size: 1.5rem;
        }
        
        .sidebar-nav {
            display: flex;
            flex-direction: column;
            gap: 0.5rem;
        }
        
        .sidebar-nav a {
            color: #ecf0f1;
            text-decoration: none;
            padding: 0.75rem 1rem;
            border-radius: 4px;
            transition: all 0.3s;
            display: flex;
            align-items: center;
            gap: 0.75rem;
        }
        
        .sidebar-nav a i {
            width: 16px;
            text-align: center;
        }
        
        .sidebar-nav a:hover {
            background-color: #34495e;
            color: #3498db;
            transform: translateX(5px);
        }
        
        /* Dropdown section styles */
        .nav-section {
            margin-bottom: 0.5rem;
        }
        
        .nav-section-header {
            color: #ecf0f1;
            text-decoration: none;
            padding: 0.75rem 1rem;
            border-radius: 4px;
            transition: all 0.3s;
            display: flex;
            align-items: center;
            justify-content: space-between;
            cursor: pointer;
            background: none;
            border: none;
            width: 100%;
            text-align: left;
            font-size: inherit;
            font-family: inherit;
        }
        
        .nav-section-header:hover {
            background-color: #34495e;
            color: #3498db;
        }
        
        .nav-section-header i.dropdown-arrow {
            transition: transform 0.3s;
            font-size: 0.8rem;
        }
        
        .nav-section-header.active i.dropdown-arrow {
            transform: rotate(180deg);
        }
        
        .nav-section-content {
            max-height: 0;
            overflow: hidden;
            transition: max-height 0.3s ease-out;
            padding-left: 1rem;
        }
        
        .nav-section-content.active {
            max-height: 200px; /* Adjust based on content */
        }
        
        .nav-section-content a {
            padding: 0.5rem 1rem;
            margin: 0.2rem 0;
            font-size: 0.9rem;
            border-left: 2px solid transparent;
        }
        
        .nav-section-content a:hover {
            border-left-color: #3498db;
            background-color: rgba(52, 73, 94, 0.5);
        }
        
        /* Main content styles */
        .main-content {
            flex: 1;
            padding: 2rem;
            overflow-y: auto;
        }
        
        /* Message styles */
        .message {
            padding: 1rem;
            margin-bottom: 1.5rem;
            border-radius: 4px;
            font-weight: 500;
        }
        
        .error-message {
            background-color: #ffebee;
            border-left: 4px solid #f44336;
            color: #d32f2f;
        }
        
        .success-message {
            background-color: #e8f5e9;
            border-left: 4px solid #4caf50;
            color: #2e7d32;
        }
        
        /* Footer styles */
        footer {
            background-color: #2c3e50;
            color: #ecf0f1;
            text-align: center;
            padding: 1rem;
            margin-top: 2rem;
            border-radius: 4px;
        }
        
        footer p {
            margin: 0;
            font-size: 0.9rem;
        }
        
        /* Responsive design */
        @media (max-width: 768px) {
            body {
                flex-direction: column;
            }
            
            .sidebar {
                width: 100%;
                height: auto;
                position: relative;
                padding: 1rem;
            }
            
            .sidebar h1 {
                margin-bottom: 1rem;
            }
            
            .sidebar-nav {
                flex-direction: row;
                flex-wrap: wrap;
            }
            
            .sidebar-nav a {
                padding: 0.5rem;
            }
            
            .main-content {
                padding: 1rem;
            }
        }
    </style>
</head>
<body>

<!-- Sidebar Navigation -->
<div class="sidebar">
    <h1>My Library</h1>
    <nav class="sidebar-nav">
        <a href="${pageContext.request.contextPath}/"><i class="fas fa-home"></i> Home</a>
        <a href="${pageContext.request.contextPath}/adherents/new"><i class="fas fa-user-plus"></i> Add Adherent</a>
        <a href="${pageContext.request.contextPath}/adherents/inscription-a-valider"><i class="fas fa-user-check"></i> Inscription à Valider</a>
        
        <!-- Livres Section with Dropdown -->
        <div class="nav-section">
            <button class="nav-section-header" onclick="toggleSection('livres-section')">
                <span><i class="fas fa-book"></i> Livres</span>
                <i class="fas fa-chevron-down dropdown-arrow"></i>
            </button>
            <div class="nav-section-content" id="livres-section">
                <a href="${pageContext.request.contextPath}/livres/list"><i class="fas fa-list"></i> Liste des Livres</a>
                <a href="${pageContext.request.contextPath}/livres/add"><i class="fas fa-plus"></i> Ajouter un Livre</a>
            </div>
        </div>
    </nav>
</div>

<!-- Main Content -->
<div class="main-content">
    <!-- Display messages -->
    <c:if test="${not empty errorMessage}">
        <div class="message error-message">${errorMessage}</div>
    </c:if>

    <c:if test="${not empty successMessage}">
        <div class="message success-message">${successMessage}</div>
    </c:if>

    <!-- Main dynamic content -->
    <jsp:include page="${contentPage}.jsp" />
    
    <footer>
        <p>&copy; 2025 My Library App</p>
    </footer>
</div>

<!-- Bootstrap JavaScript -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>

<!-- Custom Navigation JavaScript -->
<script>
    function toggleSection(sectionId) {
        const content = document.getElementById(sectionId);
        const header = content.previousElementSibling;
        
        // Toggle active class on content
        content.classList.toggle('active');
        
        // Toggle active class on header (for arrow rotation)
        header.classList.toggle('active');
    }
    
    // Auto-open section if current page is within it
    document.addEventListener('DOMContentLoaded', function() {
        const currentPath = window.location.pathname;
        
        // Check if we're on a livres page
        if (currentPath.includes('/livres/')) {
            const livresSection = document.getElementById('livres-section');
            const livresHeader = livresSection.previousElementSibling;
            
            livresSection.classList.add('active');
            livresHeader.classList.add('active');
        }
    });
</script>

</body>
</html>