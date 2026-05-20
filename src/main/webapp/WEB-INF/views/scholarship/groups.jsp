<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="ut1.appel.entity.*, java.util.*" %>
<%
    Users u = (Users) session.getAttribute("currentUser");
    if (u == null) { response.sendRedirect(request.getContextPath() + "/auth/login"); return; }
    List<StudentGroup> groupes = (List<StudentGroup>) request.getAttribute("groupes");
%>
<!DOCTYPE html>
<html lang="fr">
<head>
  <meta charset="UTF-8">
  <title>Groupes de TD — UT Capitole</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/classes.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/groups.css">
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
    <h1>Groupes de TD</h1>
    <a href="${pageContext.request.contextPath}/scolarite">← Tableau de bord</a>
  </div>

  <div class="card">
    <table class="classes-table" id="groupesTable">
      <thead>
        <tr>
          <th></th>
          <th>Nom du groupe</th>
          <th>Classe</th>
          <th>Étudiants</th>
        </tr>
      </thead>
      <tbody>
        <% if (groupes == null || groupes.isEmpty()) { %>
          <tr><td colspan="5" class="empty">Aucun groupe créé pour le moment.</td></tr>
        <% } else { for (StudentGroup g : groupes) { %>
          <tr onclick="selectGroupe(<%= g.getId() %>, this)">
            <td><input type="radio" name="selectedGroupe" value="<%= g.getId() %>" id="grp_<%= g.getId() %>"></td>
            <td><strong><%= g.getName() %></strong></td>
            <td class="groups-meta"><%= g.getStudentClass() != null ? g.getStudentClass().getName() : "—" %></td>
            <td><span class="badge"><%= g.getUsers() != null ? g.getUsers().size() : 0 %></span></td>
          </tr>
        <% } } %>
      </tbody>
    </table>
    <div class="actions">
      <button id="btnModifier" class="btn btn-secondary" disabled onclick="goEdit()">✏ Modifier</button>
      <a href="${pageContext.request.contextPath}/scolarite/groupes/form" class="btn btn-primary">+ Créer un groupe</a>
    </div>
  </div>
</main>

<script>
  window._contextPath = '<%= request.getContextPath() %>';
</script>
<script src="${pageContext.request.contextPath}/js/groups.js"></script>
</body>
</html>