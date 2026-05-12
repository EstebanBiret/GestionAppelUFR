<%@ page session="false" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head><title>Connexion</title></head>
<body>
<h2>Connexion</h2>

<% if ("true".equals(request.getParameter("registered"))) { %>
    <p style="color:green">Compte créé ! En attente d'activation par un administrateur.</p>
<% } %>
<% if (request.getAttribute("error") != null) { %>
    <p style="color:red"><%= request.getAttribute("error") %></p>
<% } %>

<form method="post" action="${pageContext.request.contextPath}/auth/login">
    <input type="email"    name="email"    placeholder="Email"         required><br>
    <input type="password" name="password" placeholder="Mot de passe"  required><br>
    <button type="submit">Se connecter</button>
</form>

<a href="${pageContext.request.contextPath}/auth/register">Créer un compte</a>
</body>
</html>