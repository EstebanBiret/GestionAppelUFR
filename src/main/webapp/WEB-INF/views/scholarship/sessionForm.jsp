<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="ut1.appel.entity.*, java.util.*, java.time.LocalDate" %>
<%
    Users u = (Users) session.getAttribute("currentUser");
    if (u == null) { response.sendRedirect(request.getContextPath() + "/auth/login"); return; }
    Course course        = (Course)           request.getAttribute("course");
    List<Users> ens      = (List<Users>)       request.getAttribute("enseignants");
    List<StudentGroup> groupes = (List<StudentGroup>) request.getAttribute("groupes");
    String error         = (String)            request.getAttribute("error");
    String today         = LocalDate.now().toString();
    Long respId          = (course != null && course.getResponsable() != null)
                               ? course.getResponsable().getId() : null;
%>
<!DOCTYPE html>
<html lang="fr">
<head>
  <meta charset="UTF-8">
  <title>Nouvelle séance — UT Capitole</title>
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
    <h1>Nouvelle séance</h1>
    <a href="${pageContext.request.contextPath}/scolarite/cours">← Retour aux cours</a>
  </div>

  <% if (error != null) { %>
    <div class="error-msg"><%= error %></div>
  <% } %>

  <div class="card">
    <div class="card-header">Créer une séance</div>
    <div class="card-body">
      <form method="post" action="${pageContext.request.contextPath}/scolarite/seances/save"
            id="seanceForm">
        <input type="hidden" name="courseId" value="<%= course != null ? course.getId() : "" %>">

        <div class="form-row" style="margin-bottom:1.25rem">
          <div class="form-group">
            <label>Cours</label>
            <div class="form-static">
              <%= course != null ? course.getName() : "—" %>
              <% if (course != null && course.getStudentClass() != null) { %>
                — <span class="badge"><%= course.getStudentClass().getName() %></span>
              <% } %>
            </div>
          </div>
          <div class="form-group">
            <label>Responsable du cours</label>
            <div class="form-static">
              <% if (course != null && course.getResponsable() != null) { %>
                <%= course.getResponsable().getFirstName() %> <%= course.getResponsable().getLastName() %>
              <% } else { %>—<% } %>
            </div>
          </div>
        </div>

        <div class="form-group" style="margin-bottom:1.25rem">
          <label for="teacherId">Enseignant de la séance</label>
          <select id="teacherId" name="teacherId" required>
            <option value="">— Sélectionner —</option>
            <% if (ens != null) { for (Users e : ens) { %>
              <option value="<%= e.getId() %>"
                <%= (respId != null && respId.equals(e.getId())) ? "selected" : "" %>>
                <%= e.getFirstName() %> <%= e.getLastName() %>
              </option>
            <% } } %>
          </select>
        </div>

        <div class="form-group" style="margin-bottom:1.25rem">
          <label for="sessionDate">Date de la séance</label>
          <input type="date" id="sessionDate" name="sessionDate" min="<%= today %>" required>
        </div>

        <div class="form-row" style="margin-bottom:1.25rem">
          <div class="form-group">
            <label for="startTime">Heure de début</label>
            <select id="startTime" name="startTime" required>
              <option value="">— Choisir —</option>
              <%

                for (int h = 8; h <= 17; h++) {
                  for (int m = 0; m < 60; m += 30) {
                    String val = String.format("%02d:%02d", h, m);
              %>
                <option value="<%= val %>"><%= val %></option>
              <%  }
                }
              %>
            </select>
          </div>
          <div class="form-group">
            <label for="endTime">Heure de fin</label>
            <select id="endTime" name="endTime" required>
              <option value="">— Choisir —</option>
              <%
                for (int h = 8; h <= 18; h++) {
                  for (int m = 0; m < 60; m += 30) {
                    if (h == 8  && m == 0)  continue;
                    if (h == 18 && m > 0)   continue;
                    String val = String.format("%02d:%02d", h, m);
              %>
                <option value="<%= val %>"><%= val %></option>
              <%  }
                }
              %>
            </select>
          </div>
        </div>

        <div class="form-group" style="margin-bottom:1.5rem">
          <label for="groupId">Groupe</label>
          <select id="groupId" name="groupId">
            <option value="">Classe entière</option>
            <% if (groupes != null) { for (StudentGroup g : groupes) { %>
              <option value="<%= g.getId() %>"><%= g.getName() %></option>
            <% } } %>
          </select>
        </div>

        <div class="form-footer">
          <a href="${pageContext.request.contextPath}/scolarite/cours"
             class="btn btn-secondary"
             onclick="return confirmCancel(event)">Annuler</a>
          <button type="submit" class="btn btn-primary">Créer la séance</button>
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