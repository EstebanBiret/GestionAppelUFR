<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="ut1.appel.entity.Users" %>
<%@ page import="ut1.appel.entity.Justification" %>
<%@ page import="ut1.appel.enums.JustificationStatus" %>
<%@ page import="java.util.List, java.util.ArrayList" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%
    Users u = (Users) session.getAttribute("currentUser");
    if (u == null) { response.sendRedirect(request.getContextPath() + "/auth/login"); return; }
    List<Justification> justifications = (List<Justification>) request.getAttribute("justifications");
    DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy à HH:mm");

    List<Justification> pending = new ArrayList<>();
    List<Justification> treated = new ArrayList<>();
    if (justifications != null) {
        for (Justification j : justifications) {
            if (j.getStatus() == JustificationStatus.EN_ATTENTE) pending.add(j);
            else treated.add(j);
        }
    }
%>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Suivi des justificatifs - UT Capitole</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/student.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/justifications.css">
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
        <div class="user-dropdown">
            <div class="user-chip" onclick="toggleDropdown()">
                <%= u.getFirstName() %> <%= u.getLastName() %> ▾
            </div>
            <div class="dropdown-menu" id="dropdownMenu">
                <a href="${pageContext.request.contextPath}/profil/voir">Mon profil</a>
            </div>
        </div>
        <a href="${pageContext.request.contextPath}/auth/logout" class="btn-logout">Se déconnecter</a>
    </div>
</header>

<main>
    <div class="page-header">
        <h1>Suivi de mes justificatifs</h1>
        <a href="${pageContext.request.contextPath}/etudiant">← Tableau de bord</a>
    </div>

    <% if (request.getAttribute("success") != null) { %>
        <div class="alert-success">✓ <%= request.getAttribute("success") %></div>
    <% } %>

    <div class="page-header" style="margin-top: 0; margin-bottom: 1rem;">
        <div></div>
    </div>

    <div class="justif-section-title">
        En attente de traitement
        <span class="justif-count"><%= pending.size() %></span>
    </div>

    <% if (pending.isEmpty()) { %>
        <div class="justif-empty">Aucun justificatif en attente.</div>
    <% } else { %>
        <div class="justif-grid">
            <% for (Justification j : pending) { %>
                <div class="justif-card justif-card--pending">
                    <div class="justif-card-top">
                        <div class="justif-avatar">
                            <% if (u.getPicturePath() != null) { %>
                                <img src="${pageContext.request.contextPath}/<%= u.getPicturePath() %>" alt="">
                            <% } else { %>
                                <%= u.getFirstName().charAt(0) %><%= u.getLastName().charAt(0) %>
                            <% } %>
                        </div>
                        <div>
                            <div class="justif-student">Déposé le <%= j.getDepositDate() != null ? j.getDepositDate().format(fmt) : "—" %></div>
                        </div>
                        <span class="justif-badge justif-badge--pending">En attente</span>
                    </div>
                    <% if (j.getComment() != null && !j.getComment().trim().isEmpty()) { %>
                        <div class="justif-comment">"<%= j.getComment() %>"</div>
                    <% } %>
                    <div class="justif-period">📋 En attente de traitement par la scolarité</div>
                    <a href="${pageContext.request.contextPath}/<%= j.getFileUrl() %>"
                       target="_blank" class="btn-download" style="align-self: flex-start;">
                        📄 Voir le document
                    </a>
                </div>
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
                            <% if (u.getPicturePath() != null) { %>
                                <img src="${pageContext.request.contextPath}/<%= u.getPicturePath() %>" alt="">
                            <% } else { %>
                                <%= u.getFirstName().charAt(0) %><%= u.getLastName().charAt(0) %>
                            <% } %>
                        </div>
                        <div class="justif-student">Déposé le <%= j.getDepositDate() != null ? j.getDepositDate().format(fmt) : "—" %></div>
                        <% if (j.getProcessedDate() != null) { %>
                          <div class="justif-date"><%= j.getStatus() == JustificationStatus.APPROUVEE ? "Validé" : "Refusé" %> le <%= j.getProcessedDate().format(fmt) %></div>
                        <% } %>
                        <span class="justif-badge <%= approved ? "justif-badge--approved" : "justif-badge--rejected" %>">
                            <%= approved ? "Validé" : "Refusé" %>
                        </span>
                    </div>

                    <% if (j.getComment() != null && !j.getComment().trim().isEmpty()) { %>
                        <div class="justif-comment">"<%= j.getComment() %>"</div>
                    <% } %>

                    <div class="justif-period">
                        📅 Du <strong><%= j.getStartDate() != null ? j.getStartDate().format(fmt) : "—" %></strong>
                        au <strong><%= j.getEndDate() != null ? j.getEndDate().format(fmt) : "—" %></strong>
                    </div>

                    <% if (j.getScholarshipFeedback() != null && !j.getScholarshipFeedback().trim().isEmpty()) { %>
                        <div class="justif-feedback">💬 "<%= j.getScholarshipFeedback() %>"</div>
                    <% } %>
                    <a href="${pageContext.request.contextPath}/<%= j.getFileUrl() %>"
                       target="_blank" class="btn-download" style="align-self: flex-start;">
                        📄 Voir le document
                    </a>
                </div>
            <% } %>
        </div>
    <% } %>
</main>

<script>window._contextPath = '<%= request.getContextPath() %>';</script>
<script src="${pageContext.request.contextPath}/js/student.js"></script>
</body>
</html>