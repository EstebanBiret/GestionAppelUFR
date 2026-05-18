<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="ut1.appel.entity.*, ut1.appel.enums.Role, java.util.*" %>
<%
    Users me = (Users) session.getAttribute("currentUser");
    if (me == null) { response.sendRedirect(request.getContextPath() + "/auth/login"); return; }
    List<Users> pending  = (List<Users>) request.getAttribute("pending");
    List<Users> assigned = (List<Users>) request.getAttribute("assigned");
    Map<String, String> allRoles = (Map<String, String>) request.getAttribute("allRoles");
    if (pending  == null) pending  = new ArrayList<>();
    if (assigned == null) assigned = new ArrayList<>();
%>
<!DOCTYPE html>
<html lang="fr">
<head>
  <meta charset="UTF-8">
  <title>Administration — UT Capitole</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin.css">
</head>
<body>

<header>
  <div class="logo">
    <div class="logo-bar"></div>
    <div>
      <div class="logo-text">Université Toulouse Capitole</div>
      <div class="logo-sub">Administration</div>
    </div>
  </div>
  <div class="header-right">
    <span class="user-chip"><%= me.getFirstName() %> <%= me.getLastName() %></span>
    <a href="${pageContext.request.contextPath}/auth/logout" class="btn-logout">Se déconnecter</a>
  </div>
</header>

