package ut1.appel.service;

import ut1.appel.entity.*;
import ut1.appel.enums.AttendanceRowStatus;
import ut1.appel.util.HibernateUtil;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CourseStatsService {

    public List<Map<String, Object>> getAbsencePerSession(Long courseId) {
        try (org.hibernate.Session hs = HibernateUtil.getSessionFactory().openSession()) {
            List<Object[]> rows = hs.createQuery(
                            "SELECT s.sessionDate, " +
                                    "SUM(CASE WHEN ar.status = :absent THEN 1 ELSE 0 END), " +
                                    "COUNT(ar) " +
                                    "FROM AttendanceRow ar " +
                                    "JOIN ar.attendanceSheet ash " +
                                    "JOIN ash.session s " +
                                    "WHERE s.course.id = :courseId " +
                                    "GROUP BY s.id, s.sessionDate " +
                                    "ORDER BY s.sessionDate ASC",
                            Object[].class)
                    .setParameter("courseId", courseId)
                    .setParameter("absent", AttendanceRowStatus.ABSENT)
                    .list();

            List<Map<String, Object>> result = new ArrayList<>();
            for (Object[] row : rows) {
                Map<String, Object> entry = new LinkedHashMap<>();
                entry.put("date", row[0].toString());
                entry.put("absent", ((Number) row[1]).longValue());
                entry.put("total", ((Number) row[2]).longValue());
                result.add(entry);
            }
            return result;
        }
    }

    public List<Map<String, Object>> getStudentsWithManyAbsences(Long courseId) {
        try (org.hibernate.Session hs = HibernateUtil.getSessionFactory().openSession()) {
            List<Object[]> rows = hs.createQuery(
                            "SELECT ar.user, COUNT(ar) FROM AttendanceRow ar " +
                                    "JOIN ar.attendanceSheet ash " +
                                    "JOIN ash.session s " +
                                    "WHERE s.course.id = :courseId " +
                                    "AND ar.status = :status " +
                                    "GROUP BY ar.user " +
                                    "HAVING COUNT(ar) >= 3 " +
                                    "ORDER BY COUNT(ar) DESC",
                            Object[].class)
                    .setParameter("courseId", courseId)
                    .setParameter("status", AttendanceRowStatus.ABSENT)
                    .list();

            List<Map<String, Object>> result = new ArrayList<>();
            for (Object[] row : rows) {
                Users user = (Users) row[0];
                Long count = (Long) row[1];

                Map<String, Object> entry = new LinkedHashMap<>();
                entry.put("firstName", user.getFirstName());
                entry.put("lastName", user.getLastName());
                entry.put("absences", count);
                result.add(entry);
            }
            return result;
        }
    }

    public double getAverageAbsences(Long courseId) {
        List<Map<String, Object>> perSession = getAbsencePerSession(courseId);
        if (perSession.isEmpty()) return 0;
        long total = perSession.stream()
                .mapToLong(e -> (long) e.get("absent"))
                .sum();
        return (double) total / perSession.size();
    }
}