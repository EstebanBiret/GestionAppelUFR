package ut1.appel.service;

import ut1.appel.entity.*;
import ut1.appel.enums.AttendanceRowStatus;
import ut1.appel.enums.JustificationStatus;
import ut1.appel.util.HibernateUtil;
import org.hibernate.Transaction;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class SessionService {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");

    public List<Session> findByCourse(Long courseId) {
        try (org.hibernate.Session hs = HibernateUtil.getSessionFactory().openSession()) {
            List<Session> sessions = hs.createQuery(
                            "SELECT s FROM Session s " +
                                    "LEFT JOIN FETCH s.teacher " +
                                    "LEFT JOIN FETCH s.course c " +
                                    "LEFT JOIN FETCH c.studentClass " +
                                    "WHERE c.id = :courseId " +
                                    "ORDER BY s.sessionDate DESC, s.startTime",
                            Session.class)
                    .setParameter("courseId", courseId)
                    .list();
            for (Session s : sessions) {
                s.getStudentGroups().size();
                s.getStudentClasses().size();
            }
            return sessions;
        }
    }

    public void create(Long courseId, Long teacherId, LocalDate date,
                       LocalTime startTime, LocalTime endTime, Long groupId) {
        try (org.hibernate.Session hs = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = hs.beginTransaction();

            Course course = hs.get(Course.class, courseId);

            Session s = new Session();
            s.setCourse(course);
            s.setTeacher(hs.get(Users.class, teacherId));
            s.setSessionDate(date);
            s.setStartTime(startTime);
            s.setEndTime(endTime);

            LocalDateTime sessionStart = date.atTime(startTime);
            LocalDateTime sessionEnd   = date.atTime(endTime);

            List<Users> students;
            if (groupId != null) {
                Set<StudentGroup> groups = new HashSet<>();
                groups.add(hs.get(StudentGroup.class, groupId));
                s.setStudentGroups(groups);
                s.setStudentClasses(new HashSet<>());
                students = hs.createQuery(
                                "FROM Users u WHERE u.studentGroup.id = :gid", Users.class)
                        .setParameter("gid", groupId)
                        .list();
            } else {
                Set<StudentClass> classes = new HashSet<>();
                classes.add(course.getStudentClass());
                s.setStudentClasses(classes);
                s.setStudentGroups(new HashSet<>());
                students = hs.createQuery(
                                "FROM Users u WHERE u.studentClass.id = :cid", Users.class)
                        .setParameter("cid", course.getStudentClass().getId())
                        .list();
            }
            hs.persist(s);

            AttendanceSheet sheet = new AttendanceSheet();
            sheet.setSession(s);
            sheet.setAttendanceRows(new ArrayList<>());
            hs.persist(sheet);

            for (Users student : students) {

                List<Justification> justifs = hs.createQuery(
                                "FROM Justification j WHERE j.user.id = :uid " +
                                        "AND j.startDate <= :sessionEnd AND j.endDate >= :sessionStart " +
                                        "AND j.status IN (:statuses)",
                                Justification.class)
                        .setParameter("uid",          student.getId())
                        .setParameter("sessionStart", sessionStart)
                        .setParameter("sessionEnd",   sessionEnd)
                        .setParameterList("statuses",
                                List.of(JustificationStatus.APPROUVEE, JustificationStatus.REJETEE))
                        .list();

                Justification bestJustif = justifs.stream()
                        .filter(j -> j.getStatus() == JustificationStatus.APPROUVEE)
                        .findFirst()
                        .orElse(justifs.isEmpty() ? null : justifs.getFirst());

                AttendanceRow row = getAttendanceRow(student, bestJustif, sheet);
                hs.persist(row);
            }

            tx.commit();
        }
    }

    private static AttendanceRow getAttendanceRow(Users student, Justification bestJustif, AttendanceSheet sheet) {
        AttendanceRowStatus status;
        if (bestJustif == null) {
            status = AttendanceRowStatus.PRESENT;
        } else if (bestJustif.getStatus() == JustificationStatus.APPROUVEE) {
            status = AttendanceRowStatus.ABJ;
        } else {
            status = AttendanceRowStatus.ABSENT;
        }

        AttendanceRow row = new AttendanceRow();
        row.setAttendanceSheet(sheet);
        row.setUser(student);
        row.setStatus(status);
        row.setChangedGroup(false);
        row.setJustification(bestJustif);
        return row;
    }

    public String toJsonArray(List<Session> sessions) {
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < sessions.size(); i++) {
            Session s = sessions.get(i);
            if (i > 0) json.append(",");
            String group = !s.getStudentGroups().isEmpty()
                    ? s.getStudentGroups().iterator().next().getName()
                    : "Classe entière";
            String teacher = s.getTeacher() != null
                    ? s.getTeacher().getFirstName() + " " + s.getTeacher().getLastName()
                    : "—";
            json.append("{")
                    .append("\"id\":").append(s.getId()).append(",")
                    .append("\"date\":").append(esc(s.getSessionDate().format(DATE_FMT))).append(",")
                    .append("\"startTime\":").append(esc(s.getStartTime().format(TIME_FMT))).append(",")
                    .append("\"endTime\":").append(esc(s.getEndTime().format(TIME_FMT))).append(",")
                    .append("\"group\":").append(esc(group)).append(",")
                    .append("\"teacher\":").append(esc(teacher))
                    .append("}");
        }
        return json.append("]").toString();
    }

    private String esc(String s) {
        if (s == null) return "null";
        return "\"" + s.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
    }
}