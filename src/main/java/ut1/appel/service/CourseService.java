package ut1.appel.service;

import ut1.appel.entity.Course;
import ut1.appel.entity.Users;
import ut1.appel.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import java.util.List;

public class CourseService {

    public List<Course> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "SELECT c FROM Course c LEFT JOIN FETCH c.studentClass LEFT JOIN FETCH c.responsable ORDER BY c.studentClass.name, c.name",
                    Course.class).list();
        }
    }

    public Course findById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "SELECT c FROM Course c LEFT JOIN FETCH c.studentClass LEFT JOIN FETCH c.responsable WHERE c.id = :id",
                    Course.class).setParameter("id", id).uniqueResult();
        }
    }

    public boolean existsForClass(String name, Long classId, Long excludeId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = excludeId == null
                    ? "SELECT COUNT(c) FROM Course c WHERE LOWER(c.name) = LOWER(:name) AND c.studentClass.id = :classId"
                    : "SELECT COUNT(c) FROM Course c WHERE LOWER(c.name) = LOWER(:name) AND c.studentClass.id = :classId AND c.id != :excludeId";
            var q = session.createQuery(hql, Long.class)
                    .setParameter("name", name)
                    .setParameter("classId", classId);
            if (excludeId != null) q.setParameter("excludeId", excludeId);
            return q.uniqueResult() > 0;
        }
    }

    public void create(String name, Long classId, Long responsableId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            Course c = new Course();
            c.setName(name);
            c.setStudentClass(session.get(ut1.appel.entity.StudentClass.class, classId));
            c.setResponsable(session.get(Users.class, responsableId));
            session.persist(c);
            tx.commit();
        }
    }

    public List<Users> findAllTeachers() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "FROM Users u WHERE u.role = 'ENSEIGNANT' ORDER BY u.lastName, u.firstName",
                    Users.class).list();
        }
    }
}