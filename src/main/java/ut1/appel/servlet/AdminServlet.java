package ut1.appel.servlet;

import ut1.appel.entity.Users;
import ut1.appel.enums.Role;
import ut1.appel.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet("/admin/*")
public class AdminServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<Users> pending = session.createQuery(
                            "FROM Users u WHERE u.role = :pending ORDER BY u.lastName, u.firstName",
                            Users.class)
                    .setParameter("pending", Role.PENDING)
                    .list();
            List<Users> assigned = session.createQuery(
                            "FROM Users u WHERE u.role <> :pending AND u.role <> :admin ORDER BY u.role, u.lastName",
                            Users.class)
                    .setParameter("pending", Role.PENDING)
                    .setParameter("admin", Role.ADMIN)
                    .list();
            req.setAttribute("pending", pending);
            req.setAttribute("assigned", assigned);
            req.setAttribute("allRoles", getRoleMap());
        }

        req.getRequestDispatcher("/WEB-INF/views/home/admin.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        resp.setContentType("application/json;charset=UTF-8");
        String userIdStr = req.getParameter("userId");
        String newRole   = req.getParameter("role");

        if (userIdStr == null || newRole == null) {
            resp.getWriter().write("{\"success\":false,\"message\":\"Paramètres manquants\"}");
            return;
        }

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            Users u = session.get(Users.class, Long.parseLong(userIdStr));
            if (u == null) {
                resp.getWriter().write("{\"success\":false,\"message\":\"Utilisateur introuvable\"}");
                return;
            }
            u.setRole(Role.valueOf(newRole));
            tx.commit();

            resp.getWriter().write(String.format(
                    "{\"success\":true,\"userId\":%d,\"newRole\":\"%s\",\"newRoleLabel\":\"%s\",\"wasPending\":%b}",
                    u.getId(), u.getRole().name(), getRoleLabel(u.getRole()), newRole.equals("PENDING")
            ));
        } catch (Exception e) {
            resp.getWriter().write("{\"success\":false,\"message\":\"" + e.getMessage() + "\"}");
        }
    }

    private static Map<String, String> getRoleMap() {
        Map<String, String> map = new java.util.LinkedHashMap<>();
        for (Role r : Role.values()) map.put(r.name(), getRoleLabel(r));
        return map;
    }

    private static String getRoleLabel(Role r) {
        return switch (r) {
            case PENDING     -> "En attente";
            case ADMIN       -> "Administrateur";
            case ETUDIANT_FI -> "Étudiant (Initial)";
            case ETUDIANT_FA -> "Étudiant (Alternance)";
            case ENSEIGNANT  -> "Enseignant";
            case SCOLARITE   -> "Scolarité";
        };
    }
}