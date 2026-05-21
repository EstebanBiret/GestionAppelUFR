<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="ut1.appel.entity.Users" %>
<%
    Users u = (Users) session.getAttribute("currentUser");
    if (u == null) {
        response.sendRedirect(request.getContextPath() + "/auth/login");
        return;
    }
    boolean success = "1".equals(request.getParameter("success"));
%>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Mon profil</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/student.css">
</head>
<body>

<header>
    <div class="logo">
        <div class="logo-bar"></div>
        <div>
            <div class="logo-text">UT Capitole</div>
            <div class="logo-sub">Gestion des absences</div>
        </div>
    </div>
    <div class="header-right">
        <div class="user-chip"><%= u.getFirstName() %> <%= u.getLastName() %></div>
        <a href="${pageContext.request.contextPath}/auth/logout" class="btn-logout">Se déconnecter</a>
    </div>
</header>

<main>
    <% if (success) { %>
    <div class="alert-success"> Profil mis à jour avec succès.</div>
    <% } %>

    <div class="welcome">
        <div class="welcome-bar"></div>
        <div>
            <div class="welcome-title">Mon profil</div>
            <div class="welcome-sub">Informations de votre compte</div>
        </div>
    </div>

    <div class="profile-card">
        <img src="${pageContext.request.contextPath}/<%= u.getPicturePath() %>"
             alt="Photo de profil" class="profile-picture"/>
        <div class="profile-info">
            <p><strong>Prénom :</strong> <%= u.getFirstName() %></p>
            <p><strong>Nom :</strong> <%= u.getLastName() %></p>
            <p><strong>Email :</strong> <%= u.getEmail() %></p>
            <p><strong>Rôle :</strong> <%= u.getRole() %></p>
        </div>
        <a href="${pageContext.request.contextPath}/profil/modifier" class="btn-primary">
             Modifier mon profil
        </a>
    </div>
</main>
</body>
</html>