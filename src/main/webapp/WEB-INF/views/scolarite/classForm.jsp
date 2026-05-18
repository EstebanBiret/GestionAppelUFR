<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="ut1.appel.entity.*, ut1.appel.enums.Role, java.util.*" %>
<%
    Users u = (Users) session.getAttribute("currentUser");
    if (u == null) { response.sendRedirect(request.getContextPath() + "/auth/login"); return; }
    StudentClass editClass        = (StudentClass) request.getAttribute("studentClass");
    List<StudentClass> allClasses = (List<StudentClass>) request.getAttribute("classes");
    List<Users> students          = (List<Users>) request.getAttribute("students");
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

  <% if (isEdit) { %>
  <div class="card">
    <div class="card-header" style="display:flex; align-items:center; gap:.75rem">
      <input type="checkbox" id="checkAll" style="accent-color:var(--rouge); width:16px; height:16px">
      <span>Étudiants</span>
      <span style="font-weight:400; font-size:.8rem; color:var(--txt-muted)">
        — les étudiants cochés seront assignés à cette classe
      </span>
    </div>

    <form method="post" action="${pageContext.request.contextPath}/scolarite/classes/save-students">
      <input type="hidden" name="classId" value="<%= editClass.getId() %>">

      <% if (students == null || students.isEmpty()) { %>
        <p class="empty">Aucun étudiant inscrit dans l'application.</p>
      <% } else { %>
      <table class="check-table">
        <thead>
          <tr>
            <th><label for="checkAll" style="cursor:pointer">✓</label></th>
            <th>Nom</th>
            <th>Prénom</th>
            <th>Formation</th>
            <th>Classe actuelle</th>
          </tr>
        </thead>
        <tbody id="studentsTbody">
          <% for (Users etudiant : students) {
              boolean isFI = etudiant.getRole() == Role.ETUDIANT_FI;
              boolean inThisClass = etudiant.getStudentClass() != null
                                 && etudiant.getStudentClass().getId().equals(editClass.getId());
              String classeLabel = etudiant.getStudentClass() != null
                                 ? etudiant.getStudentClass().getName()
                                 : null;
          %>
          <tr class="<%= inThisClass ? "checked-row" : "" %>" onclick="toggleRow(this)">
            <td>
              <input type="checkbox" name="checkedStudents" value="<%= etudiant.getId() %>"
                     <%= inThisClass ? "checked" : "" %>
                     onclick="event.stopPropagation()">
            </td>
            <td style="font-weight:600"><%= etudiant.getLastName() %></td>
            <td><%= etudiant.getFirstName() %></td>
            <td>
              <span class="pill <%= isFI ? "pill-fi" : "pill-fa" %>">
                <%= isFI ? "FI" : "FA" %>
              </span>
            </td>
            <td>
              <% if (classeLabel != null) { %>
                <span class="pill" style="background:var(--gris);color:var(--txt-muted)">
                  <%= classeLabel %>
                </span>
              <% } else { %>
                <span style="color:var(--txt-muted);font-size:.78rem">Aucune classe</span>
              <% } %>
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
  <% } else { %>
  <div class="card">
    <p class="empty" style="padding:1.5rem">
      Créez d'abord la classe, vous pourrez ensuite y affecter des étudiants.
    </p>
  </div>
  <% } %>
</main>

<script>
  function toggleRow(tr) {
    const cb = tr.querySelector('input[type=checkbox]');
    cb.checked = !cb.checked;
    tr.classList.toggle('checked-row', cb.checked);
  }

  document.getElementById('checkAll')?.addEventListener('change', function () {
    document.querySelectorAll('#studentsTbody input[type=checkbox]').forEach(cb => {
      cb.checked = this.checked;
      cb.closest('tr').classList.toggle('checked-row', this.checked);
    });
  });
</script>
</body>
</html>