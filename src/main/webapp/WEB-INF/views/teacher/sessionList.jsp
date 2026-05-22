<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="ut1.appel.entity.*" %>
<%@ page import="java.util.*" %>
<%@ page import="java.time.LocalDate" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%
    Users me = (Users) session.getAttribute("currentUser");
    if (me == null) { response.sendRedirect(request.getContextPath() + "/auth/login"); return; }

    List<ut1.appel.entity.Session> sessions =
            (List<ut1.appel.entity.Session>) request.getAttribute("sessions");
    String mode = (String) request.getAttribute("mode");
    if (sessions == null) sessions = new ArrayList<>();

    boolean isUpcoming = "upcoming".equals(mode);
    String pageTitle = isUpcoming ? "Cours à venir" : "Cours passés";

    DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("HH:mm");
    DateTimeFormatter dayFmt  = DateTimeFormatter.ofPattern("EEEE dd/MM/yyyy", Locale.FRENCH);
    LocalDate today = LocalDate.now();

    LinkedHashMap<LocalDate, List<ut1.appel.entity.Session>> byDay = new LinkedHashMap<>();
    for (ut1.appel.entity.Session s : sessions) {
        byDay.computeIfAbsent(s.getSessionDate(), k -> new ArrayList<>()).add(s);
    }
%>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title><%= pageTitle %></title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/teacher.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/sessionList.css">
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
        <span class="user-chip"><%= me.getFirstName() %> <%= me.getLastName() %></span>
        <a href="${pageContext.request.contextPath}/auth/logout" class="btn-logout">Se déconnecter</a>
    </div>
</header>

<main>
    <div class="page-header">
        <div><h1><%= pageTitle %></h1></div>
        <a href="${pageContext.request.contextPath}/enseignant?action=home" class="back-link">
            ← Retour au tableau de bord
        </a>
    </div>

    <div class="tc-timeline">
        <% if (sessions.isEmpty()) { %>
            <div class="tc-empty">
                <%= isUpcoming ? "Aucun cours à venir." : "Aucun cours passé." %>
            </div>
        <% } else {
            for (Map.Entry<LocalDate, List<ut1.appel.entity.Session>> entry : byDay.entrySet()) {
                LocalDate d = entry.getKey();
                String dayLabel = d.equals(today) ? "Aujourd'hui" : d.format(dayFmt);
        %>
        <div class="tc-day-group">
            <div class="tc-day-header">
                <%= dayLabel %>
                <div class="tc-day-line"></div>
            </div>

            <% for (ut1.appel.entity.Session s : entry.getValue()) {
                String appelUrl  = request.getContextPath() + "/enseignant/appel?sessionId=" + s.getId();
                String statsUrl  = (s.getCourse() != null)
                    ? request.getContextPath() + "/enseignant?action=stats&courseId=" + s.getCourse().getId()
                    : "#";
            %>

            <% if (isUpcoming) { %>
                <%-- Cours à venir : simple lien --%>
                <a class="tc-event"
                   href="<%= appelUrl %>">
                    <div class="tc-time">
                        <span class="tc-time-start"><%= s.getStartTime().format(timeFmt) %></span>
                        <span class="tc-time-end"><%= s.getEndTime().format(timeFmt) %></span>
                    </div>
                    <div class="tc-info">
                        <div class="tc-subject"><%= s.getCourse() != null ? s.getCourse().getName() : "—" %></div>
                        <div class="tc-meta">
                            <% if (s.getCourse() != null && s.getCourse().getStudentClass() != null) { %>
                                <span class="tc-chip tc-chip-class"><%= s.getCourse().getStudentClass().getName() %></span>
                            <% } %>
                            <% if (s.getStudentGroups() != null && !s.getStudentGroups().isEmpty()) {
                                for (StudentGroup sg : s.getStudentGroups()) { %>
                                    <span class="tc-chip tc-chip-group"><%= sg.getName() %></span>
                            <%  }
                            } else { %>
                                <span class="tc-chip tc-chip-group">Classe entière</span>
                            <% } %>
                        </div>
                    </div>
                    <span class="tc-arrow">→</span>
                </a>

            <% } else { %>
                <div class="tc-event-wrapper">
                    <div class="tc-event past"
                         onclick="window.location='<%= appelUrl %>'">
                        <div class="tc-time">
                            <span class="tc-time-start"><%= s.getStartTime().format(timeFmt) %></span>
                            <span class="tc-time-end"><%= s.getEndTime().format(timeFmt) %></span>
                        </div>
                        <div class="tc-info">
                            <div class="tc-subject"><%= s.getCourse() != null ? s.getCourse().getName() : "—" %></div>
                            <div class="tc-meta">
                                <% if (s.getCourse() != null && s.getCourse().getStudentClass() != null) { %>
                                    <span class="tc-chip tc-chip-class"><%= s.getCourse().getStudentClass().getName() %></span>
                                <% } %>
                                <% if (s.getStudentGroups() != null && !s.getStudentGroups().isEmpty()) {
                                    for (StudentGroup sg : s.getStudentGroups()) { %>
                                        <span class="tc-chip tc-chip-group"><%= sg.getName() %></span>
                                <%  }
                                } else { %>
                                    <span class="tc-chip tc-chip-group">Classe entière</span>
                                <% } %>
                            </div>
                        </div>
                        <span class="tc-arrow">→</span>
                    </div>
                    <a class="btn-stats" href="<%= statsUrl %>">Voir le bilan</a>
                </div>
            <% } %>

            <% } %>
        </div>
        <% } } %>
    </div>
</main>
</body>
</html>