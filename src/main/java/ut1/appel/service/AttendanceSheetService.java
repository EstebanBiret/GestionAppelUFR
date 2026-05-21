package ut1.appel.service;

import ut1.appel.entity.AttendanceSheet;
import ut1.appel.util.HibernateUtil;
import org.hibernate.Transaction;

public class AttendanceSheetService {

    public static AttendanceSheet getSheetBySessionId(Long sessionId) {
        try (org.hibernate.Session hibernateSession = HibernateUtil.getSessionFactory().openSession()) {
            return hibernateSession.createQuery(
                            "SELECT DISTINCT a FROM AttendanceSheet a " +
                                    "LEFT JOIN FETCH a.attendanceRows r " +
                                    "LEFT JOIN FETCH r.user " +
                                    "LEFT JOIN FETCH r.justification " +
                                    "WHERE a.session.id = :sessionId",
                            AttendanceSheet.class)
                    .setParameter("sessionId", sessionId)
                    .uniqueResult();
        }
    }

    public static void updateSheet(AttendanceSheet sheet) {
        Transaction transaction = null;
        try (org.hibernate.Session hibernateSession = HibernateUtil.getSessionFactory().openSession()) {
            transaction = hibernateSession.beginTransaction();

            hibernateSession.merge(sheet);

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }
}