<main>
  <div class="page-header">
    <div>
      <h1>Gestion des utilisateurs</h1>
      <p style="color:var(--txt-muted);font-size:.875rem;margin-top:.25rem">
        Attribuez les rôles aux utilisateurs en attente et gérez les accès.
      </p>
    </div>
  </div>

  <%-- TABLE 1 : EN ATTENTE --%>
  <div class="section-label">
    En attente d'attribution
    <span class="count-chip" id="pendingCount"><%= pending.size() %></span>
  </div>
  <div class="card" style="margin-bottom:2rem">
    <div class="table-toolbar">
      <div class="search-wrap">
        <input type="text" class="search-bar" placeholder="Rechercher…"
               oninput="filterTable('pendingTbody', this.value)"
               id="searchPending">
      </div>
      <span class="table-count" id="pendingLabel"><%= pending.size() %> utilisateur(s)</span>
    </div>
    <div style="overflow-x:auto">
      <table class="table-container" id="pendingTable">
        <thead>
          <tr>
            <th onclick="sortTable('pendingTbody',0,this)" data-dir="asc">ID <span class="sort-icon">↕</span></th>
            <th onclick="sortTable('pendingTbody',1,this)" data-dir="asc">Utilisateur <span class="sort-icon">↕</span></th>
            <th>Rôle à attribuer</th>
            <th>Action</th>
          </tr>
        </thead>
        <tbody id="pendingTbody">
          <% if (pending.isEmpty()) { %>
            <tr id="pendingEmpty"><td colspan="4" class="empty-table">
              ✅ Aucun utilisateur en attente d'attribution de rôle.
            </td></tr>
          <% } else { for (Users u : pending) {
              String initials = u.getFirstName().substring(0,1).toUpperCase() + u.getLastName().substring(0,1).toUpperCase();
          %>
          <tr data-uid="<%= u.getId() %>" data-search="<%= u.getFirstName().toLowerCase() %> <%= u.getLastName().toLowerCase() %> <%= u.getEmail().toLowerCase() %>">
            <td style="color:var(--txt-muted);font-size:.8rem">#<%= u.getId() %></td>
            <td>
              <div class="user-cell">
                <div class="avatar">
                  <% if (u.getPicturePath() != null && !u.getPicturePath().isEmpty()) { %>
                    <img src="${pageContext.request.contextPath}/images/users/<%= u.getPicturePath() %>" alt="">
                  <% } else { %>
                    <img src="${pageContext.request.contextPath}/images/users/default.jpg" alt="">
                  <% } %>
                </div>
                <div>
                  <div class="user-cell-name"><%= u.getFirstName() %> <%= u.getLastName() %></div>
                  <div class="user-cell-email"><%= u.getEmail() %></div>
                </div>
              </div>
            </td>
            <td>
             <select class="role-select" data-uid="<%= u.getId() %>">
               <% for (Map.Entry<String, String> entry : allRoles.entrySet()) {
                   if (entry.getKey().equals("ADMIN")) continue; %>
                 <option value="<%= entry.getKey() %>"
                   <%= entry.getKey().equals("PENDING") ? "selected" : "" %>>
                   <%= entry.getValue() %>
                 </option>
               <% } %>
             </select>
            </td>
            <td>
              <button class="btn btn-primary btn-sm"
                      onclick="saveRole(<%= u.getId() %>, this)">Enregistrer</button>
            </td>
          </tr>
          <% } } %>
        </tbody>
      </table>
    </div>
  </div>

  <%-- TABLE 2 : UTILISATEURS AVEC RÔLE --%>
  <div class="section-label">
    Utilisateurs actifs
    <span class="count-chip" id="assignedCount"><%= assigned.size() %></span>
  </div>
  <div class="card">
    <div class="table-toolbar">
      <div class="search-wrap">
        <input type="text" class="search-bar" placeholder="Rechercher…"
               oninput="filterTable('assignedTbody', this.value)"
               id="searchAssigned">
      </div>
      <span class="table-count" id="assignedLabel"><%= assigned.size() %> utilisateur(s)</span>
    </div>
    <div style="overflow-x:auto">
      <table class="table-container" id="assignedTable">
        <thead>
          <tr>
            <th onclick="sortTable('assignedTbody',0,this)" data-dir="asc">ID <span class="sort-icon">↕</span></th>
            <th onclick="sortTable('assignedTbody',1,this)" data-dir="asc">Utilisateur <span class="sort-icon">↕</span></th>
            <th onclick="sortTable('assignedTbody',2,this)" data-dir="asc">Rôle actuel <span class="sort-icon">↕</span></th>
            <th>Modifier le rôle</th>
            <th>Action</th>
          </tr>
        </thead>
        <tbody id="assignedTbody">
          <% if (assigned.isEmpty()) { %>
            <tr id="assignedEmpty"><td colspan="5" class="empty-table">Aucun utilisateur avec rôle.</td></tr>
          <% } else { for (Users u : assigned) {
              String initials = u.getFirstName().substring(0,1).toUpperCase() + u.getLastName().substring(0,1).toUpperCase();
          %>
          <tr data-uid="<%= u.getId() %>" data-role="<%= u.getRole().name() %>"
              data-search="<%= u.getFirstName().toLowerCase() %> <%= u.getLastName().toLowerCase() %> <%= u.getEmail().toLowerCase() %> <%= u.getRole().name().toLowerCase() %>">
            <td style="color:var(--txt-muted);font-size:.8rem">#<%= u.getId() %></td>
            <td>
              <div class="user-cell">
                <div class="avatar">
                  <% if (u.getPicturePath() != null && !u.getPicturePath().isEmpty()) { %>
                    <img src="${pageContext.request.contextPath}/images/users/<%= u.getPicturePath() %>" alt="">
                  <% } else { %>
                    <img src="${pageContext.request.contextPath}/images/users/default.jpg" alt="">
                  <% } %>
                </div>
                <div>
                  <div class="user-cell-name"><%= u.getFirstName() %> <%= u.getLastName() %></div>
                  <div class="user-cell-email"><%= u.getEmail() %></div>
                </div>
              </div>
            </td>
            <td>
              <span class="role-badge role-<%= u.getRole().name() %>" id="badge_<%= u.getId() %>">
                <%= allRoles.get(u.getRole().name()) %>
              </span>
            </td>
            <td>
              <select class="role-select" data-uid="<%= u.getId() %>">
                <% for (Map.Entry<String, String> entry : allRoles.entrySet()) {
                    if (entry.getKey().equals("ADMIN")) continue; %>
                  <option value="<%= entry.getKey() %>"
                    <%= entry.getKey().equals(u.getRole().name()) ? "selected" : "" %>>
                    <%= entry.getValue() %>
                  </option>
                <% } %>
              </select>
            </td>
            <td>
              <button class="btn btn-primary btn-sm"
                      onclick="saveRole(<%= u.getId() %>, this)">Enregistrer</button>
            </td>
          </tr>
          <% } } %>
        </tbody>
      </table>
    </div>
  </div>
</main>

<div class="toast" id="toast"></div>

<script>
  window._contextPath = '<%= request.getContextPath() %>';
  window._roleLabels  = {
    <% for (Map.Entry<String,String> e : allRoles.entrySet()) { %>
      '<%= e.getKey() %>': '<%= e.getValue() %>',
    <% } %>
  };
</script>
<script src="${pageContext.request.contextPath}/js/admin.js"></script>

</body>
</html>