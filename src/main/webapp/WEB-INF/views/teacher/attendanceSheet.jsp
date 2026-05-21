<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="ut1.appel.entity.*" %>
<%@ page import="ut1.appel.enums.Role" %>
<%@ page import="ut1.appel.enums.AttendanceRowStatus" %>
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
    DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("dd/MM/yyyy à HH:mm");

    boolean isSigned = (sheet != null && Boolean.TRUE.equals(sheet.getIsSigned()));
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
    <%
        String flashSuccess = (String) session.getAttribute("flashSuccess");
        String flashError = (String) session.getAttribute("flashError");
        if (flashSuccess != null) {
    %>
    <div class="alert alert-success"><%= flashSuccess %>
    </div>
    <%
            session.removeAttribute("flashSuccess");
        }
        if (flashError != null) {
    %>
    <div class="alert alert-danger"><%= flashError %>
    </div>
    <%
            session.removeAttribute("flashError");
        }
    %>

    <div class="page-header page-header-flex">
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
            <span class="as-time-container">
                <%= courseSession.getStartTime() != null
                        ? courseSession.getStartTime().format(timeFmt) : "—" %>
                →
                <%= courseSession.getEndTime() != null
                        ? courseSession.getEndTime().format(timeFmt) : "—" %>

                <%
                    boolean isEnCours = false;
                    if (courseSession.getSessionDate() != null && courseSession.getStartTime() != null && courseSession.getEndTime() != null) {
                        java.time.LocalDate today = java.time.LocalDate.now();
                        java.time.LocalTime now = java.time.LocalTime.now();

                        if (courseSession.getSessionDate().equals(today) &&
                                !now.isBefore(courseSession.getStartTime()) &&
                                !now.isAfter(courseSession.getEndTime())) {
                            isEnCours = true;
                        }
                    }
                    if (isEnCours) {
                %>
                    <span class="as-badge-live">En cours</span>
                <% } %>
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

    <form id="attendanceForm" action="${pageContext.request.contextPath}/enseignant/appel/save" method="POST">
        <input type="hidden" name="sessionId" value="<%= courseSession.getId() %>">
        <input type="hidden" id="submitAction" name="submitAction" value="save">

        <div class="as-table-wrap">
            <table class="as-table">
                <thead>
                <tr>
                    <th>Prénom</th>
                    <th>Nom</th>
                    <th class="as-col-formation">FI / FA</th>
                    <th class="as-col-photo">Photo</th>
                    <th class="as-col-status">Statut</th>
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

                    AttendanceRowStatus currentStatus = row.getStatus() != null ? row.getStatus() : AttendanceRowStatus.PRESENT;

                    boolean isAbj = (currentStatus == AttendanceRowStatus.ABJ);
                    boolean isDisabled = isAbj || isSigned;
                %>

                <tr class="<%= isAbj ? "as-row-abj" : "" %>">

                    <td><span class="as-student-name"><%= student.getFirstName() %></span></td>

                    <td>
                        <span class="as-student-name text-uppercase"><%= student.getLastName() %></span>
                        <% if (isAbj) { %>
                        <span class="as-badge-abj">Justifié</span>
                        <% } %>
                    </td>

                    <td>
                        <% if (isFi) { %>
                        <span class="as-badge-fi">FI</span>
                        <% } else if (isFa) { %>
                        <span class="as-badge-fa">FA</span>
                        <% } else { %>
                        <span class="as-muted-text">—</span>
                        <% } %>
                    </td>

                    <td>
                        <div class="as-avatar-wrap">
                            <img class="as-avatar"
                                 src="${pageContext.request.contextPath}/<%= picturePath %>"
                                 alt="<%= student.getFirstName() %> <%= student.getLastName() %>"
                                 data-initials="<%= initials %>"
                                 onerror="avatarFallback(this)">
                        </div>
                    </td>

                    <td class="as-col-status">
                        <div class="status-toggle-group">
                            <input type="radio" id="present_<%= student.getId() %>" name="status_<%= student.getId() %>"
                                   value="PRESENT" <%= currentStatus == AttendanceRowStatus.PRESENT ? "checked" : "" %> <%= isDisabled ? "disabled" : "" %>>
                            <label for="present_<%= student.getId() %>" class="status-btn btn-present" title="Présent">
                                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3"
                                     stroke-linecap="round" stroke-linejoin="round">
                                    <polyline points="20 6 9 17 4 12"></polyline>
                                </svg>
                            </label>

                            <input type="radio" id="absent_<%= student.getId() %>" name="status_<%= student.getId() %>"
                                   value="ABSENT" <%= (currentStatus == AttendanceRowStatus.ABSENT || currentStatus == AttendanceRowStatus.ABJ) ? "checked" : "" %> <%= isDisabled ? "disabled" : "" %>>
                            <label for="absent_<%= student.getId() %>" class="status-btn btn-absent" title="Absent">
                                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3"
                                     stroke-linecap="round" stroke-linejoin="round">
                                    <line x1="18" y1="6" x2="6" y2="18"></line>
                                    <line x1="6" y1="6" x2="18" y2="18"></line>
                                </svg>
                            </label>

                            <input type="radio" id="late_<%= student.getId() %>" name="status_<%= student.getId() %>"
                                   value="LATE" <%= currentStatus == AttendanceRowStatus.EN_RETARD ? "checked" : "" %> <%= isDisabled ? "disabled" : "" %>>
                            <label for="late_<%= student.getId() %>" class="status-btn btn-late" title="En retard">
                                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"
                                     stroke-linecap="round" stroke-linejoin="round">
                                    <circle cx="12" cy="12" r="10"></circle>
                                    <polyline points="12 6 12 12 16 14"></polyline>
                                </svg>
                            </label>

                        </div>
                    </td>
                </tr>
                <% } %>
                </tbody>
            </table>
        </div>

        <div class="as-footer-actions">
            <a href="${pageContext.request.contextPath}/enseignant?action=home" class="as-footer-link">Retour</a>

            <% if (isSigned) { %>
            <div style="color: #047857; font-weight: 600; display: flex; align-items: center; gap: 0.5rem;">
                Fiche signée le <%= sheet.getValidationDate() != null ? sheet.getValidationDate().format(dateFmt) : "" %>
            </div>
            <% } else { %>
            <div style="display: flex; gap: 1rem;">
                <div style="display: flex; gap: 1rem;">
                    <button type="submit" name="submitAction" value="save" class="btn btn-secondary">Enregistrer brouillon</button>
                    <button type="button" class="btn btn-primary btn-valider-appel" onclick="document.getElementById('confirmModal').classList.add('active')">Signer la fiche</button>
                </div>
            </div>
            <% } %>
        </div>
    </form>
    <% } %>
    <% } %>
</main>

<div id="confirmModal" class="as-modal-overlay">
    <div class="as-modal">
        <div class="as-modal-title">Signer la fiche d'appel</div>
        <div class="as-modal-body">
            Êtes-vous sûr de vouloir signer cette fiche de présence ? <br><br>
            <strong>Attention :</strong> Une fois signée, la fiche sera verrouillée et vous ne pourrez plus la modifier.
            Les absences seront transmises automatiquement à la scolarité.
        </div>
        <div class="as-modal-actions">
            <button type="button" class="btn btn-secondary" onclick="closeConfirmModal()">Annuler</button>
            <button type="button" class="btn btn-primary" onclick="confirmSign()">Oui, signer l'appel</button>
        </div>
    </div>
</div>

<script src="${pageContext.request.contextPath}/js/attendanceSheet.js"></script>
</body>
</html>