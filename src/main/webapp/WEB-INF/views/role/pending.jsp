<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="ut1.appel.entity.Users" %>
<%
    Users u = (Users) session.getAttribute("currentUser");
    if (u == null) {
        response.sendRedirect(request.getContextPath() + "/auth/login");
        return;
    }
%>
<html>
<head><title>En attente d'activation</title></head>
<body>
  <h2>Bienvenue <%= u.getFirstName() %> <%= u.getLastName() %></h2>
  <p>Votre compte est en attente d'activation par un administrateur.</p>
  <a href="${pageContext.request.contextPath}/auth/logout">Se déconnecter</a>
</body>
</html>