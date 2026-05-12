<%@ page session="false" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head><title>Inscription</title></head>
<body>
<h2>Créer un compte</h2>

<% if (request.getAttribute("error") != null) { %>
    <p style="color:red"><%= request.getAttribute("error") %></p>
<% } %>

<form method="post" action="${pageContext.request.contextPath}/auth/register">
    <input type="text"     name="firstName"       placeholder="Prénom"           required><br>
    <input type="text"     name="lastName"        placeholder="Nom"              required><br>
    <input type="email"    name="email"           placeholder="Email"            required><br>
    <input type="password" name="password"        placeholder="Mot de passe"     required><br>
    <input type="password" name="confirmPassword" placeholder="Confirmer le MDP" required><br>
    <button type="submit">S'inscrire</button>
</form>

<a href="${pageContext.request.contextPath}/auth/login">Déjà un compte ? Se connecter</a>
</body>
</html>