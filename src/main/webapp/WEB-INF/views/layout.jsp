<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
<head>
    <title>My Library App</title>
</head>
<body>

<header>
    <h1>My Library</h1>
    <nav>
        <a href="${pageContext.request.contextPath}/">Home</a> |
        <a href="${pageContext.request.contextPath}/adherents/new">Add Adherent</a>
    </nav>
    <hr/>
</header>

<main>
    <!-- Display messages -->
    <c:if test="${not empty errorMessage}">
        <p style="color: red;">${errorMessage}</p>
    </c:if>

    <c:if test="${not empty successMessage}">
        <p style="color: green;">${successMessage}</p>
    </c:if>

    <!-- Main dynamic content -->
    <jsp:include page="${contentPage}.jsp" />
</main>

<footer>
    <hr/>
    <p>&copy; 2025 My Library App</p>
</footer>

</body>
</html>
