<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="ut1.appel.entity.*, ut1.appel.enums.Role, java.util.*" %>
<%
    Users u = (Users) session.getAttribute("currentUser");
    if (u == null) { response.sendRedirect(request.getContextPath() + "/auth/login"); return; }
    StudentClass editClass  = (StudentClass) request.getAttribute("studentClass");
    List<StudentClass> allClasses = (List<StudentClass>) request.getAttribute("classes");
    List<Users> students    = (List<Users>) request.getAttribute("students");
    boolean isEdit = editClass != null;
    String error   = (String) request.getAttribute("error");
%>
<!DOCTYPE html>
<html lang="fr">
<head>
  <meta charset="UTF-8">
  <title><%= isEdit ? "Modifier " + editClass.getName() : "Nouvelle classe" %> — UT Capitole</title>
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
    <h1><%= isEdit ? "Modifier la classe" : "Nouvelle classe" %></h1>
    <a href="${pageContext.request.contextPath}/scolarite/classes">← Retour aux classes</a>
  </div>

  <% if (error != null) { %>
    <div class="error-msg">⚠ <%= error %></div>
  <% } %>

  <div class="card">
    <div class="card-header">Nom de la classe</div>
    <div class="card-body">
      <form method="post" action="${pageContext.request.contextPath}/scolarite/classes/save">
        <% if (isEdit) { %>
          <input type="hidden" name="id" value="<%= editClass.getId() %>">
        <% } %>
        <div class="form-row">
          <div class="form-group">
            <label for="name">Nom</label>
            <input type="text" id="name" name="name"
                   value="<%= isEdit ? editClass.getName() : "" %>"
                   placeholder="Ex : M2 MIAGE IPM" required>
          </div>
          <button type="submit" class="btn btn-primary">
            <%= isEdit ? "Enregistrer" : "Créer la classe" %>
          </button>
        </div>
      </form>
    </div>
  </div>

  <div class="card">
    <div class="card-header">
      Affectation des étudiants
      <span style="font-weight:400; color:var(--txt-muted); font-size:.8rem; margin-left:.5rem;">
        — Modifiez la classe de chaque étudiant puis cliquez sur Enregistrer
      </span>
    </div>
    <form method="post" action="${pageContext.request.contextPath}/scolarite/classes/save-students">
      <% if (students == null || students.isEmpty()) { %>
        <p class="empty">Aucun étudiant inscrit dans l'application.</p>
      <% } else { %>
      <table class="students-table">
        <thead>
          <tr>
            <th>Étudiant</th>
            <th>Formation</th>
            <th>Classe</th>
          </tr>
        </thead>
        <tbody>
          <% for (Users etudiant : students) {
              String initials = etudiant.getFirstName().substring(0,1).toUpperCase()
                              + etudiant.getLastName().substring(0,1).toUpperCase();
              boolean isFI = etudiant.getRole() == Role.ETUDIANT_FI;
          %>
          <tr>
            <td>
              <input type="hidden" name="userId" value="<%= etudiant.getId() %>">
              <div class="student-info">
                <div class="avatar">
                  <% if (etudiant.getPicturePath() != null && !etudiant.getPicturePath().isEmpty()) { %>
                    <img src="${pageContext.request.contextPath}/images/users/<%= etudiant.getPicturePath() %>" alt="">
                  <% } else { %>
                    <img src="${pageContext.request.contextPath}/images/users/default.jpg" alt="">
                  <% } %>
                </div>
                <div>
                  <div style="font-weight:600"><%= etudiant.getFirstName() %> <%= etudiant.getLastName() %></div>
                  <div style="font-size:.78rem; color:var(--txt-muted)"><%= etudiant.getEmail() %></div>
                </div>
              </div>
            </td>
            <td>
              <span class="pill <%= isFI ? "pill-fi" : "pill-fa" %>">
                <%= isFI ? "FI" : "FA" %>
              </span>
            </td>
            <td>
              <select name="classId">
                <option value="0" <%= etudiant.getStudentClass() == null ? "selected" : "" %>>— Sans classe —</option>
                <% if (allClasses != null) { for (StudentClass sc : allClasses) {
                    boolean selected = etudiant.getStudentClass() != null
                                    && etudiant.getStudentClass().getId().equals(sc.getId());
                %>
                  <option value="<%= sc.getId() %>" <%= selected ? "selected" : "" %>><%= sc.getName() %></option>
                <% } } %>
              </select>
            </td>
          </tr>
          <% } %>
        </tbody>
      </table>
      <% } %>
      <div class="form-footer">
        <a href="${pageContext.request.contextPath}/scolarite/classes" class="btn btn-secondary">Annuler</a>
        <button type="submit" class="btn btn-primary">Enregistrer les affectations</button>
      </div>
    </form>
  </div>
</main>
</body>
</html>