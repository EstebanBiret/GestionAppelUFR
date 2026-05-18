<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="ut1.appel.entity.Users" %>
<%
    Users u = (Users) session.getAttribute("currentUser");
    if (u == null) {
        response.sendRedirect(request.getContextPath() + "/auth/login");
        return;
    }
%>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>En attente d'activation</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/auth.css">
</head>
<body>

<div class="auth-header">
    <img src="${pageContext.request.contextPath}/images/logo_utc.jpg" alt="Université Toulouse Capitole">
</div>

<div class="auth-container pending">
    <div class="pending-icon">⏳</div>

    <h2>Bienvenue <%= u.getFirstName() %> <%= u.getLastName() %></h2>

    <p class="pending-text">Votre compte est en attente d'activation par un administrateur.</p>

    <div class="form-action">
        <a href="${pageContext.request.contextPath}/auth/logout" class="btn btn-primary">Se déconnecter</a>
    </div>
</div>

</body>
</html>