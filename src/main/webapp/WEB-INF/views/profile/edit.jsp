<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="ut1.appel.entity.Users" %>
<%
    Users u = (Users) session.getAttribute("currentUser");
    if (u == null) { response.sendRedirect(request.getContextPath() + "/auth/login"); return; }
    String error = (String) request.getAttribute("error");
    String pic   = u.getPicturePath() != null ? u.getPicturePath() : "default.jpg";
%>
<!DOCTYPE html>
<html lang="fr">
<head>
  <meta charset="UTF-8">
  <title>Modifier le profil — UT Capitole</title>
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
    <h1>Modifier mon profil</h1>
    <a href="${pageContext.request.contextPath}/profil/voir">← Retour au profil</a>
  </div>

  <% if (error != null) { %>
    <div class="alert-error"><%= error %></div>
  <% } %>

  <form class="profile-form"
        method="post"
        action="${pageContext.request.contextPath}/profil/save"
        enctype="multipart/form-data">

    <div class="form-group">
      <label>Photo de profil actuelle</label>
      <img class="profile-picture-small"
           src="${pageContext.request.contextPath}/<%= pic %>"
           alt="Photo actuelle"
           id="previewImg">
    </div>

    <div class="form-group">
      <label for="photo">Changer la photo</label>
      <input type="file" id="photo" name="photo"
             accept="image/jpeg,image/png,image/gif,image/webp"
             onchange="previewPhoto(this)">
    </div>

    <div class="form-group">
      <label for="email">Adresse email</label>
      <input type="email" id="email" name="email"
             value="<%= u.getEmail() %>" required>
    </div>

    <div class="form-actions">
      <a href="${pageContext.request.contextPath}/profil/voir" class="btn-secondary">Annuler</a>
      <button type="submit" class="btn-primary">Enregistrer</button>
    </div>

  </form>
</main>

<script>
  function previewPhoto(input) {
    if (input.files && input.files[0]) {
      const reader = new FileReader();
      reader.onload = e => document.getElementById('previewImg').src = e.target.result;
      reader.readAsDataURL(input.files[0]);
    }
  }
</script>
</body>
</html>