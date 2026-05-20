package ut1.appel.service;

import ut1.appel.enums.Role;
import ut1.appel.entity.Users;
import ut1.appel.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.security.MessageDigest;
import java.util.HexFormat;

public class UserService {

    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            return HexFormat.of().formatHex(hash);
        } catch (Exception e) {
            throw new RuntimeException("Erreur hashage", e);
        }
    }

    public boolean emailExists(String email) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Long count = session.createQuery(
                            "SELECT COUNT(u) FROM Users u WHERE u.email = :email", Long.class)
                    .setParameter("email", email)
                    .uniqueResult();
            return count > 0;
        }
    }

    public Users findByEmail(String email) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                            "FROM Users u WHERE u.email = :email", Users.class)
                    .setParameter("email", email)
                    .uniqueResult();
        }
    }

    public Users register(String firstName, String lastName, String email, String password, String picturePath) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();

            Users u = new Users();
            u.setFirstName(firstName);
            u.setLastName(lastName);
            u.setEmail(email);
            u.setPassword(hashPassword(password));
            u.setRole(Role.EN_ATTENTE);
            u.setPicturePath(picturePath);

            session.persist(u);
            tx.commit();
            return u;
        }
    }
}