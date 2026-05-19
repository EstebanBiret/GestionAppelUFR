<%@ page session="false" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Connexion</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/auth.css">
</head>
<body>

<div class="auth-header">
    <img src="${pageContext.request.contextPath}/images/logo_utc.jpg" alt="Université Toulouse Capitole">
</div>

<div class="auth-container login">
    <h2>Connexion</h2>

    <% if (request.getAttribute("error") != null) { %>
    <div class="error-msg">
        <%= request.getAttribute("error") %>
    </div>
    <% } %>

    <form method="post" action="${pageContext.request.contextPath}/auth/login">
        <div class="form-group">
            <label for="email" class="form-label">Adresse email</label>
            <input type="email" class="form-control" id="email" name="email"
                   placeholder="nom@exemple.com"
                   value="${emailValue}"
                   required>
        </div>
        <div class="form-group">
            <label for="password" class="form-label">Mot de passe</label>
            <input type="password" class="form-control" id="password" name="password"
                   placeholder="Mot de passe" required>
        </div>
        <div class="form-action">
            <button type="submit" class="btn btn-primary">Se connecter</button>
        </div>
    </form>

    <div class="form-footer-link">
        <a href="${pageContext.request.contextPath}/auth/register">Créer un compte</a>
    </div>
</div>
</body>
</html>