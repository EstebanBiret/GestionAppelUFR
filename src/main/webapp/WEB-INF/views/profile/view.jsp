<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="ut1.appel.entity.Users" %>
<%@ page import="ut1.appel.enums.Role" %>
<%
    Users u = (Users) session.getAttribute("currentUser");
    if (u == null) { response.sendRedirect(request.getContextPath() + "/auth/login"); return; }
    boolean success = "1".equals(request.getParameter("success"));
    String pic = u.getPicturePath() != null ? u.getPicturePath() : "default.jpg";

    String dashboardUrl;
    Role role = u.getRole();
    if (role == Role.ADMIN)                                          dashboardUrl = "/admin";
    else if (role == Role.SCOLARITE)                                 dashboardUrl = "/scolarite";
    else if (role == Role.ENSEIGNANT)                                dashboardUrl = "/enseignant";
    else if (role == Role.ETUDIANT_FI || role == Role.ETUDIANT_FA)  dashboardUrl = "/etudiant";
    else                                                             dashboardUrl = "/auth/pending";
%>
<!DOCTYPE html>
<html lang="fr">
<head>
  <meta charset="UTF-8">
  <title>Mon profil — UT Capitole</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/student.css">
</head>
<body>
<header>
  <div class="logo">
    <div class="logo-bar"></div>
    <div>
      <div class="logo-text">Université Toulouse Capitole</div>
      <div class="logo-sub">Gestion de l'appel</div>
    </div>
  </div>
  <div class="header-right">
    <span class="user-chip"><%= u.getFirstName() %> <%= u.getLastName() %></span>
    <a href="${pageContext.request.contextPath}/auth/logout" class="btn-logout">Se déconnecter</a>
  </div>
</header>

<main>
  <div class="page-header">
    <h1>Mon profil</h1>
    <a href="${pageContext.request.contextPath}<%= dashboardUrl %>">← Tableau de bord</a>
  </div>

  <% if (success) { %>
    <div class="alert-success">Profil mis à jour avec succès.</div>
  <% } %>

  <div class="profile-card">
    <img class="profile-picture"
         src="${pageContext.request.contextPath}/<%= pic %>"
         alt="Photo de profil">

    <div class="profile-info">
      <p><strong><%= u.getFirstName() %> <%= u.getLastName() %></strong></p>
      <p><%= u.getEmail() %></p>
    </div>

    <a href="${pageContext.request.contextPath}/profil/modifier" class="btn-primary">
      Modifier mon profil
    </a>
  </div>
</main>
</body>
</html>