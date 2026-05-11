package ut1.appel.servlet;

import ut1.appel.entity.User;
import ut1.appel.util.HibernateUtil;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/test")
public class TestServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        resp.setContentType("text/plain;charset=UTF-8");
        PrintWriter out = resp.getWriter();

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();

            User u = new User();
            u.setEmail("test@ut-capitole.fr");
            u.setFirstName("Esteban");
            u.setLastName("Test");
            session.persist(u);

            tx.commit();
            out.println("✅ INSERT OK - id=" + u.getId());
        } catch (Exception e) {
            out.println("❌ Erreur : " + e.getMessage());
        }

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            var liste = session.createQuery("FROM User", User.class).list();
            out.println("📋 Users in DB : " + liste.size());
            liste.forEach(u -> out.println("  → " + u.getFirstName() + " " + u.getLastName() + " (" + u.getEmail() + ")"));
        } catch (Exception e) {
            out.println("❌ Error reading : " + e.getMessage());
        }
    }
}