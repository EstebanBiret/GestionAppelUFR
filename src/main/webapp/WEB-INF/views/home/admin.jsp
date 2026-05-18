<%@ page import="java.util.List" %>
<%@ page import="ut1.appel.entity.Users" %>
<%@ page import="java.util.Map" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Administration des comptes</title>
    <style>
        body { font-family: Arial, sans-serif; padding: 20px; }

        .header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            border-bottom: 2px solid #ddd;
            padding-bottom: 10px;
            margin-bottom: 20px;
        }
        .header h1 { margin: 0; }

        .btn-logout {
            background-color: #dc3545;
            color: white;
            padding: 10px 15px;
            text-decoration: none;
            border-radius: 4px;
            font-weight: bold;
        }
        .btn-logout:hover { background-color: #c82333; }

        /* Styles du tableau */
        table { width: 100%; border-collapse: collapse; }
        th, td { border: 1px solid #ddd; padding: 12px; text-align: left; }
        th { background-color: #0056b3; color: white; }
        tr:nth-child(even) { background-color: #f2f2f2; }
        select, button { padding: 5px; }
        .btn-save { background-color: #28a745; color: white; border: none; cursor: pointer; border-radius: 3px; }
        .btn-save:hover { background-color: #218838; }
    </style>
</head>
<body>

<div class="header">
    <div>
        <h1>Panneau d'Administration</h1>
        <p style="margin-top: 5px; color: #555;">Gérez les rôles des utilisateurs inscrits sur la plateforme.</p>
    </div>

    <a href="${pageContext.request.contextPath}/auth/logout" class="btn-logout">Déconnexion</a>
</div>

<table>
    <thead>
    <tr>
        <th>ID</th>
        <th>Prénom & Nom</th>
        <th>Email</th>
        <th>Rôle Actuel</th>
        <th>Action</th>
    </tr>
    </thead>
    <tbody>
    <%
        List<Users> usersList = (List<Users>) request.getAttribute("users");

        if (usersList != null && !usersList.isEmpty()) {
            for (Users u : usersList) {
                String role = (u.getRole() != null) ? u.getRole().toString() : "PENDING";
    %>
    <tr>
        <td><%= u.getId() %></td>
        <td><%= u.getFirstName() %> <%= u.getLastName() %></td>
        <td><%= u.getEmail() %></td>

        <form id="form_<%= u.getId() %>" action="${pageContext.request.contextPath}/admin" method="post" style="display: none;">
            <input type="hidden" name="userId" value="<%= u.getId() %>">
        </form>

        <td>
            <select name="role" form="form_<%= u.getId() %>">
                <%
                    Map<String, String> allRoles = (Map<String, String>) request.getAttribute("allRoles");
                    for (Map.Entry<String, String> entry : allRoles.entrySet()) {
                        String roleKey = entry.getKey();
                        String roleLabel = entry.getValue();
                %>
                <option value="<%= roleKey %>" <%= roleKey.equals(role) ? "selected" : "" %>>
                    <%= roleLabel %>
                </option>
                <%
                    }
                %>
            </select>
        </td>
        <td>
            <button type="submit" form="form_<%= u.getId() %>" class="btn-save">Enregistrer</button>
        </td>
    </tr>
    <%
        }
    } else {
    %>
    <tr>
        <td colspan="5" style="text-align: center;">Aucun utilisateur trouvé.</td>
    </tr>
    <%
        }
    %>
    </tbody>
</table>

</body>
</html>