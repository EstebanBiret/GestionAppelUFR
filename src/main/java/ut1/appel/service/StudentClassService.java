package ut1.appel.service;

import ut1.appel.entity.StudentClass;
import ut1.appel.entity.Users;
import ut1.appel.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class StudentClassService {

    public List<StudentClass> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<StudentClass> classes = session.createQuery(
                    "SELECT DISTINCT c FROM StudentClass c LEFT JOIN FETCH c.groups",
                    StudentClass.class).list();
            session.createQuery(
                    "SELECT DISTINCT c FROM StudentClass c LEFT JOIN FETCH c.users",
                    StudentClass.class).list();
            return classes;
        }
    }

    public StudentClass findById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(StudentClass.class, id);
        }
    }

    public boolean nameExists(String name, Long excludeId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = excludeId == null
                    ? "SELECT COUNT(c) FROM StudentClass c WHERE LOWER(c.name) = LOWER(:name)"
                    : "SELECT COUNT(c) FROM StudentClass c WHERE LOWER(c.name) = LOWER(:name) AND c.id != :excludeId";
            var q = session.createQuery(hql, Long.class).setParameter("name", name);
            if (excludeId != null) q.setParameter("excludeId", excludeId);
            return q.uniqueResult() > 0;
        }
    }

    public void create(String name) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            StudentClass c = new StudentClass();
            c.setName(name);
            session.persist(c);
            tx.commit();
        }
    }

    public void update(Long id, String name) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            StudentClass c = session.get(StudentClass.class, id);
            if (c != null) c.setName(name);
            tx.commit();
        }
    }

    public List<Users> findAllStudents() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "FROM Users u WHERE u.role IN ('ETUDIANT_FI', 'ETUDIANT_FA') ORDER BY u.lastName, u.firstName",
                    Users.class).list();
        }
    }

    // Remplace l'ancienne méthode
    public void saveStudentAssignments(Long classId, String[] checkedUserIds) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();

            Set<Long> checkedSet = new HashSet<>();
            if (checkedUserIds != null) {
                for (String id : checkedUserIds) checkedSet.add(Long.parseLong(id));
            }

            // Tous les étudiants FI/FA
            List<Users> allStudents = session.createQuery(
                    "FROM Users u WHERE u.role IN ('ETUDIANT_FI', 'ETUDIANT_FA')",
                    Users.class).list();

            StudentClass sc = session.get(StudentClass.class, classId);

            for (Users u : allStudents) {
                if (checkedSet.contains(u.getId())) {
                    if (u.getStudentClass() == null || !u.getStudentClass().getId().equals(classId)) {
                        u.setStudentGroup(null);
                    }
                    u.setStudentClass(sc);
                } else {
                    if (u.getStudentClass() != null && u.getStudentClass().getId().equals(classId)) {
                        u.setStudentClass(null);
                        u.setStudentGroup(null);
                    }
                }
            }
            tx.commit();
        }
    }
}