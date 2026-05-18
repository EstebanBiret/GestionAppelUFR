<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="ut1.appel.entity.*, java.util.*" %>
<%
    Users u = (Users) session.getAttribute("currentUser");
    if (u == null) { response.sendRedirect(request.getContextPath() + "/auth/login"); return; }
    List<StudentClass> classes = (List<StudentClass>) request.getAttribute("classes");
%>
<!DOCTYPE html>
<html lang="fr">
<head>
  <meta charset="UTF-8">
  <title>Gestion des classes — UT Capitole</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
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

<main>
  <div class="page-header">
    <h1>Gestion des classes</h1>
    <a href="${pageContext.request.contextPath}/scolarite">← Tableau de bord</a>
  </div>

  <div class="card">
    <table class="classes-table" id="classesTable">
      <thead>
        <tr>
          <th></th>
          <th>Nom de la classe</th>
          <th>Étudiants</th>
          <th>Groupes TD</th>
        </tr>
      </thead>
      <tbody>
        <% if (classes == null || classes.isEmpty()) { %>
          <tr><td colspan="4" class="empty">Aucune classe créée pour le moment.</td></tr>
        <% } else { for (StudentClass c : classes) { %>
          <tr onclick="selectClass(<%= c.getId() %>, this)">
            <td><input type="radio" name="selectedClass" value="<%= c.getId() %>" id="cls_<%= c.getId() %>"></td>
            <td><strong><%= c.getName() %></strong></td>
            <td><span class="badge"><%= c.getUsers() != null ? c.getUsers().size() : 0 %></span></td>
            <td><span class="badge"><%= c.getGroups() != null ? c.getGroups().size() : 0 %></span></td>
          </tr>
        <% } } %>
      </tbody>
    </table>
    <div class="actions">
      <button id="btnModifier" class="btn btn-secondary" disabled onclick="goEdit()">✏ Modifier</button>
      <a href="${pageContext.request.contextPath}/scolarite/classes/form" class="btn btn-primary">+ Créer une classe</a>
    </div>
  </div>
</main>

<script>
  const contextPath = '<%= request.getContextPath() %>';
</script>
<script src="${pageContext.request.contextPath}/js/classes.js"></script>
</body>
</html>