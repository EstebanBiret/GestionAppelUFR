package ut1.appel.service;

import ut1.appel.entity.Session;
import ut1.appel.util.HibernateUtil;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class TeacherService {

    public static boolean isCurrentSession(Long teacherId) {
        try (org.hibernate.Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<Session> sessions = session.createQuery(
                            "FROM Session s WHERE s.teacher.id = :teacherId " +
                                    "AND s.sessionDate = :today " +
                                    "AND s.startTime <= :now AND s.endTime >= :now", Session.class)
                    .setParameter("teacherId", teacherId)
                    .setParameter("today", LocalDate.now())
                    .setParameter("now", LocalTime.now())
                    .list();
            return !sessions.isEmpty();
        }
    }

    public static Session getCurrentSession(Long teacherId) {
        try (org.hibernate.Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                            "SELECT DISTINCT s FROM Session s " +
                                    "LEFT JOIN FETCH s.studentClasses " +
                                    "LEFT JOIN FETCH s.studentGroups " +
                                    "LEFT JOIN FETCH s.course " +
                                    "WHERE s.teacher.id = :teacherId " +
                                    "AND s.sessionDate = :today " +
                                    "AND s.startTime <= :now AND s.endTime >= :now", ut1.appel.entity.Session.class)
                    .setParameter("teacherId", teacherId)
                    .setParameter("today", LocalDate.now())
                    .setParameter("now", LocalTime.now())
                    .setMaxResults(1)
                    .uniqueResult();
        }
    }

    public static List<ut1.appel.entity.Session> getUpcomingSessionsForTeacher(Long teacherId) {
        try (org.hibernate.Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                            "SELECT DISTINCT s FROM Session s " +
                                    "LEFT JOIN FETCH s.studentClasses " +
                                    "LEFT JOIN FETCH s.studentGroups " +
                                    "LEFT JOIN FETCH s.course " +
                                    "WHERE s.teacher.id = :teacherId " +
                                    "AND (s.sessionDate > :today OR (s.sessionDate = :today AND s.startTime > :now)) " +
                                    "ORDER BY s.sessionDate ASC, s.startTime ASC", ut1.appel.entity.Session.class)
                    .setParameter("teacherId", teacherId)
                    .setParameter("today", LocalDate.now())
                    .setParameter("now", LocalTime.now())
                    .setMaxResults(5)
                    .list();
        }
    }

    public static List<ut1.appel.entity.Session> getPastSessionsForTeacher(Long teacherId) {
        try (org.hibernate.Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                            "SELECT DISTINCT s FROM Session s " +
                                    "LEFT JOIN FETCH s.studentClasses " +
                                    "LEFT JOIN FETCH s.studentGroups " +
                                    "LEFT JOIN FETCH s.course " +
                                    "WHERE s.teacher.id = :teacherId " +
                                    "AND (s.sessionDate < :today OR (s.sessionDate = :today AND s.endTime < :now)) " +
                                    "ORDER BY s.sessionDate DESC, s.startTime DESC", ut1.appel.entity.Session.class)
                    .setParameter("teacherId", teacherId)
                    .setParameter("today", LocalDate.now())
                    .setParameter("now", LocalTime.now())
                    .setMaxResults(5)
                    .list();
        }
    }

    public static ut1.appel.entity.Session getSessionById(Long sessionId) {
        try (org.hibernate.Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                            "SELECT DISTINCT s FROM Session s " +
                                    "LEFT JOIN FETCH s.course " +
                                    "LEFT JOIN FETCH s.teacher " +
                                    "LEFT JOIN FETCH s.studentClasses " +
                                    "LEFT JOIN FETCH s.studentGroups " +
                                    "WHERE s.id = :sessionId",
                            ut1.appel.entity.Session.class)
                    .setParameter("sessionId", sessionId)
                    .uniqueResult();
        }
    }
}