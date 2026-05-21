<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="ut1.appel.entity.Users" %>
<%
    Users u = (Users) session.getAttribute("currentUser");
    if (u == null) {
        response.sendRedirect(request.getContextPath() + "/auth/login");
        return;
    }
    String success = request.getParameter("success");
    String picturePath = u.getPicturePath() != null ? u.getPicturePath() : "default.jpg";
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Mon profil</title>
</head>
<body>

<h2>Mon profil</h2>

<% if ("1".equals(success)) { %>
<p style="color:green;">Profil mis à jour avec succès.</p>
<% } %>

<img src="${pageContext.request.contextPath}/images/users/<%= picturePath %>"
     alt="Photo de profil" width="120" height="120"
     style="border-radius:50%; object-fit:cover;"/>

<form action="${pageContext.request.contextPath}/etudiant/profil"
      method="post" enctype="multipart/form-data">

    <p>
        <label>Prénom : </label>
        <span><%= u.getFirstName() %></span>
    </p>
    <p>
        <label>Nom : </label>
        <span><%= u.getLastName() %></span>
    </p>
    <p>
        <label for="email">Adresse email :</label>
        <input type="email" id="email" name="email" value="<%= u.getEmail() %>" required/>
    </p>
    <p>
        <label for="photo">Photo de profil :</label>
        <input type="file" id="photo" name="photo" accept="image/*"/>
    </p>

    <button type="submit">Enregistrer</button>
</form>

<a href="${pageContext.request.contextPath}/auth/logout">Se déconnecter</a>

</body>
</html>