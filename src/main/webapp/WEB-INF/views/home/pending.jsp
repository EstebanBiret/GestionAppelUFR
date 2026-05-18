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
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet"
          integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH" crossorigin="anonymous">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/pending.css">
</head>
<body>
<div class="pending-container">
    <img src="${pageContext.request.contextPath}/pictures/logo_utc.jpg"
         alt="Université Toulouse Capitole">
    <div class="pending-icon">⏳</div>
    <h2>Bienvenue <%= u.getFirstName() %> <%= u.getLastName() %></h2>
    <p>Votre compte est en attente d'activation par un administrateur.</p>
    <a href="${pageContext.request.contextPath}/auth/logout">Se déconnecter</a>
</div>
</body>
</html>