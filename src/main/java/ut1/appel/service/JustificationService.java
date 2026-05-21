package ut1.appel.service;

import ut1.appel.entity.*;
import ut1.appel.enums.AttendanceRowStatus;
import ut1.appel.enums.JustificationStatus;
import ut1.appel.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.time.LocalDateTime;
import java.util.List;

public class JustificationService {

    public void save(Users student, String fileUrl, String comment) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            try {
                Justification j = new Justification();
                j.setUser(student);
                j.setFileUrl(fileUrl);
                j.setDepositDate(LocalDateTime.now());
                j.setComment(comment);
                j.setStatus(JustificationStatus.EN_ATTENTE);
                j.setStartDate(null);
                j.setEndDate(null);
                session.persist(j);
                tx.commit();
            } catch (Exception e) {
                if (tx.isActive()) tx.rollback();
                throw e;
            }
        }
    }

    public List<Justification> findByUser(Users user) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                            "FROM Justification j LEFT JOIN FETCH j.user WHERE j.user.id = :uid ORDER BY j.depositDate DESC",
                            Justification.class)
                    .setParameter("uid", user.getId())
                    .list();
        }
    }

    public List<Justification> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                            "FROM Justification j LEFT JOIN FETCH j.user u ORDER BY j.status ASC, j.depositDate DESC",
                            Justification.class)
                    .list();
        }
    }

    public Justification findById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                            "FROM Justification j LEFT JOIN FETCH j.user WHERE j.id = :id",
                            Justification.class)
                    .setParameter("id", id)
                    .uniqueResult();
        }
    }

    public void process(Long justifId, JustificationStatus decision,
                        LocalDateTime startDate, LocalDateTime endDate,
                        String feedback) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            try {
                Justification j = session.get(Justification.class, justifId);
                j.setStatus(decision);
                j.setScholarshipFeedback(feedback);
                j.setProcessedDate(LocalDateTime.now());

                if (decision == JustificationStatus.APPROUVEE) {
                    j.setStartDate(startDate);
                    j.setEndDate(endDate);

                    session.createNativeMutationQuery(
                                    "UPDATE AttendanceRow ar " +
                                            "JOIN AttendanceSheet sh ON ar.attendance_sheet_id = sh.id " +
                                            "JOIN Session s ON sh.session_id = s.id " +
                                            "SET ar.status = 'ABJ', ar.justification_id = :justifId " +
                                            "WHERE ar.user_id = :uid " +
                                            "AND TIMESTAMP(s.sessionDate, s.startTime) <= :end " +
                                            "AND TIMESTAMP(s.sessionDate, s.endTime)   >= :start")
                            .setParameter("justifId", j.getId())
                            .setParameter("uid",      j.getUser().getId())
                            .setParameter("start",    startDate)
                            .setParameter("end",      endDate)
                            .executeUpdate();
                }
                tx.commit();
            } catch (Exception e) {
                if (tx.isActive()) tx.rollback();
                throw e;
            }
        }
    }
}