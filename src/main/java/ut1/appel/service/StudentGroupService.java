package ut1.appel.service;

import ut1.appel.entity.StudentClass;
import ut1.appel.entity.StudentGroup;
import ut1.appel.entity.Users;
import ut1.appel.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import java.util.List;

public class StudentGroupService {

    public List<StudentGroup> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<StudentGroup> groupes = session.createQuery(
                    "SELECT DISTINCT g FROM StudentGroup g LEFT JOIN FETCH g.studentClass ORDER BY g.studentClass.name, g.name",
                    StudentGroup.class).list();
            session.createQuery(
                    "SELECT DISTINCT g FROM StudentGroup g LEFT JOIN FETCH g.users",
                    StudentGroup.class).list();
            return groupes;
        }
    }

    public StudentGroup findById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "SELECT g FROM StudentGroup g LEFT JOIN FETCH g.studentClass LEFT JOIN FETCH g.users WHERE g.id = :id",
                    StudentGroup.class).setParameter("id", id).uniqueResult();
        }
    }

    public boolean nameExistsInClass(String name, Long classId, Long excludeGroupId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = excludeGroupId == null
                    ? "SELECT COUNT(g) FROM StudentGroup g WHERE LOWER(g.name) = LOWER(:name) AND g.studentClass.id = :classId"
                    : "SELECT COUNT(g) FROM StudentGroup g WHERE LOWER(g.name) = LOWER(:name) AND g.studentClass.id = :classId AND g.id != :excludeId";
            var q = session.createQuery(hql, Long.class)
                    .setParameter("name", name)
                    .setParameter("classId", classId);
            if (excludeGroupId != null) q.setParameter("excludeId", excludeGroupId);
            return q.uniqueResult() > 0;
        }
    }

    public void create(String name, Long classId, Long[] userIds) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            StudentClass sc = session.get(StudentClass.class, classId);
            StudentGroup g = new StudentGroup();
            g.setName(name);
            g.setStudentClass(sc);
            session.persist(g);
            if (userIds != null) assignUsers(session, g, userIds);
            tx.commit();
        }
    }

    public void update(Long id, String name, Long classId, Long[] userIds) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            StudentGroup g = session.get(StudentGroup.class, id);
            if (g == null) { tx.rollback(); return; }
            session.createQuery("FROM Users u WHERE u.studentGroup.id = :gid", Users.class)
                    .setParameter("gid", id).list()
                    .forEach(u -> u.setStudentGroup(null));
            StudentClass sc = session.get(StudentClass.class, classId);
            g.setName(name);
            g.setStudentClass(sc);
            if (userIds != null) assignUsers(session, g, userIds);
            tx.commit();
        }
    }

    public List<Users> findStudentsByClass(Long classId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "FROM Users u WHERE u.studentClass.id = :classId AND u.role IN ('ETUDIANT_FI', 'ETUDIANT_FA') ORDER BY u.lastName, u.firstName",
                    Users.class).setParameter("classId", classId).list();
        }
    }

    private void assignUsers(Session session, StudentGroup g, Long[] userIds) {
        for (Long uid : userIds) {
            Users u = session.get(Users.class, uid);
            if (u != null) u.setStudentGroup(g);
        }
    }
}