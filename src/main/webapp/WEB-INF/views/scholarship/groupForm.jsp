<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="ut1.appel.entity.*, ut1.appel.enums.Role, java.util.*" %>
<%
    Users u = (Users) session.getAttribute("currentUser");
    if (u == null) { response.sendRedirect(request.getContextPath() + "/auth/login"); return; }
    StudentGroup editGroupe   = (StudentGroup) request.getAttribute("groupe");
    List<StudentClass> classes = (List<StudentClass>) request.getAttribute("classes");
    List<Users> students       = (List<Users>) request.getAttribute("students");
    boolean isEdit = editGroupe != null;
    String error   = (String) request.getAttribute("error");

    Set<Long> memberIds = new HashSet<>();
    if (isEdit && editGroupe.getUsers() != null) {
        for (Users m : editGroupe.getUsers()) memberIds.add(m.getId());
    }
%>
<!DOCTYPE html>
<html lang="fr">
<head>
  <meta charset="UTF-8">
  <title><%= isEdit ? "Modifier " + editGroupe.getName() : "Nouveau groupe" %> — UT Capitole</title>
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
    <h1><%= isEdit ? "Modifier le groupe" : "Nouveau groupe de TD" %></h1>
    <a href="${pageContext.request.contextPath}/scolarite/groupes">← Retour aux groupes</a>
  </div>

  <% if (error != null) { %>
    <div class="error-msg"><%= error %></div>
  <% } %>

  <div class="card">
    <div class="card-header">Informations du groupe</div>
    <div class="card-body">
      <form method="post"
            action="${pageContext.request.contextPath}/scolarite/groupes/save"
            id="mainForm">
        <% if (isEdit) { %>
          <input type="hidden" name="id" value="<%= editGroupe.getId() %>">
        <% } %>

        <div class="form-row" style="margin-bottom:1rem">
          <div class="form-group">
            <label for="name">Nom du groupe</label>
            <input type="text" id="name" name="name"
                   value="<%= isEdit ? editGroupe.getName() : "" %>"
                   placeholder="Ex : TD1" required>
          </div>
          <div class="form-group">
            <label for="classId">Classe rattachée</label>
            <select id="classId" name="classId" required onchange="onClassChange(this.value)">
              <option value="">— Sélectionner une classe —</option>
              <% if (classes != null) { for (StudentClass sc : classes) {
                  boolean sel = isEdit && editGroupe.getStudentClass() != null
                              && editGroupe.getStudentClass().getId().equals(sc.getId());
              %>
                <option value="<%= sc.getId() %>" <%= sel ? "selected" : "" %>><%= sc.getName() %></option>
              <% } } %>
            </select>
          </div>
        </div>

        <div class="card" id="studentsCard" style="margin-bottom:0">
          <div class="card-header" style="display:flex; align-items:center; justify-content:space-between; flex-wrap:wrap; gap:.75rem">
            <span>
              Étudiants
              <span style="font-weight:400; font-size:.8rem; color:var(--txt-muted); margin-left:.5rem">— cochez les membres du groupe</span>
            </span>
            <div style="position:relative">
              <input type="text" class="search-bar" placeholder="Rechercher un étudiant…"
                     oninput="filterCheckTable('studentsTbody', this.value)"
                     style="width:240px">
            </div>
          </div>
          <table class="check-table" id="groupStudentsTable">
            <thead>
              <tr>
                <th style="cursor:default"></th>
                <th onclick="sortCheckTable('groupStudentsTable', 1, this)" data-dir="asc">
                  Étudiant <span class="sort-icon">↕</span>
                </th>
                <th onclick="sortCheckTable('groupStudentsTable', 2, this)" data-dir="asc">
                  Formation <span class="sort-icon">↕</span>
                </th>
                <th onclick="sortCheckTable('groupStudentsTable', 3, this)" data-dir="asc">
                  Groupe actuel <span class="sort-icon">↕</span>
                </th>
              </tr>
            </thead>
            <tbody id="studentsTbody">
              <tr id="selectHint">
                <td colspan="4" class="select-class-hint">
                  Sélectionnez d'abord une classe pour voir ses étudiants.
                </td>
              </tr>
            </tbody>
          </table>
        </div>

        <div class="form-footer">
<a href="${pageContext.request.contextPath}/scolarite/groupes"
   class="btn btn-secondary"
   onclick="return confirmCancel(event)">Annuler</a>
   <button type="submit" class="btn btn-primary">
            <%= isEdit ? "Enregistrer les modifications" : "Créer le groupe" %>
          </button>
        </div>
      </form>
    </div>
  </div>
</main>

<script>
  window._contextPath = '<%= request.getContextPath() %>';
  const currentGroupId = <%= isEdit ? editGroupe.getId() : "null" %>;
  const preCheckedIds  = [<% int i=0; for(Long mid : memberIds){ if(i++>0) out.print(","); out.print("\""+mid+"\""); } %>];

  function onClassChange(classId) {
    loadStudents(classId, currentGroupId, preCheckedIds);
  }

  <% if (isEdit && editGroupe.getStudentClass() != null) { %>
    document.addEventListener('DOMContentLoaded', () => {
      loadStudents('<%= editGroupe.getStudentClass().getId() %>', currentGroupId, preCheckedIds);
    });
  <% } %>
</script>
<script src="${pageContext.request.contextPath}/js/groupForm.js"></script>
</body>
</html>