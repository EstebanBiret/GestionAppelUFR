<%@ page session="false" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Inscription</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet"
          integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH" crossorigin="anonymous">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/register.css">
</head>
<body>

<div class="register-header">
    <img src="${pageContext.request.contextPath}/images/logo_utc.jpg"
         alt="Université Toulouse Capitole">
</div>

<div class="register-container">
    <h2 class="text-center mb-4">Créer un compte</h2>

    <% if (request.getAttribute("error") != null) { %>
    <div class="alert alert-danger" role="alert">
        <%= request.getAttribute("error") %>
    </div>
    <% } %>

    <form method="post" action="${pageContext.request.contextPath}/auth/register" enctype="multipart/form-data">
        <div class="row g-3 mb-3">
            <div class="col-sm-6">
                <label for="firstName" class="form-label">Prénom <span class="text-danger">*</span></label>
                <input type="text" class="form-control" id="firstName" name="firstName"
                       placeholder="Prénom" required>
            </div>
            <div class="col-sm-6">
                <label for="lastName" class="form-label">Nom <span class="text-danger">*</span></label>
                <input type="text" class="form-control" id="lastName" name="lastName"
                       placeholder="Nom" required>
            </div>
        </div>
        <div class="mb-3">
            <label for="email" class="form-label">Adresse email <span class="text-danger">*</span></label>
            <input type="email" class="form-control" id="email" name="email"
                   placeholder="nom@exemple.com" required>
        </div>
        <div class="mb-3">
            <label for="password" class="form-label">Mot de passe <span class="text-danger">*</span></label>
            <input type="password" class="form-control" id="password" name="password"
                   placeholder="Mot de passe" required>
        </div>
        <div class="mb-4">
            <label for="confirmPassword" class="form-label">Confirmer le mot de passe <span class="text-danger">*</span></label>
            <input type="password" class="form-control" id="confirmPassword" name="confirmPassword"
                   placeholder="Confirmer le mot de passe" required>
        </div>
        <div class="mb-4">
            <label for="profilePicture" class="form-label">Photo de profil</label>
            <input class="form-control" type="file" id="profilePicture" name="profilePicture" accept="image/*">

        </div>
        <div class="d-grid">
            <button type="submit" class="btn btn-primary">S'inscrire</button>
        </div>
    </form>

    <div class="text-center mt-3">
        <a href="${pageContext.request.contextPath}/auth/login">Déjà un compte ? Se connecter</a>
    </div>
</div>

</body>
</html>
