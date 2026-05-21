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
  <title>Modifier mon profil</title>
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
  <div class="welcome">
    <div class="welcome-bar"></div>
    <div>
      <div class="welcome-title">Modifier mon profil</div>
      <div class="welcome-sub">Laissez un champ vide pour ne pas le modifier</div>
    </div>
  </div>

  <% if (request.getAttribute("error") != null) { %>
  <div class="alert-error"><%= request.getAttribute("error") %></div>
  <% } %>

  <form action="${pageContext.request.contextPath}/profil/modifier"
        method="post" enctype="multipart/form-data" class="profile-form">

    <div class="form-group">
      <label>Photo actuelle</label>
      <img src="${pageContext.request.contextPath}/<%= u.getPicturePath() %>"
           alt="Photo actuelle" class="profile-picture-small"/>
    </div>

    <div class="form-group">
      <label for="photo">Nouvelle photo (optionnel)</label>
      <input type="file" id="photo" name="photo" accept="image/*"/>
    </div>

    <div class="form-group">
      <label for="email">Nouvel email (optionnel)</label>
      <input type="email" id="email" name="email" value="<%= u.getEmail() %>"/>
    </div>

    <div class="form-actions">
      <a href="${pageContext.request.contextPath}/profil/voir" class="btn-secondary">Annuler</a>
      <button type="submit" class="btn-primary">💾 Enregistrer</button>
    </div>
  </form>
</main>
</body>
</html>