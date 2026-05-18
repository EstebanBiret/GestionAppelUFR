<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="ut1.appel.entity.Users" %>
<%
    Users u = (Users) session.getAttribute("currentUser");
    if (u == null) {
        response.sendRedirect(request.getContextPath() + "/auth/login");
        return;
    }
%>
<html><body>
  <h2>Bienvenue <%= u.getFirstName() %> — <%= u.getRole() %></h2>
  <a href="${pageContext.request.contextPath}/auth/logout">Se déconnecter</a>
</body></html>