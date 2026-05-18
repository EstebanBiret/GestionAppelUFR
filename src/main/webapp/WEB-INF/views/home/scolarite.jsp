<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="ut1.appel.entity.Users" %>
<%
    Users u = (Users) session.getAttribute("currentUser");
    if (u == null) { response.sendRedirect(request.getContextPath() + "/auth/login"); return; }
%>
<!DOCTYPE html>
<html lang="fr">
<head>
  <meta charset="UTF-8">
  <title>Scolarité — UT Capitole</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/scolarite_dashboard.css">
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
  <div class="welcome">
    <div class="welcome-bar"></div>
    <div>
      <div class="welcome-title">Bonjour, <%= u.getFirstName() %> <%= u.getLastName() %></div>
      <div class="welcome-sub">Espace Scolarité — que souhaitez-vous faire ?</div>
    </div>
  </div>

  <div class="nav-grid">
    <a href="${pageContext.request.contextPath}/scolarite/classes" class="nav-card">
      <div class="nav-icon">📚</div>
      <div>
        <div class="nav-card-title">Gestion des classes</div>
        <div class="nav-card-sub">Créer, modifier, affecter les étudiants</div>
      </div>
    </a>
  </div>
</main>

</body>
</html>