<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="ut1.appel.entity.*, ut1.appel.service.TeacherService, java.util.*" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%
    Users me = (Users) session.getAttribute("currentUser");
    if (me == null) { response.sendRedirect(request.getContextPath() + "/auth/login"); return; }

    Long teacherId = me.getId();

    Session        currentSession   = TeacherService.getCurrentSession(teacherId);
    List<Session>  upcomingSessions = TeacherService.getUpcomingSessionsForTeacher(teacherId);
    List<Session>  pastSessions     = TeacherService.getPastSessionsForTeacher(teacherId);

    DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("HH:mm");
%>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Tableau de bord — Enseignant</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/teacher.css">
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

<%
    java.util.LinkedHashMap<java.time.LocalDate, java.util.List<Session>> upcomingByDay = new java.util.LinkedHashMap<>();
    for (Session s : upcomingSessions) {
        upcomingByDay.computeIfAbsent(s.getSessionDate(), k -> new java.util.ArrayList<>()).add(s);
    }
    java.util.LinkedHashMap<java.time.LocalDate, java.util.List<Session>> pastByDay = new java.util.LinkedHashMap<>();
    for (Session s : pastSessions) {
        pastByDay.computeIfAbsent(s.getSessionDate(), k -> new java.util.ArrayList<>()).add(s);
    }

    java.time.LocalDate today = java.time.LocalDate.now();
    DateTimeFormatter dayFmt  = DateTimeFormatter.ofPattern("EEEE dd/MM/yyyy", java.util.Locale.FRENCH);
%>

<main>
    <div class="page-header">
        <div>
            <h1>Bonjour, <%= me.getFirstName() %> 👋</h1>
            <p class="page-subtitle">
                Voici un aperçu de vos cours.
            </p>
        </div>
    </div>

    <% if (currentSession != null) { %>
    <div class="tc-section-label">
        <span class="tc-live-dot"></span> Cours en ce moment
    </div>
    <div class="tc-card-current">
        <a class="tc-event"
           href="${pageContext.request.contextPath}/enseignant/appel?sessionId=<%= currentSession.getId() %>">
            <div class="tc-time">
                <span class="tc-time-start"><%= currentSession.getStartTime().format(timeFmt) %></span>
                <span class="tc-time-end"><%= currentSession.getEndTime().format(timeFmt) %></span>
            </div>
            <div class="tc-info">
                <div class="tc-subject"><%= currentSession.getCourse() != null ? currentSession.getCourse().getName() : "—" %></div>
                <div class="tc-meta">
                    <% if (currentSession.getStudentClasses() != null) { for (StudentClass sc : currentSession.getStudentClasses()) { %>
                    <span class="tc-chip tc-chip-class"><%= sc.getName() %></span>
                    <% } } %>
                    <% if (currentSession.getStudentGroups() != null) { for (StudentGroup sg : currentSession.getStudentGroups()) { %>
                    <span class="tc-chip tc-chip-group"><%= sg.getName() %></span>
                    <% } } %>
                </div>
            </div>
            <span class="tc-arrow">→</span>
        </a>
    </div>
    <% } %>

    <div class="tc-section-label">
        Prochains cours
        <span class="tc-badge"><%= upcomingSessions.size() %></span>
    </div>
    <div class="tc-timeline">
        <% if (upcomingSessions.isEmpty()) { %>
        <div class="tc-empty">Aucun cours à venir.</div>
        <% } else { for (java.util.Map.Entry<java.time.LocalDate, java.util.List<Session>> entry : upcomingByDay.entrySet()) {
            java.time.LocalDate d = entry.getKey();
            String dayLabel = d.equals(today) ? "Aujourd'hui" : d.format(dayFmt);
        %>
        <div class="tc-day-group">
            <div class="tc-day-header">
                <%= dayLabel %> <div class="tc-day-line"></div>
            </div>
            <% for (Session s : entry.getValue()) { %>
            <a class="tc-event"
               href="${pageContext.request.contextPath}/enseignant/appel?sessionId=<%= s.getId() %>"
               >
                <div class="tc-time">
                    <span class="tc-time-start"><%= s.getStartTime().format(timeFmt) %></span>
                    <span class="tc-time-end"><%= s.getEndTime().format(timeFmt) %></span>
                </div>
                <div class="tc-info">
                    <div class="tc-subject"><%= s.getCourse() != null ? s.getCourse().getName() : "—" %></div>
                    <div class="tc-meta">
                        <% if (s.getStudentClasses() != null) { for (StudentClass sc : s.getStudentClasses()) { %>
                        <span class="tc-chip tc-chip-class"><%= sc.getName() %></span>
                        <% } } %>
                        <% if (s.getStudentGroups() != null) { for (StudentGroup sg : s.getStudentGroups()) { %>
                        <span class="tc-chip tc-chip-group"><%= sg.getName() %></span>
                        <% } } %>
                    </div>
                </div>
                <span class="tc-arrow">→</span>
            </a>
            <% } %>
        </div>
        <% } } %>
    </div>

    <div class="plus">
        <a href="${pageContext.request.contextPath}/enseignant?action=upcoming"
           class="btn btn-secondary">
            Voir tous les cours à venir →
        </a>
    </div>

    <div class="tc-section-label">
        Derniers cours
        <span class="tc-badge muted"><%= pastSessions.size() %></span>
    </div>
    <div class="tc-timeline">
        <% if (pastSessions.isEmpty()) { %>
        <div class="tc-empty">Aucun cours passé.</div>
        <% } else { for (java.util.Map.Entry<java.time.LocalDate, java.util.List<Session>> entry : pastByDay.entrySet()) { %>
        <div class="tc-day-group">
            <div class="tc-day-header">
                <%= entry.getKey().format(dayFmt) %> <div class="tc-day-line"></div>
            </div>
            <% for (Session s : entry.getValue()) { %>
            <a class="tc-event past"
               href="${pageContext.request.contextPath}/enseignant/appel?sessionId=<%= s.getId() %>"
               >
                <div class="tc-time">
                    <span class="tc-time-start"><%= s.getStartTime().format(timeFmt) %></span>
                    <span class="tc-time-end"><%= s.getEndTime().format(timeFmt) %></span>
                </div>
                <div class="tc-info">
                    <div class="tc-subject"><%= s.getCourse() != null ? s.getCourse().getName() : "—" %></div>
                    <div class="tc-meta">
                        <% if (s.getStudentClasses() != null) { for (StudentClass sc : s.getStudentClasses()) { %>
                        <span class="tc-chip tc-chip-class"><%= sc.getName() %></span>
                        <% } } %>
                        <% if (s.getStudentGroups() != null) { for (StudentGroup sg : s.getStudentGroups()) { %>
                        <span class="tc-chip tc-chip-group"><%= sg.getName() %></span>
                        <% } } %>
                    </div>
                </div>
                <span class="tc-arrow">→</span>
            </a>
            <% } %>
        </div>
        <% } } %>
    </div>

    <div class="plus">
        <a href="${pageContext.request.contextPath}/enseignant?action=past"
           class="btn btn-secondary">
            Voir tous les cours passés →
        </a>
    </div>

</main>

</body>
</html>