<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="ut1.appel.entity.Users" %>
<%
    Users etudiant = (Users) session.getAttribute("currentUser");
    if (etudiant == null) {
        response.sendRedirect(request.getContextPath() + "/auth/login");
        return;
    }
%>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Transmettre un justificatif - UT Capitole</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
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
        <div class="user-chip"><%= etudiant.getFirstName() %> <%= etudiant.getLastName() %></div>
        <a href="${pageContext.request.contextPath}/etudiant" class="btn btn-secondary">Retour à l'accueil</a>
    </div>
</header>

<main>
    <div class="page-header">
        <h1>Transmettre un justificatif</h1>
    </div>

    <div class="card">
        <div class="card-header">
            Envoi de document (médical, administratif...)
        </div>
        <div class="card-body">

            <% if (request.getAttribute("error") != null) { %>
            <div class="error-msg"><%= request.getAttribute("error") %></div>
            <% } %>

            <% if (request.getAttribute("success") != null) { %>
            <div class="alert-success"><%= request.getAttribute("success") %></div>
            <% } %>

            <form action="${pageContext.request.contextPath}/etudiant/justification" method="POST" enctype="multipart/form-data">

                <div class="form-group">
                    <label for="justificatifFile" class="form-label">Document justificatif (PDF ou Image) <span class="text-utc">*</span></label>
                    <input type="file" id="justificatifFile" name="justificatifFile" class="form-control" accept=".pdf, image/*" required>
                </div>

                <p style="font-size: 0.85rem; color: var(--txt-muted); margin-bottom: 1.5rem; line-height: 1.5;">
                    <em>Note : Votre justificatif sera transmis au service de la scolarité.
                        Un gestionnaire se chargera de l'étudier et de l'associer à vos absences correspondantes.
                        L'envoi de ce document ne garantit pas la justification immédiate.</em>
                </p>

                <div class="form-action" style="display: flex; justify-content: flex-end;">
                    <button type="submit" class="btn btn-primary">Envoyer le document</button>
                </div>
            </form>

        </div>
    </div>
</main>

</body>
</html>