<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="ut1.appel.entity.*, java.util.*" %>
<%
    Users u = (Users) session.getAttribute("currentUser");
    if (u == null) { response.sendRedirect(request.getContextPath() + "/auth/login"); return; }
    List<Course> cours = (List<Course>) request.getAttribute("cours");
%>
<!DOCTYPE html>
<html lang="fr">
<head>
  <meta charset="UTF-8">
  <title>Cours — UT Capitole</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/courses.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/classes.css">
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

<main class="main-wide">
  <div class="page-header">
    <h1>Cours &amp; Séances</h1>
    <a href="${pageContext.request.contextPath}/scolarite">← Tableau de bord</a>
  </div>

  <div class="cours-layout">

    <div class="card cours-panel">
      <div class="card-header">Liste des cours</div>
      <table class="classes-table" id="coursTable">
        <thead>
          <tr>
            <th></th>
            <th>Nom du cours</th>
            <th>Classe</th>
            <th>Responsable</th>
          </tr>
        </thead>
        <tbody>
          <% if (cours == null || cours.isEmpty()) { %>
            <tr><td colspan="4" class="empty">Aucun cours créé pour le moment.</td></tr>
          <% } else { for (Course c : cours) { %>
            <tr onclick="selectCours(<%= c.getId() %>, this)">
              <td><input type="radio" name="selectedCours" value="<%= c.getId() %>" id="crs_<%= c.getId() %>"></td>
              <td><strong><%= c.getName() %></strong></td>
              <td><span class="badge"><%= c.getStudentClass() != null ? c.getStudentClass().getName() : "—" %></span></td>
              <td>
                <% if (c.getResponsable() != null) { %>
                  <%= c.getResponsable().getFirstName() %> <%= c.getResponsable().getLastName() %>
                <% } else { %>
                  <span style="color:var(--txt-muted)">—</span>
                <% } %>
              </td>
            </tr>
          <% } } %>
        </tbody>
      </table>
      <div class="actions">
        <a href="${pageContext.request.contextPath}/scolarite/cours/form" class="btn btn-primary">+ Créer un cours</a>
      </div>
    </div>

    <div class="card seances-panel">
      <div class="card-header">Séances</div>
      <div class="seances-body" id="seancesBody">
        <div class="seances-placeholder">
          <div class="seances-placeholder-icon">📅</div>
          <div class="seances-placeholder-text">Sélectionnez un cours pour afficher ses séances.</div>
        </div>
      </div>
    </div>

  </div>
</main>

<script>
  window._contextPath = '<%= request.getContextPath() %>';
</script>
<script src="${pageContext.request.contextPath}/js/course.js"></script>
</body>
</html>