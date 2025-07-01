<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
<head>
    <title>My Library App</title>
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
        
        .sidebar-nav a:hover {
            background-color: #34495e;
            color: #3498db;
        }
        
        .sidebar-nav a:before {
            content: "→";
            font-size: 0.8rem;
            opacity: 0;
            transition: all 0.3s;
        }
        
        .sidebar-nav a:hover:before {
            opacity: 1;
            margin-right: 0.5rem;
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
        <a href="${pageContext.request.contextPath}/">Home</a>
        <a href="${pageContext.request.contextPath}/adherents/new">Add Adherent</a>
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

</body>
</html>