<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="ut1.appel.entity.*, java.time.format.*" %>
<%
    Users u = (Users) session.getAttribute("currentUser");
    if (u == null) { response.sendRedirect(request.getContextPath() + "/auth/login"); return; }
    Justification j = (Justification) request.getAttribute("justif");
    String error    = (String) request.getAttribute("error");
    DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    DateTimeFormatter htmlFmt = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
%>
<!DOCTYPE html>
<html lang="fr">
<head>
  <meta charset="UTF-8">
  <title>Traiter un justificatif — UT Capitole</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/justifications.css">
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
    <h1>Traiter le justificatif</h1>
    <a href="${pageContext.request.contextPath}/scolarite/justificatifs">← Retour aux justificatifs</a>
  </div>

  <% if (error != null) { %>
    <div class="error-msg">⚠ <%= error %></div>
  <% } %>

  <div class="justif-review-layout">

    <div class="card">
      <div class="card-header">Document déposé</div>
      <div class="card-body">
        <div class="review-student-info">
          <div class="justif-avatar justif-avatar--lg">
            <% if (j.getUser().getPicturePath() != null) { %>
              <img src="${pageContext.request.contextPath}/<%= j.getUser().getPicturePath() %>"
                   alt="<%= j.getUser().getFirstName() %>">
            <% } else { %>
              <%= j.getUser().getFirstName().charAt(0) %><%= j.getUser().getLastName().charAt(0) %>
            <% } %>
          </div>
          <div>
            <div class="review-student-name"><%= j.getUser().getFirstName() %> <%= j.getUser().getLastName() %></div>
            <div class="review-student-email"><%= j.getUser().getEmail() %></div>
            <div class="justif-date">Déposé le <%= j.getDepositDate() != null ? j.getDepositDate().format(fmt) : "—" %></div>
          </div>
        </div>

        <% if (j.getComment() != null && !j.getComment().trim().isEmpty()) { %>
          <div class="review-comment-block">
            <div class="review-label">Commentaire de l'étudiant</div>
            <div class="review-comment">"<%= j.getComment() %>"</div>
          </div>
        <% } %>

        <div style="margin-top: 1.25rem;">
          <div class="review-label">Justificatif joint</div>
          <a href="${pageContext.request.contextPath}/<%= j.getFileUrl() %>"
             target="_blank" class="btn-download">
            📄 Télécharger le document
          </a>
        </div>
      </div>
    </div>

    <div class="card">
      <div class="card-header">Décision</div>
      <div class="card-body">
        <form method="post" action="${pageContext.request.contextPath}/scolarite/justificatifs/save">
          <input type="hidden" name="id" value="<%= j.getId() %>">

          <div class="form-row" id="datesBlock" style="margin-bottom: 1.25rem; display: none; min-width: 0;">
            <div class="form-group" style="min-width: 0; overflow: hidden;">
              <label for="startDate">Date et heure de début</label>
              <input type="datetime-local" id="startDate" name="startDate">
            </div>
            <div class="form-group" style="min-width: 0; overflow: hidden;">
              <label for="endDate">Date et heure de fin</label>
              <input type="datetime-local" id="endDate" name="endDate">
            </div>
          </div>

          <div class="form-group" style="margin-bottom: 1.25rem;">
            <label for="feedback">Retour à l'étudiant (facultatif)</label>
            <textarea id="feedback" name="feedback" rows="3"
                      placeholder="Motif du refus, remarque..."><%= j.getScholarshipFeedback() != null ? j.getScholarshipFeedback() : "" %></textarea>
          </div>

         <div class="justif-form-footer">
           <a href="${pageContext.request.contextPath}/scolarite/justificatifs"
              class="btn btn-secondary">Annuler</a>
           <button type="submit" name="decision" value="REJETEE"
                   class="btn btn-reject" onclick="hideDates()">✗ Refuser</button>
           <button type="submit" name="decision" value="APPROUVEE"
                   class="btn btn-approve" onclick="showDates()">✓ Valider</button>
         </div>
      </div>
    </div>

  </div>
</main>
<script>
  window._contextPath = '<%= request.getContextPath() %>';
</script>
<script src="${pageContext.request.contextPath}/js/justifications.js"></script>
</body>
</html>