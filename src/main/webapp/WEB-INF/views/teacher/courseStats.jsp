<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="ut1.appel.entity.*, java.util.*" %>
<%
    Users u = (Users) session.getAttribute("currentUser");
    if (u == null) { response.sendRedirect(request.getContextPath() + "/auth/login"); return; }
    ut1.appel.entity.Course course = (ut1.appel.entity.Course) request.getAttribute("course");
    List<Map<String, Object>> absencePerSession = (List<Map<String, Object>>) request.getAttribute("absencePerSession");
    List<Map<String, Object>> studentsAtRisk    = (List<Map<String, Object>>) request.getAttribute("studentsAtRisk");
    Double averageAbsences = (Double) request.getAttribute("averageAbsences");
%>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Bilan — <%= course != null ? course.getName() : "Cours" %></title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/teacher.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/classes.css">
</head>
<body>

<header>
    <div class="logo">
        <div class="logo-bar"></div>
        <div>
            <div class="logo-text">Université Toulouse Capitole</div>
            <div class="logo-sub">Espace Enseignant</div>
        </div>
    </div>
    <div class="header-right">
        <span class="user-chip"><%= u.getFirstName() %> <%= u.getLastName() %></span>
        <a href="${pageContext.request.contextPath}/auth/logout" class="btn-logout">Se déconnecter</a>
    </div>
</header>

<main>
    <div class="page-header">
        <h1>Bilan — <%= course != null ? course.getName() : "—" %></h1>
        <a href="${pageContext.request.contextPath}/enseignant?action=past">← Retour aux cours passés</a>
    </div>

    <div style="display: grid; grid-template-columns: 1fr 2fr; gap: 1.25rem; align-items: start; margin-bottom: 1.25rem;">

        <div class="card">
            <div class="card-header">Moyenne d'absences par séance</div>
            <div class="card-body" style="text-align: center; padding: 2rem 1.25rem;">
                <div style="font-size: 3rem; font-weight: 700; color: var(--rouge); font-family: 'Source Serif 4', serif; line-height: 1;">
                    <%= averageAbsences != null ? String.format("%.1f", averageAbsences) : "—" %>
                </div>
                <div style="font-size: .8rem; color: var(--txt-muted); margin-top: .5rem;">absences en moyenne</div>
            </div>
        </div>

        <div class="card">
            <div class="card-header">Taux d'absence par séance</div>
            <div class="card-body">
                <% if (absencePerSession == null || absencePerSession.isEmpty()) { %>
                    <div class="empty">Aucune donnée disponible.</div>
                <% } else { %>
                    <canvas id="absenceChart" height="80"></canvas>
                <% } %>
            </div>
        </div>

    </div>

    <div class="card">
        <div class="card-header">Étudiants avec 3 absences injustifiées ou plus</div>
        <% if (studentsAtRisk == null || studentsAtRisk.isEmpty()) { %>
            <div class="empty">Aucun étudiant concerné.</div>
        <% } else { %>
            <table class="classes-table">
                <thead>
                    <tr>
                        <th>Prénom</th>
                        <th>Nom</th>
                        <th>Absences injustifiées</th>
                    </tr>
                </thead>
                <tbody>
                    <% for (Map<String, Object> s : studentsAtRisk) { %>
                    <tr>
                        <td><%= s.get("firstName") %></td>
                        <td><strong><%= s.get("lastName") %></strong></td>
                        <td>
                            <span style="background: var(--rouge-lt); color: var(--rouge-dk);
                                         padding: .2rem .6rem; border-radius: 20px;
                                         font-size: .8rem; font-weight: 700;">
                                <%= s.get("absences") %>
                            </span>
                        </td>
                    </tr>
                    <% } %>
                </tbody>
            </table>
        <% } %>
    </div>

</main>

<% if (absencePerSession != null && !absencePerSession.isEmpty()) { %>
<script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
<script src="${pageContext.request.contextPath}/js/courseStats.js"></script>
<script>
    renderAbsenceChart(
        [<% for (int i = 0; i < absencePerSession.size(); i++) {
            if (i > 0) out.print(",");
            out.print("\"" + absencePerSession.get(i).get("date") + "\"");
        } %>],
        [<% for (int i = 0; i < absencePerSession.size(); i++) {
            if (i > 0) out.print(",");
            long absent = (long) absencePerSession.get(i).get("absent");
            long total  = (long) absencePerSession.get(i).get("total");
            out.print(total > 0 ? (absent * 100.0 / total) : 0);
        } %>]
    );
</script>
<% } %>

</body>
</html>