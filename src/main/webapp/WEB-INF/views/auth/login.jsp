<%@ page session="false" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Connexion</title>
    <!-- Intégration de Bootstrap 5 via CDN -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet"
          integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH" crossorigin="anonymous">
    <!-- Lien vers votre fichier CSS personnalisé -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/login.css">
</head>
<body>

<div class="login-header">
    <img src="${pageContext.request.contextPath}/images/logo_utc.jpg"
         alt="Université Toulouse Capitole">
</div>

<div class="login-container">
    <h2 class="text-center mb-4">Connexion</h2>

    <% if ("true".equals(request.getParameter("registered"))) { %>
    <div class="alert alert-success" role="alert">
        Compte créé ! En attente d'activation par un administrateur.
    </div>
    <% } %>
    <% if (request.getAttribute("error") != null) { %>
    <div class="alert alert-danger" role="alert">
        <%= request.getAttribute("error") %>
    </div>
    <% } %>

    <form method="post" action="${pageContext.request.contextPath}/auth/login">
        <div class="mb-3">
            <label for="email" class="form-label">Adresse email</label>
            <input type="email" class="form-control" id="email" name="email" placeholder="nom@exemple.com" required>
        </div>
        <div class="mb-3">
            <label for="password" class="form-label">Mot de passe</label>
            <input type="password" class="form-control" id="password" name="password" placeholder="Mot de passe"
                   required>
        </div>
        <div class="d-grid">
            <button type="submit" class="btn btn-primary">Se connecter</button>
        </div>
    </form>

    <div class="text-center mt-3">
        <a href="${pageContext.request.contextPath}/auth/register">Créer un compte</a>
    </div>
</div>

</body>
</html>
