package ut1.appel.servlet;

import ut1.appel.entity.*;
import ut1.appel.enums.AttendanceRowStatus;
import ut1.appel.enums.JustificationStatus;
import ut1.appel.enums.Role;
import ut1.appel.service.UserService;
import ut1.appel.util.HibernateUtil;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@WebServlet("/test")
public class TestServlet extends HttpServlet {  

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        resp.setContentType("text/plain;charset=UTF-8");
        PrintWriter out = resp.getWriter();

        // ============================================================
        // STEP 1 — DROP ALL
        // ============================================================
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();

            session.createNativeMutationQuery("DELETE FROM session_groups").executeUpdate();
            session.createNativeMutationQuery("DELETE FROM session_classes").executeUpdate();
            session.createMutationQuery("DELETE FROM AttendanceRow").executeUpdate();
            session.createMutationQuery("DELETE FROM AttendanceSheet").executeUpdate();
            session.createMutationQuery("DELETE FROM Justification").executeUpdate();
            session.createMutationQuery("DELETE FROM Session").executeUpdate();
            session.createMutationQuery("DELETE FROM Course").executeUpdate();
            session.createMutationQuery("DELETE FROM Users").executeUpdate();
            session.createMutationQuery("DELETE FROM StudentGroup").executeUpdate();
            session.createMutationQuery("DELETE FROM StudentClass").executeUpdate();

