<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="ut1.appel.entity.*, ut1.appel.enums.*, java.util.*, java.time.format.*" %>
<%
    Users u = (Users) session.getAttribute("currentUser");
    if (u == null) { response.sendRedirect(request.getContextPath() + "/auth/login"); return; }
    List<Justification> justifs = (List<Justification>) request.getAttribute("justificatifs");
    String error   = (String) request.getAttribute("error");
    String success = (String) request.getAttribute("success");
    DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    List<Justification> pending = new ArrayList<>();
    List<Justification> treated = new ArrayList<>();
    if (justifs != null) {
        for (Justification j : justifs) {
            if (j.getStatus() == JustificationStatus.EN_ATTENTE) pending.add(j);
            else treated.add(j);
        }
    }
%>
<!DOCTYPE html>
<html lang="fr">
<head>
  <meta charset="UTF-8">
  <title>Justificatifs — UT Capitole</title>
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
    <h1>Justificatifs d'absence</h1>
    <a href="${pageContext.request.contextPath}/scolarite">← Tableau de bord</a>
  </div>

  <% if (error != null) { %>
    <div class="error-msg">⚠ <%= error %></div>
  <% } %>
  <% if (success != null) { %>
    <div class="alert-success">✓ <%= success %></div>
  <% } %>

  <div class="justif-section-title">
    En attente de traitement
    <span class="justif-count"><%= pending.size() %></span>
  </div>

  <% if (pending.isEmpty()) { %>
    <div class="justif-empty">Aucun justificatif en attente.</div>
  <% } else { %>
    <div class="justif-grid">
      <% for (Justification j : pending) { %>
        <a href="${pageContext.request.contextPath}/scolarite/justificatifs/form?id=<%= j.getId() %>"
           class="justif-card justif-card--pending">
          <div class="justif-card-top">
           <div class="justif-avatar">
             <% if (j.getUser().getPicturePath() != null) { %>
               <img src="${pageContext.request.contextPath}/<%= j.getUser().getPicturePath() %>"
                    alt="<%= j.getUser().getFirstName() %>">
             <% } else { %>
               <%= j.getUser().getFirstName().charAt(0) %><%= j.getUser().getLastName().charAt(0) %>
             <% } %>
           </div>
            <div>
              <div class="justif-student"><%= j.getUser().getFirstName() %> <%= j.getUser().getLastName() %></div>
              <div class="justif-date">Déposé le <%= j.getDepositDate() != null ? j.getDepositDate().format(fmt) : "—" %></div>
            </div>
            <span class="justif-badge justif-badge--pending">En attente</span>
          </div>
          <% if (j.getComment() != null && !j.getComment().trim().isEmpty()) { %>
            <div class="justif-comment">"<%= j.getComment() %>"</div>
          <% } %>
          <div class="justif-action-hint">Cliquer pour traiter →</div>
        </a>
      <% } %>
    </div>
  <% } %>

  <div class="justif-section-title" style="margin-top: 2rem;">
    Justificatifs traités
    <span class="justif-count"><%= treated.size() %></span>
  </div>

  <% if (treated.isEmpty()) { %>
    <div class="justif-empty">Aucun justificatif traité.</div>
  <% } else { %>
    <div class="justif-grid">
      <% for (Justification j : treated) {
           boolean approved = j.getStatus() == JustificationStatus.APPROUVEE;
      %>
        <div class="justif-card <%= approved ? "justif-card--approved" : "justif-card--rejected" %>">
          <div class="justif-card-top">
            <div class="justif-avatar">
              <% if (j.getUser().getPicturePath() != null) { %>
                <img src="${pageContext.request.contextPath}/<%= j.getUser().getPicturePath() %>"
                     alt="<%= j.getUser().getFirstName() %>">
              <% } else { %>
                <%= j.getUser().getFirstName().charAt(0) %><%= j.getUser().getLastName().charAt(0) %>
              <% } %>
            </div>
            <div class="justif-student"><%= j.getUser().getFirstName() %> <%= j.getUser().getLastName() %></div>
            <div class="justif-date">Déposé le <%= j.getDepositDate() != null ? j.getDepositDate().format(fmt) : "—" %></div>
            <% if (j.getProcessedDate() != null) { %>
              <div class="justif-date">Traité le <%= j.getProcessedDate().format(fmt) %></div>
            <% } %>
            <span class="justif-badge <%= approved ? "justif-badge--approved" : "justif-badge--rejected" %>">
              <%= approved ? "Validé" : "Refusé" %>
            </span>
          </div>
          <div class="justif-period">
            📅 Du <strong><%= j.getStartDate() != null ? j.getStartDate().format(fmt) : "—" %></strong>
            au <strong><%= j.getEndDate() != null ? j.getEndDate().format(fmt) : "—" %></strong>
          </div>
          <% if (j.getScholarshipFeedback() != null && !j.getScholarshipFeedback().trim().isEmpty()) { %>
            <div class="justif-feedback">"<%= j.getScholarshipFeedback() %>"</div>
          <% } %>
        </div>
      <% } %>
    </div>
  <% } %>
</main>
</body>
</html>