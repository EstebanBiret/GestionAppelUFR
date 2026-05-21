<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="ut1.appel.entity.*, java.util.*" %>
<%
    Users u = (Users) session.getAttribute("currentUser");
    if (u == null) { response.sendRedirect(request.getContextPath() + "/auth/login"); return; }
    ut1.appel.entity.Course course = (ut1.appel.entity.Course) request.getAttribute("course");
    List<Map<String, Object>> absencePerSession = (List<Map<String, Object>>) request.getAttribute("absencePerSession");
    List<Map<String, Object>> studentsAtRisk = (List<Map<String, Object>>) request.getAttribute("studentsAtRisk");
    Double averageAbsences = (Double) request.getAttribute("averageAbsences");
%>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Bilan du cours</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/teacher.css">
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
</head>
<body>
<%
    System.out.println("DEBUG JSP average: " + averageAbsences);
    System.out.println("DEBUG JSP perSession: " + (absencePerSession == null ? "NULL" : absencePerSession.size()));
%>
<div class="container">

    <h1>Bilan — <%= course.getName() %></h1>

    <%-- Average absences per session --%>
    <div class="stat-card">
        <h2>Moyenne d'absences par séance</h2>
        <p class="stat-number"><%= String.format("%.1f", averageAbsences) %></p>
    </div>

    <%-- Absence rate over time --%>
    <div class="stat-card">
        <h2>Taux d'absence par séance</h2>
        <canvas id="absenceChart" height="100"></canvas>
    </div>

    <%-- Students with 3+ unjustified absences --%>
    <div class="stat-card">
        <h2>Étudiants avec 3 absences injustifiées ou plus</h2>
        <% if (studentsAtRisk == null || studentsAtRisk.isEmpty()) { %>
        <p>Aucun étudiant concerné.</p>
        <% } else { %>
        <table class="stats-table">
            <thead>
            <tr>
                <th>Nom</th>
                <th>Prénom</th>
                <th>Absences injustifiées</th>
            </tr>
            </thead>
            <tbody>
            <% for (Map<String, Object> s : studentsAtRisk) { %>
            <tr>
                <td><%= s.get("lastName") %></td>
                <td><%= s.get("firstName") %></td>
                <td><%= s.get("absences") %></td>
            </tr>
            <% } %>
            </tbody>
        </table>
        <% } %>
    </div>

    <a href="${pageContext.request.contextPath}/enseignant/" class="btn-back">← Retour</a>

</div>

<script>
    // Build chart data from server-side attributes
    const labels = [
        <% for (int i = 0; i < absencePerSession.size(); i++) {
            if (i > 0) out.print(",");
            out.print("\"" + absencePerSession.get(i).get("date") + "\"");
        } %>
    ];
    const rates = [
        <% for (int i = 0; i < absencePerSession.size(); i++) {
            if (i > 0) out.print(",");
            long absent = (long) absencePerSession.get(i).get("absent");
            long total  = (long) absencePerSession.get(i).get("total");
            out.print(total > 0 ? (absent * 100.0 / total) : 0);
        } %>
    ];

    new Chart(document.getElementById("absenceChart"), {
        type: "line",
        data: {
            labels: labels,
            datasets: [{
                label: "Taux d'absence (%)",
                data: rates,
                borderColor: "#e74c3c",
                backgroundColor: "rgba(231,76,60,0.1)",
                tension: 0.3,
                fill: true
            }]
        },
        options: {
            scales: {
                y: { min: 0, max: 100, ticks: { callback: v => v + "%" } }
            }
        }
    });
</script>

</body>
</html>