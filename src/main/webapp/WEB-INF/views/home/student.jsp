<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="ut1.appel.entity.Users" %>
<%
    Users u = (Users) session.getAttribute("currentUser");
    if (u == null) {
        response.sendRedirect(request.getContextPath() + "/auth/login");
        return;
    }
%>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Espace Étudiant - UT Capitole</title>
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
    <div class="welcome">
        <div class="welcome-bar"></div>
        <div>
            <div class="welcome-title">Bonjour, <%= u.getFirstName() %> <%= u.getLastName() %></div>
            <div class="welcome-sub">Espace Étudiant — que souhaitez-vous faire ?</div>
        </div>
    </div>

    <div class="page-header">
        <h2>Mes actions rapides</h2>
    </div>

    <div class="nav-grid">
        <a href="${pageContext.request.contextPath}/etudiant/justification/nouveau" class="nav-card">
            <div class="nav-icon">📄</div>
            <div>
                <div class="nav-card-title">Justifier une absence</div>
                <div class="nav-card-sub">Transmettre un certificat médical ou un autre justificatif</div>
            </div>
        </a>

        <a href="${pageContext.request.contextPath}/etudiant/justification/liste" class="nav-card">
            <div class="nav-icon">🔍</div>
            <div>
                <div class="nav-card-title">Suivre mes justificatifs</div>
                <div class="nav-card-sub">Consulter l'état de validation de vos documents par la scolarité</div>
            </div>
        </a>
    </div>

</main>
   <script>
       window._contextPath = '<%= request.getContextPath() %>';
   </script>
   <script src="${pageContext.request.contextPath}/js/student.js"></script>
</body>
</html>