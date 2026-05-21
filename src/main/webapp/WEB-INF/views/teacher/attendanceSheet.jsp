<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="ut1.appel.entity.*" %>
<%@ page import="ut1.appel.enums.Role" %>
<%@ page import="java.util.List" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page import="java.util.Locale" %>
<%
    Users me = (Users) session.getAttribute("currentUser");
    if (me == null) {
        response.sendRedirect(request.getContextPath() + "/auth/login");
        return;
    }

    ut1.appel.entity.Session courseSession =
            (ut1.appel.entity.Session) request.getAttribute("courseSession");
    AttendanceSheet sheet = (AttendanceSheet) request.getAttribute("sheet");

    DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("HH:mm");
    DateTimeFormatter dayFmt = DateTimeFormatter.ofPattern("EEEE dd MMMM yyyy", Locale.FRENCH);
%>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Fiche d'appel</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/teacher.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/attendanceSheet.css">
    <script>
        function avatarFallback(img) {
            var initials = img.getAttribute('data-initials') || '?';
            var wrap = img.parentNode;
            var div = document.createElement('div');
            div.className = 'as-avatar-placeholder';
            div.textContent = initials;
            wrap.replaceChild(div, img);
        }
    </script>
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
        <div>
            <h1>Fiche d'appel</h1>
        </div>
        <a href="${pageContext.request.contextPath}/enseignant?action=home">← Retour au tableau de bord</a>
    </div>
    <% if (courseSession != null) { %>
    <div class="as-session-card">
        <div class="as-session-title">
            <%= courseSession.getCourse() != null ? courseSession.getCourse().getName() : "—" %>
        </div>
        <div class="as-session-meta">
            <% if (courseSession.getStudentClasses() != null) {
                for (StudentClass sc : courseSession.getStudentClasses()) { %>
            <span class="as-chip as-chip-class"><%= sc.getName() %></span>
            <% }
            } %>
            <% if (courseSession.getStudentGroups() != null) {
                for (StudentGroup sg : courseSession.getStudentGroups()) { %>
            <span class="as-chip as-chip-group"><%= sg.getName() %></span>
            <% }
            } %>
            <span class="as-sep">·</span>
            <span>
                <%= courseSession.getSessionDate() != null
                        ? courseSession.getSessionDate().format(dayFmt) : "—" %>
            </span>
            <span class="as-sep">·</span>
            <span>
                <%= courseSession.getStartTime() != null
                        ? courseSession.getStartTime().format(timeFmt) : "—" %>
                →
                <%= courseSession.getEndTime() != null
                        ? courseSession.getEndTime().format(timeFmt) : "—" %>
            </span>
        </div>
    </div>
    <% if (sheet == null || sheet.getAttendanceRows() == null || sheet.getAttendanceRows().isEmpty()) { %>
    <div class="card">
        <div class="as-empty">Aucun étudiant dans cette feuille d'appel.</div>
    </div>
    <% } else {
        List<AttendanceRow> rows = sheet.getAttendanceRows();
    %>
    <div class="as-table-wrap">
        <table class="as-table">
            <thead>
            <tr>
                <th class="as-col-photo">Photo</th>
                <th>Prénom</th>
                <th>Nom</th>
                <th class="as-col-formation">FI / FA</th>
            </tr>
            </thead>
            <tbody>
            <% for (AttendanceRow row : rows) {
                Users student = row.getUser();
                if (student == null) continue;
                String initials = "";
                if (student.getFirstName() != null && !student.getFirstName().isEmpty())
                    initials += student.getFirstName().charAt(0);
                if (student.getLastName() != null && !student.getLastName().isEmpty())
                    initials += student.getLastName().charAt(0);
                boolean isFi = student.getRole() == Role.ETUDIANT_FI;
                boolean isFa = student.getRole() == Role.ETUDIANT_FA;
                String picturePath = student.getPicturePath() != null ? student.getPicturePath() : "";
            %>
            <tr>
                <td>
                    <div class="as-avatar-wrap">
                        <img class="as-avatar"
                             src="${pageContext.request.contextPath}/<%= picturePath %>"
                             alt="<%= student.getFirstName() %> <%= student.getLastName() %>"
                             data-initials="<%= initials %>"
                             onerror="avatarFallback(this)">
                    </div>
                </td>
                <td><span class="as-student-name"><%= student.getFirstName() %></span></td>
                <td><span class="as-student-name"><%= student.getLastName() %></span></td>
                <td>
                    <% if (isFi) { %>
                    <span class="as-badge-fi">FI</span>
                    <% } else if (isFa) { %>
                    <span class="as-badge-fa">FA</span>
                    <% } else { %>
                    <span class="as-muted-text">—</span>
                    <% } %>
                </td>
            </tr>
            <% } %>
            </tbody>
        </table>
    </div>
    <% } %>
    <% } %>
</main>
</body>
</html>
