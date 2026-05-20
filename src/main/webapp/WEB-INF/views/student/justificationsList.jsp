<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="ut1.appel.entity.Users" %>
<%@ page import="ut1.appel.entity.Justification" %>
<%@ page import="java.util.List" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%
    Users u = (Users) session.getAttribute("currentUser");
    if (u == null) {
        response.sendRedirect(request.getContextPath() + "/auth/login");
        return;
    }
    List<Justification> justifications = (List<Justification>) request.getAttribute("justifications");

    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy à HH:mm");
%>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Suivi des justificatifs - UT Capitole</title>
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
    <div class="page-header">
        <h1>Suivi de mes justificatifs</h1>
        <a href="${pageContext.request.contextPath}/etudiant">← Tableau de bord</a>
    </div>

    <% if (request.getAttribute("success") != null) { %>
        <div class="alert-success">✓ <%= request.getAttribute("success") %></div>
    <% } %>

    <div class="card">
        <div class="card-body card-body-flush">
            <% if (justifications == null || justifications.isEmpty()) { %>
            <div class="empty-state">
                <div class="empty-state-icon">📂</div>
                Vous n'avez transmis aucun justificatif pour le moment.
            </div>
            <% } else { %>
            <table class="table-suivi">
                <thead>
                <tr>
                    <th>Date d'envoi</th>
                    <th>Commentaire</th>
                    <th>Période couverte</th>
                    <th class="cell-status">Statut</th>
                </tr>
                </thead>
                <tbody>
                <% for (Justification j : justifications) { %>
                <tr>
                    <td class="cell-date">
                        <%= j.getDepositDate() != null ? j.getDepositDate().format(dateFormatter) : "" %>
                    </td>

                    <td class="cell-comment">
                        <%= j.getComment() != null && !j.getComment().trim().isEmpty() ? j.getComment() : "<em>Aucun commentaire</em>" %>
                    </td>

                    <td class="cell-period">
                        <% if (j.getStartDate() != null && j.getEndDate() != null) { %>
                        Du <strong><%= j.getStartDate().format(dateFormatter) %></strong> <br>
                        au <strong><%= j.getEndDate().format(dateFormatter) %></strong>
                        <% } else { %>
                        <span class="text-italic-muted">En attente de traitement</span>
                        <% } %>
                    </td>

                    <td class="cell-status">
                        <% if ("EN_ATTENTE".equals(j.getStatus().toString())) { %>
                        <span class="badge badge-pending">En attente</span>
                        <% } else if ("APPROUVEE".equals(j.getStatus().toString())) { %>
                        <span class="badge badge-accepted">Validé</span>
                        <% } else { %>
                        <span class="badge badge-rejected">Refusé</span>
                        <% } %>
                    </td>
                </tr>
                <% } %>
                </tbody>
            </table>
            <% } %>
        </div>
    </div>
</main>

</body>
</html>