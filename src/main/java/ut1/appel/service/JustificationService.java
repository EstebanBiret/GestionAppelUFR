package ut1.appel.service;

import ut1.appel.entity.Justification;
import ut1.appel.entity.Users;
import ut1.appel.enums.JustificationStatus;
import ut1.appel.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.time.LocalDate;
import java.util.List;

public class JustificationService {

    public void save(Users student, String fileUrl, String comment) {
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                Transaction tx = session.beginTransaction();

                try {
                    Justification justification = new Justification();
                    justification.setUser(student);
                    justification.setFileUrl(fileUrl);
                    justification.setDepositDate(LocalDate.now());
                    justification.setComment(comment);
                    justification.setStatus(JustificationStatus.PENDING);

                    justification.setStartDate(null);
                    justification.setEndDate(null);

                    session.persist(justification);
                    tx.commit();
                } catch (Exception e) {
                    if (tx != null && tx.isActive()) {
                        tx.rollback();
                    }
                    throw e;
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("Erreur lors de l'enregistrement de la justification", e);
            }
    }

    public List<Justification> findByUser(Users user) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                            "FROM Justification j WHERE j.user.id = :userId ORDER BY j.depositDate DESC",
                            Justification.class)
                    .setParameter("userId", user.getId())
                    .list();
        }
    }
}