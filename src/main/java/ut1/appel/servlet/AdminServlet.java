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

@WebServlet("/admin")
public class AdminServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        java.util.Map<String, String> allRoles = new java.util.LinkedHashMap<>();

        for (ut1.appel.enums.Role role : ut1.appel.enums.Role.values()) {

            String label = switch (role) {
                case PENDING -> "En attente";
                case ADMIN -> "Administrateur";
                case ETUDIANT_FI -> "Étudiant (Initiale)";
                case ETUDIANT_FA -> "Étudiant (Alternance)";
                case ENSEIGNANT -> "Enseignant";
                case SCOLARITE -> "Scolarité";
            };

            allRoles.put(role.name(), label);
        }

        req.setAttribute("allRoles", allRoles);

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<Users> usersList = session.createQuery("FROM Users", Users.class).list();
            req.setAttribute("users", usersList);
        } catch (Exception e) {
            e.printStackTrace();
        }

        req.getRequestDispatcher("/WEB-INF/views/home/admin.jsp").forward(req, resp);
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String userIdStr = req.getParameter("userId");
        String newRole = req.getParameter("role");

        if (userIdStr != null && newRole != null) {

            Transaction transaction = null;
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                transaction = session.beginTransaction();

                Long id = Long.parseLong(userIdStr);

                Users userToUpdate = session.get(Users.class, id);

                if (userToUpdate != null) {
                    userToUpdate.setRole(Role.valueOf(newRole));

                    session.merge(userToUpdate);
                }

                transaction.commit();

            } catch (Exception e) {
                if (transaction != null) {
                    transaction.rollback();
                }
                e.printStackTrace();
            }
        }

        resp.sendRedirect(req.getContextPath() + "/admin");
    }
}