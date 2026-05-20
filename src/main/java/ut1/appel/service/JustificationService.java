package ut1.appel.service;

import ut1.appel.entity.Justification;
import ut1.appel.entity.Users;
import ut1.appel.enums.JustificationStatus;
import ut1.appel.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.time.LocalDateTime;

public class JustificationService {

    public void save(Users student, String fileUrl, String comment) {
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                Transaction tx = session.beginTransaction();

                try {
                    Justification justification = new Justification();
                    justification.setUser(student);
                    justification.setFileUrl(fileUrl);
                    justification.setDepositDate(LocalDateTime.now());
                    justification.setComment(comment);
                    justification.setStatus(JustificationStatus.EN_ATTENTE);

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
}