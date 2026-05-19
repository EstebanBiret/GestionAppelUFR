<%@ page session="false" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Inscription</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/auth.css">
</head>
<body>

<div class="auth-header">
    <img src="${pageContext.request.contextPath}/images/logo_utc.jpg" alt="Université Toulouse Capitole">
</div>

<div class="auth-container register">
    <h2>Créer un compte</h2>

    <% if (request.getAttribute("error") != null) { %>
    <div class="error-msg">
        <%= request.getAttribute("error") %>
    </div>
    <% } %>

    <form method="post" action="${pageContext.request.contextPath}/auth/register" enctype="multipart/form-data">
        <div class="form-row">
            <div class="form-group">
                <label for="firstName" class="form-label">Prénom <span class="text-utc">*</span></label>
                <input type="text" class="form-control" id="firstName" name="firstName" placeholder="Prénom" required>
            </div>
            <div class="form-group">
                <label for="lastName" class="form-label">Nom <span class="text-utc">*</span></label>
                <input type="text" class="form-control" id="lastName" name="lastName" placeholder="Nom" required>
            </div>
        </div>

        <div class="form-group">
            <label for="email" class="form-label">Adresse email <span class="text-utc">*</span></label>
            <input type="email" class="form-control" id="email" name="email" placeholder="nom@exemple.com" required>
        </div>

        <div class="form-group">
            <label for="password" class="form-label">Mot de passe <span class="text-utc">*</span></label>
            <input type="password" class="form-control" id="password" name="password" placeholder="Mot de passe" required>
        </div>

        <div class="form-group">
            <label for="confirmPassword" class="form-label">Confirmer le mot de passe <span class="text-utc">*</span></label>
            <input type="password" class="form-control" id="confirmPassword" name="confirmPassword" placeholder="Confirmer le mot de passe" required>
        </div>

        <div class="form-group">
            <label for="profilePicture" class="form-label">Photo de profil</label>
            <input class="form-control" type="file" id="profilePicture" name="profilePicture" accept="image/*">
        </div>

        <div class="form-action">
            <button type="submit" class="btn btn-primary">S'inscrire</button>
        </div>
    </form>

    <div class="form-footer-link">
        <a href="${pageContext.request.contextPath}/auth/login">Déjà un compte ? Se connecter</a>
    </div>
</div>
</body>
</html>