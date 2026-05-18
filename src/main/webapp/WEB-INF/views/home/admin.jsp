<link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="ut1.appel.entity.Users" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Administration des rôles - UT1</title>

    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">

    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin.css">
</head>
<body>

<header>
    <div class="logo">
        <div class="logo-bar"></div>
        <div>
            <div class="logo-text">Appel UT1</div>
            <div class="logo-sub">Gestion des rôles</div>
        </div>
    </div>
    <div class="header-right">
        <div class="user-chip">Administrateur</div>
        <a href="${pageContext.request.contextPath}/auth/logout" class="btn-logout">Déconnexion</a>
    </div>
</header>

<main>
    <div class="page-header">
        <div>
            <h1>Administration</h1>
            <p style="color: var(--txt-muted); font-size: .9rem; margin-top: .25rem;">
                Gérez les rôles et les accès des utilisateurs de la plateforme.
            </p>
        </div>
    </div>

    <div class="card">
        <div class="card-header">
            Liste des utilisateurs
        </div>

        <div class="card-body" style="padding: 0; overflow-x: auto;">
            <table class="table-container">
                <thead>
                <tr>
                    <th>ID</th>
                    <th>Utilisateur</th>
                    <th>Email</th>
                    <th>Rôle</th>
                    <th>Action</th>
                </tr>
                </thead>
                <tbody>
                <%
                    List<Users> usersList = (List<Users>) request.getAttribute("users");

                    if (usersList != null && !usersList.isEmpty()) {
                        for (Users u : usersList) {
                            String role = (u.getRole() != null) ? u.getRole().name() : "PENDING";
                %>
                <tr>
                    <td style="color: var(--txt-muted);"><%= u.getId() %></td>
                    <td style="font-weight: 500;"><%= u.getFirstName() %> <%= u.getLastName() %></td>
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
                        <button type="submit" form="form_<%= u.getId() %>" class="btn btn-primary btn-sm">
                            Enregistrer
                        </button>
                    </td>
                </tr>
                <%
                    }
                } else {
                %>
                <tr>
                    <td colspan="5" class="empty">Aucun utilisateur trouvé dans la base de données.</td>
                </tr>
                <%
                    }
                %>
                </tbody>
            </table>
        </div>
    </div>
</main>

</body>
</html>