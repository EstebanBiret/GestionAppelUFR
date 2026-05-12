package ut1.appel.servlet;

import ut1.appel.enums.Role;
import ut1.appel.entity.User;
import ut1.appel.util.HibernateUtil;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/* Petite classe histoire d'initialiser la bd locale avec qqls utilisateurs */
@WebServlet("/test")
public class TestServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        resp.setContentType("text/plain;charset=UTF-8");
        PrintWriter out = resp.getWriter();

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            int deleted = session.createMutationQuery("DELETE FROM User").executeUpdate();
            tx.commit();
            out.println("Table vidée (" + deleted + " enregistrement(s) supprimé(s))\n");
        } catch (Exception e) {
            out.println("Erreur suppression : " + e.getMessage());
        }

        List<User> defaultUsers = List.of(
                makeUser("admin@ut-capitole.fr", "a", "Admin",    "Système",   Role.ADMIN),
                makeUser("scolarite@ut-capitole.fr", "b", "Sophie",   "Dupont",    Role.SCOLARITE),
                makeUser("prof.martin@ut-capitole.fr", "c", "Jean",     "Martin",    Role.ENSEIGNANT),
                makeUser("prof.durand@ut-capitole.fr","d", "Claire",   "Durand",    Role.ENSEIGNANT),
                makeUser("fi.alice@etud.ut-capitole.fr","e", "Alice",  "Bernard",   Role.ETUDIANT_FI),
                makeUser("fi.bob@etud.ut-capitole.fr",  "f", "Bob",    "Leroy",     Role.ETUDIANT_FI),
                makeUser("fa.marc@etud.ut-capitole.fr", "g", "Marc",   "Petit",     Role.ETUDIANT_FA),
                makeUser("fa.lea@etud.ut-capitole.fr",  "h", "Léa",    "Moreau",    Role.ETUDIANT_FA)
        );

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            defaultUsers.forEach(session::persist);
            tx.commit();
            out.println("Utilisateurs créés :");
        } catch (Exception e) {
            out.println("Erreur création : " + e.getMessage());
        }

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            var liste = session.createQuery("FROM User ORDER BY role", User.class).list();
            out.println("Total en BD : " + liste.size() + "\n");
            liste.forEach(u -> out.println(
                    String.format("  [%-10s] %-12s %-12s  %s",
                            u.getRole(),
                            u.getFirstName(),
                            u.getLastName(),
                            u.getEmail())
            ));
        } catch (Exception e) {
            out.println("Erreur lecture : " + e.getMessage());
        }
    }

    private User makeUser(String email, String password, String firstName, String lastName, Role role) {
        User u = new User();
        u.setEmail(email);
        u.setPassword(password);
        u.setFirstName(firstName);
        u.setLastName(lastName);
        u.setRole(role);
        return u;
    }
}