            tx.commit();
            out.println("Toutes les tables vidées\n");
        } catch (Exception e) {
            out.println("Erreur suppression : " + e.getMessage() + "\n");
        }

        // ============================================================
        // STEP 2 — Classes
        // ============================================================
        StudentClass classMIAGE1 = new StudentClass();
        classMIAGE1.setName("M1 MIAGE");

        StudentClass classMIAGE2 = new StudentClass();
        classMIAGE2.setName("M2 MIAGE");

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.persist(classMIAGE1);
            session.persist(classMIAGE2);
            tx.commit();
            out.println("StudentClass créées : " + classMIAGE1.getName() + ", " + classMIAGE2.getName());
        } catch (Exception e) {
            out.println("Erreur StudentClass : " + e.getMessage());
        }

        // ============================================================
        // STEP 3 — Groupes
        // ============================================================
        StudentGroup groupTD1 = new StudentGroup();
        groupTD1.setName("TD1");
        groupTD1.setStudentClass(classMIAGE2);

        StudentGroup groupTD2 = new StudentGroup();
        groupTD2.setName("TD2");
        groupTD2.setStudentClass(classMIAGE2);

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.persist(groupTD1);
            session.persist(groupTD2);
            tx.commit();
            out.println("StudentGroup créés : " + groupTD1.getName() + ", " + groupTD2.getName());
        } catch (Exception e) {
            out.println("Erreur StudentGroup : " + e.getMessage());
        }

        // ============================================================
        // STEP 4 — Utilisateurs
        // ============================================================
        Users admin     = makeUser("admin@ut-capitole.fr",           "admin123",  "Admin",  "Système",  Role.ADMIN,        null, null);
        Users scolarite = makeUser("scolarite@ut-capitole.fr",       "scol123",   "Sophie", "Dupont",   Role.SCOLARITE,    null, null);
        Users profMartin= makeUser("prof.martin@ut-capitole.fr",     "prof123",   "Jean",   "Martin",   Role.ENSEIGNANT,   null, null);
        Users profDurand= makeUser("prof.durand@ut-capitole.fr",     "prof123",   "Claire", "Durand",   Role.ENSEIGNANT,   null, null);
        Users alice     = makeUser("fi.alice@etud.ut-capitole.fr",   "etud123",   "Alice",  "Bernard",  Role.ETUDIANT_FI,  classMIAGE2, groupTD1);
        Users bob       = makeUser("fi.bob@etud.ut-capitole.fr",     "etud123",   "Bob",    "Leroy",    Role.ETUDIANT_FI,  classMIAGE2, groupTD1);
        Users marc      = makeUser("fa.marc@etud.ut-capitole.fr",    "etud123",   "Marc",   "Petit",    Role.ETUDIANT_FA,  classMIAGE2, groupTD2);
        Users lea       = makeUser("fa.lea@etud.ut-capitole.fr",     "etud123",   "Léa",    "Moreau",   Role.ETUDIANT_FA,  classMIAGE2, groupTD2);

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            List.of(admin, scolarite, profMartin, profDurand, alice, bob, marc, lea)
                    .forEach(session::persist);
            tx.commit();
            out.println("Users créés : 8");
        } catch (Exception e) {
            out.println("Erreur Users : " + e.getMessage());
        }

        // ============================================================
        // STEP 5 — Cours
        // ============================================================
        Course coursDAI = new Course();
        coursDAI.setName("Développement Agile & Intégration");
        coursDAI.setResponsable(profMartin);
        coursDAI.setStudentClass(classMIAGE2);

        Course coursSOA = new Course();
        coursSOA.setName("Architecture SOA");
        coursSOA.setResponsable(profDurand);
        coursSOA.setStudentClass(classMIAGE2);

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.persist(coursDAI);
            session.persist(coursSOA);
            tx.commit();
            out.println("Cours créés : " + coursDAI.getName() + ", " + coursSOA.getName());
        } catch (Exception e) {
            out.println("Erreur Cours : " + e.getMessage());
        }

        // ============================================================
        // STEP 6 — Séances
        // ============================================================

        ut1.appel.entity.Session sessionDAI = new ut1.appel.entity.Session();
        sessionDAI.setCourse(coursDAI);
        sessionDAI.setSessionDate(LocalDate.now());
        sessionDAI.setStartTime(LocalTime.of(8, 0));
        sessionDAI.setEndTime(LocalTime.of(10, 0));
        sessionDAI.setCourse(coursDAI);
        sessionDAI.setTeacher(profMartin);
        sessionDAI.setStudentClasses(Set.of(classMIAGE2));
        sessionDAI.setStudentGroups(Set.of(groupTD1, groupTD2));

        ut1.appel.entity.Session sessionSOA = new ut1.appel.entity.Session();
        sessionSOA.setCourse(coursSOA);
        sessionSOA.setSessionDate(LocalDate.now().plusDays(1));
        sessionSOA.setStartTime(LocalTime.of(10, 0));
        sessionSOA.setEndTime(LocalTime.of(12, 0));
        sessionSOA.setCourse(coursSOA);
        sessionSOA.setTeacher(profDurand);
        sessionSOA.setStudentClasses(Set.of(classMIAGE2));
        sessionSOA.setStudentGroups(Set.of(groupTD2));

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.persist(sessionDAI);
            session.persist(sessionSOA);
            tx.commit();
            out.println("Sessions créées : " + sessionDAI.getCourse().getName() + ", " + sessionSOA.getCourse().getName());
        } catch (Exception e) {
            out.println("Erreur Sessions : " + e.getMessage());
        }

        // ============================================================
        // STEP 7 — Feuilles de présence
        // ============================================================
        AttendanceSheet sheetDAI = new AttendanceSheet();
        sheetDAI.setSession(sessionDAI);
        sheetDAI.setValidationDate(null);

        AttendanceSheet sheetSOA = new AttendanceSheet();
        sheetSOA.setSession(sessionSOA);
        sheetSOA.setValidationDate(LocalDate.now());

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.persist(sheetDAI);
            session.persist(sheetSOA);
            tx.commit();
            out.println("AttendanceSheets créées : 2");
        } catch (Exception e) {
            out.println("Erreur AttendanceSheet : " + e.getMessage());
        }

        // ============================================================
        // STEP 8 — Justifications d'abscence
        // ============================================================
        Justification justifMarc = new Justification();
        justifMarc.setUser(marc);
        justifMarc.setFileUrl("justifications/las_vegas.jpg");
        justifMarc.setDepositDate(LocalDateTime.now().minusDays(1));
        justifMarc.setStatus(JustificationStatus.APPROUVEE);
        justifMarc.setComment("pitié acceptez");
        justifMarc.setStartDate(LocalDateTime.of(2026, 5, 1, 0, 0));
        justifMarc.setEndDate(LocalDateTime.of(2026, 5, 25, 5, 59, 59));

        Justification justifAlice = new Justification();
        justifAlice.setUser(alice);
        justifAlice.setFileUrl("justifications/las_vegas.jpg");
        justifAlice.setDepositDate(LocalDateTime.now());
        justifAlice.setStatus(JustificationStatus.EN_ATTENTE);

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.persist(justifMarc);
            session.persist(justifAlice);
            tx.commit();
            out.println("Justifications créées : 2");
        } catch (Exception e) {
            out.println("Erreur Justification : " + e.getMessage());
        }

        // ============================================================
        // STEP 9 — Lignes feuilles de présence
        // ============================================================
        AttendanceRow rowAlice = makeRow(alice, sheetDAI, AttendanceRowStatus.ABSENT, false, justifAlice);
        AttendanceRow rowBob   = makeRow(bob,   sheetDAI, AttendanceRowStatus.PRESENT, false, null);
        AttendanceRow rowMarc  = makeRow(marc,  sheetSOA, AttendanceRowStatus.ABJ,    false, justifMarc);
        AttendanceRow rowLea   = makeRow(lea,   sheetSOA, AttendanceRowStatus.EN_RETARD, true, null);

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            List.of(rowAlice, rowBob, rowMarc, rowLea).forEach(session::persist);
            tx.commit();
            out.println("AttendanceRows créées : 4");
        } catch (Exception e) {
            out.println("Erreur AttendanceRow : " + e.getMessage());
        }

        // ============================================================
        // Récap complètement rocambolesque
        // ============================================================
        out.println("\n=== RÉCAPITULATIF BD ===");
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            out.println("Utilisateurs          : " + session.createQuery("SELECT COUNT(u) FROM Users u", Long.class).uniqueResult());
            out.println("Classes   : " + session.createQuery("SELECT COUNT(c) FROM StudentClass c", Long.class).uniqueResult());
            out.println("Groupes   : " + session.createQuery("SELECT COUNT(g) FROM StudentGroup g", Long.class).uniqueResult());
            out.println("Cours       : " + session.createQuery("SELECT COUNT(s) FROM Course s", Long.class).uniqueResult());
            out.println("Séances       : " + session.createQuery("SELECT COUNT(s) FROM Session s", Long.class).uniqueResult());
            out.println("Feuilles de présence: " + session.createQuery("SELECT COUNT(a) FROM AttendanceSheet a", Long.class).uniqueResult());
            out.println("Justificatifs d'absence : " + session.createQuery("SELECT COUNT(j) FROM Justification j", Long.class).uniqueResult());
            out.println("Lignes feuilles de présence : " + session.createQuery("SELECT COUNT(r) FROM AttendanceRow r", Long.class).uniqueResult());
        } catch (Exception e) {
            out.println("Erreur récap : " + e.getMessage());
        }
    }

    private Users makeUser(String email, String password, String firstName, String lastName,
                          Role role, StudentClass studentClass, StudentGroup studentGroup) {
        Users u = new Users();
        u.setEmail(email);
        u.setPassword(UserService.hashPassword(password));
        u.setFirstName(firstName);
        u.setLastName(lastName);
        u.setRole(role);
        u.setStudentClass(studentClass);
        u.setStudentGroup(studentGroup);
        u.setPicturePath("images/users/default.jpg");
        return u;
    }

    private AttendanceRow makeRow(Users user, AttendanceSheet sheet,
                                  AttendanceRowStatus status, boolean changedGroup,
                                  Justification justification) {
        AttendanceRow row = new AttendanceRow();
        row.setUser(user);
        row.setAttendanceSheet(sheet);
        row.setStatus(status);
        row.setChangedGroup(changedGroup);
        row.setJustification(justification);
        return row;
    }
}