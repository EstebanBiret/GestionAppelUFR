<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="ut1.appel.entity.*, java.util.*" %>
<%
    Users u = (Users) session.getAttribute("currentUser");
    if (u == null) { response.sendRedirect(request.getContextPath() + "/auth/login"); return; }
    List<StudentClass> classes = (List<StudentClass>) request.getAttribute("classes");
    List<Users> enseignants    = (List<Users>) request.getAttribute("enseignants");
    String error = (String) request.getAttribute("error");
%>
<!DOCTYPE html>
<html lang="fr">
<head>
  <meta charset="UTF-8">
  <title>Nouveau cours — UT Capitole</title>
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

<main>
  <div class="page-header">
    <h1>Nouveau cours</h1>
    <a href="${pageContext.request.contextPath}/scolarite/cours">← Retour aux cours</a>
  </div>

  <% if (error != null) { %>
    <div class="error-msg"><%= error %></div>
  <% } %>

  <div class="card">
    <div class="card-header">Créer un cours</div>
    <div class="card-body">
      <form method="post" action="${pageContext.request.contextPath}/scolarite/cours/save">

        <div class="form-group" style="margin-bottom:1rem">
          <label for="name">Nom du cours</label>
          <input type="text" id="name" name="name" placeholder="Ex : Algorithmique" required>
        </div>

        <div class="form-row" style="margin-bottom:1.25rem">
          <div class="form-group">
            <label for="classId">Classe</label>
            <select id="classId" name="classId" required>
              <option value="">— Sélectionner —</option>
              <% if (classes != null) { for (StudentClass sc : classes) { %>
                <option value="<%= sc.getId() %>"><%= sc.getName() %></option>
              <% } } %>
            </select>
          </div>

          <div class="form-group">
            <label for="responsableId">Responsable</label>
            <select id="responsableId" name="responsableId" required>
              <option value="">— Sélectionner —</option>
              <% if (enseignants != null) { for (Users ens : enseignants) { %>
                <option value="<%= ens.getId() %>"><%= ens.getFirstName() %> <%= ens.getLastName() %></option>
              <% } } %>
            </select>
          </div>
        </div>

        <div class="form-footer">
          <a href="${pageContext.request.contextPath}/scolarite/cours"
            class="btn btn-secondary"
            onclick="return confirmCancel(event)">
            Annuler
          </a>
          <button type="submit" class="btn btn-primary">Créer le cours</button>
        </div>

      </form>
    </div>
  </div>
</main>
<script>
  window._contextPath = '<%= request.getContextPath() %>';
</script>
<script src="${pageContext.request.contextPath}/js/course.js"></script>
</body>
</html>