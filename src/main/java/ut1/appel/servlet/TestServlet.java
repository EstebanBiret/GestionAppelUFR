package ut1.appel.servlet;

import ut1.appel.entity.*;
import ut1.appel.enums.*;
import ut1.appel.service.UserService;
import ut1.appel.util.HibernateUtil;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;
import org.hibernate.Transaction;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/test")
public class TestServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/plain;charset=UTF-8");
        PrintWriter out = resp.getWriter();

        // ============================================================
        // STEP 1 — DROP ALL
        // ============================================================
        clearDatabase(out);

        // ============================================================
        // STEP 2 — Student classes
        // ============================================================
        StudentClass classL1Droit = makeStudentClass("L1 Droit");
        StudentClass classL2Droit = makeStudentClass("L2 Droit");
        StudentClass classL3Droit = makeStudentClass("L3 Droit");
        StudentClass classL3MIASHS = makeStudentClass("L3 MIASHS");
        StudentClass classMIAGE1 = makeStudentClass("M1 MIAGE");
        StudentClass classMIAGE2 = makeStudentClass("M2 IPM");
        StudentClass classM1Informatique = makeStudentClass("M1 Informatique");
        StudentClass classM2Informatique = makeStudentClass("M2 Informatique");
        StudentClass classM1AES = makeStudentClass("M1 AES");
        StudentClass classM2AES = makeStudentClass("M2 AES");

        List<StudentClass> studentClasses = List.of(
                classL1Droit, classL2Droit, classL3Droit, classL3MIASHS, classMIAGE1,
                classMIAGE2, classM1Informatique, classM2Informatique, classM1AES, classM2AES
        );

        persistEntities(studentClasses, out,
                "StudentClass créées : " + studentClasses.size(),
                "Erreur StudentClass : "
        );

        // ============================================================
        // STEP 3 — Student groups
        // ============================================================
        StudentGroup groupL1DroitTD1 = makeStudentGroup("TD1", classL1Droit);
        StudentGroup groupL1DroitTD2 = makeStudentGroup("TD2", classL1Droit);
        StudentGroup groupL1DroitTD3 = makeStudentGroup("TD3", classL1Droit);

        StudentGroup groupL2DroitTD1 = makeStudentGroup("TD1", classL2Droit);
        StudentGroup groupL2DroitTD2 = makeStudentGroup("TD2", classL2Droit);
        StudentGroup groupL2DroitTD3 = makeStudentGroup("TD3", classL2Droit);

        StudentGroup groupL3DroitTD1 = makeStudentGroup("TD1", classL3Droit);
        StudentGroup groupL3DroitTD2 = makeStudentGroup("TD2", classL3Droit);

        StudentGroup groupL3MIASHSTD1 = makeStudentGroup("TD1", classL3MIASHS);
        StudentGroup groupL3MIASHSTD2 = makeStudentGroup("TD2", classL3MIASHS);

        StudentGroup groupMIAGE1TD1 = makeStudentGroup("TD1", classMIAGE1);
        StudentGroup groupMIAGE1TD2 = makeStudentGroup("TD2", classMIAGE1);

        StudentGroup groupMIAGE2TD1 = makeStudentGroup("TD1", classMIAGE2);
        StudentGroup groupMIAGE2TD2 = makeStudentGroup("TD2", classMIAGE2);

        StudentGroup groupM1InformatiqueTD1 = makeStudentGroup("TD1", classM1Informatique);
        StudentGroup groupM1InformatiqueTD2 = makeStudentGroup("TD2", classM1Informatique);

        StudentGroup groupM2InformatiqueTD1 = makeStudentGroup("TD1", classM2Informatique);

        StudentGroup groupM1AESTD1 = makeStudentGroup("TD1", classM1AES);
        StudentGroup groupM1AESTD2 = makeStudentGroup("TD2", classM1AES);

        List<StudentGroup> studentGroups = List.of(
                groupL1DroitTD1, groupL1DroitTD2, groupL1DroitTD3,
                groupL2DroitTD1, groupL2DroitTD2, groupL2DroitTD3,
                groupL3DroitTD1, groupL3DroitTD2,
                groupL3MIASHSTD1, groupL3MIASHSTD2,
                groupMIAGE1TD1, groupMIAGE1TD2,
                groupMIAGE2TD1, groupMIAGE2TD2,
                groupM1InformatiqueTD1, groupM1InformatiqueTD2,
                groupM2InformatiqueTD1,
                groupM1AESTD1, groupM1AESTD2
        );

        persistEntities(studentGroups, out,
                "StudentGroup créés : " + studentGroups.size(),
                "Erreur StudentGroup : "
        );

        // ============================================================
        // STEP 4 — Admin & scolarité
        // ============================================================
        Users admin = makeUser("admin@ut-capitole.fr", "admin123", "Admin", "Système", Role.ADMIN, null, null);
        Users scolarite = makeUser("scolarite@ut-capitole.fr", "scol123", "Sophie", "Dupont", Role.SCOLARITE, null, null);

        List<Users> administrativeUsers = List.of(admin, scolarite);

        persistEntities(administrativeUsers, out,
                "Utilisateurs administratifs créés : " + administrativeUsers.size(),
                "Erreur utilisateurs administratifs : "
        );

        // ============================================================
        // STEP 5 — Teachers
        // ============================================================
        Users profBerro = makeUser("prof.berro@ut-capitole.fr", "prof123", "Alain", "Berro", Role.ENSEIGNANT, null, null);
        Users profBour = makeUser("prof.bour@ut-capitole.fr", "prof123", "Raphaëlle", "Bour", Role.ENSEIGNANT, null, null);
        Users profBernard = makeUser("prof.bernard@ut-capitole.fr", "prof123", "David", "Bernard", Role.ENSEIGNANT, null, null);
        Users profRavat = makeUser("prof.ravat@ut-capitole.fr", "prof123", "Franck", "Ravat", Role.ENSEIGNANT, null, null);
        Users profAndonnof = makeUser("prof.andonnof@ut-capitole.fr", "prof123", "Eric", "Andonnof", Role.ENSEIGNANT, null, null);
        Users profNavard = makeUser("prof.navard@ut-capitole.fr", "prof123", "David", "Navard", Role.ENSEIGNANT, null, null);
        Users profParpex = makeUser("prof.parpex@ut-capitole.fr", "prof123", "Arold", "Parpex", Role.ENSEIGNANT, null, null);

        List<Users> teachers = List.of(
                profBerro, profBour, profBernard, profRavat, profAndonnof, profNavard, profParpex
        );

        persistEntities(teachers, out,
                "Enseignants créés : " + teachers.size(),
                "Erreur enseignants : "
        );

        // ============================================================
        // STEP 6 — Students
        // ============================================================
        List<Users> students = new ArrayList<>();

        Users alice = makeUser("fi.alice@etud.ut-capitole.fr", "etud123", "Alice", "Bernard", Role.ETUDIANT_FI, classMIAGE2, groupMIAGE2TD1);
        Users bob = makeUser("fi.bob@etud.ut-capitole.fr", "etud123", "Bob", "Leroy", Role.ETUDIANT_FI, classMIAGE2, groupMIAGE2TD1);
        Users marc = makeUser("fa.marc@etud.ut-capitole.fr", "etud123", "Marc", "Petit", Role.ETUDIANT_FA, classMIAGE2, groupMIAGE2TD2);
        Users lea = makeUser("fa.lea@etud.ut-capitole.fr", "etud123", "Léa", "Moreau", Role.ETUDIANT_FA, classMIAGE2, groupMIAGE2TD2);

        students.addAll(List.of(alice, bob, marc, lea));

        addGeneratedStudents(students, "l1droit", 18, Role.ETUDIANT_FI, classL1Droit, new StudentGroup[]{groupL1DroitTD1, groupL1DroitTD2, groupL1DroitTD3}, 0);
        addGeneratedStudents(students, "l2droit", 18, Role.ETUDIANT_FI, classL2Droit, new StudentGroup[]{groupL2DroitTD1, groupL2DroitTD2, groupL2DroitTD3}, 18);
        addGeneratedStudents(students, "l3droit", 16, Role.ETUDIANT_FI, classL3Droit, new StudentGroup[]{groupL3DroitTD1, groupL3DroitTD2}, 36);
        addGeneratedStudents(students, "l3miashs", 16, Role.ETUDIANT_FI, classL3MIASHS, new StudentGroup[]{groupL3MIASHSTD1, groupL3MIASHSTD2}, 52);
        addGeneratedStudents(students, "m1miage", 18, Role.ETUDIANT_FI, classMIAGE1, new StudentGroup[]{groupMIAGE1TD1, groupMIAGE1TD2}, 68);
        addGeneratedStudents(students, "m2miage", 14, Role.ETUDIANT_FA, classMIAGE2, new StudentGroup[]{groupMIAGE2TD1, groupMIAGE2TD2}, 86);
        addGeneratedStudents(students, "m1info", 17, Role.ETUDIANT_FI, classM1Informatique, new StudentGroup[]{groupM1InformatiqueTD1, groupM1InformatiqueTD2}, 100);
        addGeneratedStudents(students, "m2info", 15, Role.ETUDIANT_FA, classM2Informatique, new StudentGroup[]{groupM2InformatiqueTD1}, 117);
        addGeneratedStudents(students, "m1aes", 17, Role.ETUDIANT_FI, classM1AES, new StudentGroup[]{groupM1AESTD1, groupM1AESTD2}, 132);
        addGeneratedStudents(students, "m2aes", 15, Role.ETUDIANT_FA, classM2AES, new StudentGroup[]{}, 149);
        addGeneratedStudents(students, "sansclasse", 10, Role.ETUDIANT_FI, null, new StudentGroup[]{}, 164);

        persistEntities(students, out,
                "Étudiants créés : " + students.size(),
                "Erreur étudiants : "
        );

        // ============================================================
        // STEP 7 — Pending users
        // ============================================================
        UserService userService = new UserService();

        try {
            List<Users> pendingUsers = List.of(
                    userService.register("Mathilde", "Caron", "pending.01@etud.ut-capitole.fr", "etud123", "images/users/default.jpg"),
                    userService.register("Yanis", "Meunier", "pending.02@etud.ut-capitole.fr", "etud123", "images/users/default.jpg"),
                    userService.register("Elisa", "Renaud", "pending.03@etud.ut-capitole.fr", "etud123", "images/users/default.jpg"),
                    userService.register("Nolan", "Perrin", "pending.04@etud.ut-capitole.fr", "etud123", "images/users/default.jpg"),
                    userService.register("Maëlys", "Colin", "pending.05@etud.ut-capitole.fr", "etud123", "images/users/default.jpg"),
                    userService.register("Ilyes", "Marchand", "pending.06@etud.ut-capitole.fr", "etud123", "images/users/default.jpg"),
                    userService.register("Clémence", "Aubert", "pending.07@etud.ut-capitole.fr", "etud123", "images/users/default.jpg"),
                    userService.register("Baptiste", "Renard", "pending.08@etud.ut-capitole.fr", "etud123", "images/users/default.jpg"),
                    userService.register("Nora", "Benoit", "pending.09@etud.ut-capitole.fr", "etud123", "images/users/default.jpg"),
                    userService.register("Romain", "Barbier", "pending.10@etud.ut-capitole.fr", "etud123", "images/users/default.jpg")
            );

            out.println("Utilisateurs en attente créés : " + pendingUsers.size());
        } catch (Exception e) {
            out.println("Erreur utilisateurs en attente : " + e.getMessage());
        }

        // ============================================================
        // STEP 8 — Courses
        // ============================================================
        Course coursIntroDroitPrive = makeCourse("Introduction au droit privé", profBerro, classL1Droit);
        Course coursInstitutionsJudiciaires = makeCourse("Institutions judiciaires", profBour, classL1Droit);

        Course coursDroitObligations = makeCourse("Droit des obligations", profBernard, classL2Droit);
        Course coursDroitAdministratif = makeCourse("Droit administratif", profRavat, classL2Droit);

        Course coursDroitSocietes = makeCourse("Droit des sociétés", profAndonnof, classL3Droit);
        Course coursProcedureCivile = makeCourse("Procédure civile", profNavard, classL3Droit);

        Course coursStatsAppliquees = makeCourse("Statistiques appliquées", profParpex, classL3MIASHS);
        Course coursBasesDonnees = makeCourse("Bases de données", profBernard, classL3MIASHS);

        Course coursProcessusMetier = makeCourse("Ingénierie des processus métier", profBour, classMIAGE1);
        Course coursDevWebAvance = makeCourse("Développement web avancé", profRavat, classMIAGE1);

        Course coursGestionProjetAgile = makeCourse("Gestion de projet agile", profBour, classMIAGE2);
        Course coursArchitectureSI = makeCourse("Architecture des systèmes d'information", profBernard, classMIAGE2);

        Course coursAlgorithmiqueAvancee = makeCourse("Algorithmique avancée", profParpex, classM1Informatique);
        Course coursGenieLogiciel = makeCourse("Génie logiciel", profRavat, classM1Informatique);

        Course coursArchitectureLogicielle = makeCourse("Architecture logicielle", profBernard, classM2Informatique);
        Course coursSecuriteSystemes = makeCourse("Sécurité des systèmes", profNavard, classM2Informatique);

        Course coursEconomieOrganisations = makeCourse("Économie des organisations", profAndonnof, classM1AES);
        Course coursDroitTravail = makeCourse("Droit du travail", profBerro, classM1AES);

        Course coursManagementPublic = makeCourse("Management public", profBour, classM2AES);
        Course coursControleGestion = makeCourse("Contrôle de gestion", profParpex, classM2AES);

        List<Course> courses = List.of(
                coursIntroDroitPrive, coursInstitutionsJudiciaires,
                coursDroitObligations, coursDroitAdministratif,
                coursDroitSocietes, coursProcedureCivile,
                coursStatsAppliquees, coursBasesDonnees,
                coursProcessusMetier, coursDevWebAvance,
                coursGestionProjetAgile, coursArchitectureSI,
                coursAlgorithmiqueAvancee, coursGenieLogiciel,
                coursArchitectureLogicielle, coursSecuriteSystemes,
                coursEconomieOrganisations, coursDroitTravail,
                coursManagementPublic, coursControleGestion
        );

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();

            courses.forEach(session::persist);

            tx.commit();
            out.println("Cours créés : " + courses.size());
        } catch (Exception e) {
            out.println("Erreur Cours : " + e.getMessage());
        }
        // ============================================================
        // STEP 9 — Absence justifications
        // SessionService to automatically set ABJ / ABSENT rows.
        // ============================================================
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();

            int createdJustifications = 0;

            createdJustifications += addJustification(
                    session,
                    alice,
                    "justifications/certificat_medical_alice.pdf",
                    "Certificat médical pour absence ponctuelle.",
                    JustificationStatus.APPROUVEE,
                    2026, 5, 22, 8,
                    2026, 5, 22, 18
            );

            createdJustifications += addJustification(
                    session,
                    bob,
                    "justifications/convocation_bob.pdf",
                    "Convocation administrative.",
                    JustificationStatus.APPROUVEE,
                    2026, 5, 5, 7,
                    2026, 5, 5, 12
            );

            createdJustifications += addJustification(
                    session,
                    marc,
                    "justifications/arret_travail_marc.pdf",
                    "Arrêt de travail transmis dans les délais.",
                    JustificationStatus.APPROUVEE,
                    2026, 5, 1, 0,
                    2026, 5, 25, 23
            );

            createdJustifications += addJustification(
                    session,
                    lea,
                    "justifications/transport_lea.pdf",
                    "Retard important lié à un problème de transport.",
                    JustificationStatus.EN_ATTENTE,
                    2026, 6, 8, 7,
                    2026, 6, 8, 12
            );

            createdJustifications += addJustificationByEmail(
                    session,
                    "m1miage.03@etud.ut-capitole.fr",
                    "justifications/certificat_medical_m1miage03.pdf",
                    "Certificat médical pour absence à une séance de M1 MIAGE.",
                    JustificationStatus.APPROUVEE,
                    2026, 4, 30, 7,
                    2026, 4, 30, 13
            );

            createdJustifications += addJustificationByEmail(
                    session,
                    "m1miage.07@etud.ut-capitole.fr",
                    "justifications/justificatif_familial_m1miage07.pdf",
                    "Raison familiale signalée après la séance.",
                    JustificationStatus.REJETEE,
                    2026, 6, 4, 7,
                    2026, 6, 4, 13
            );

            createdJustifications += addJustificationByEmail(
                    session,
                    "m2miage.04@etud.ut-capitole.fr",
                    "justifications/certificat_medical_m2miage04.pdf",
                    "Absence justifiée pour raison médicale.",
                    JustificationStatus.APPROUVEE,
                    2026, 5, 22, 12,
                    2026, 5, 22, 18
            );

            createdJustifications += addJustificationByEmail(
                    session,
                    "m2miage.09@etud.ut-capitole.fr",
                    "justifications/document_incomplet_m2miage09.pdf",
                    "Document transmis incomplet.",
                    JustificationStatus.REJETEE,
                    2026, 6, 8, 7,
                    2026, 6, 8, 13
            );

            createdJustifications += addJustificationByEmail(
                    session,
                    "l1droit.05@etud.ut-capitole.fr",
                    "justifications/convocation_l1droit05.pdf",
                    "Convocation officielle acceptée.",
                    JustificationStatus.APPROUVEE,
                    2026, 4, 20, 7,
                    2026, 4, 20, 11
            );

            createdJustifications += addJustificationByEmail(
                    session,
                    "l2droit.11@etud.ut-capitole.fr",
                    "justifications/motif_personnel_l2droit11.pdf",
                    "Motif personnel non recevable.",
                    JustificationStatus.REJETEE,
                    2026, 5, 27, 12,
                    2026, 5, 27, 16
            );

            createdJustifications += addJustificationByEmail(
                    session,
                    "l3miashs.02@etud.ut-capitole.fr",
                    "justifications/certificat_l3miashs02.pdf",
                    "Justificatif médical en attente de validation.",
                    JustificationStatus.EN_ATTENTE,
                    2026, 6, 2, 12,
                    2026, 6, 2, 16
            );

            createdJustifications += addJustificationByEmail(
                    session,
                    "m1info.06@etud.ut-capitole.fr",
                    "justifications/rdv_administratif_m1info06.pdf",
                    "Rendez-vous administratif accepté.",
                    JustificationStatus.APPROUVEE,
                    2026, 5, 7, 12,
                    2026, 5, 7, 16
            );

            createdJustifications += addJustificationByEmail(
                    session,
                    "m2info.03@etud.ut-capitole.fr",
                    "justifications/arret_m2info03.pdf",
                    "Arrêt transmis pour plusieurs journées.",
                    JustificationStatus.APPROUVEE,
                    2026, 5, 11, 0,
                    2026, 5, 12, 23
            );

            createdJustifications += addJustificationByEmail(
                    session,
                    "m1aes.08@etud.ut-capitole.fr",
                    "justifications/absence_m1aes08.pdf",
                    "Justificatif refusé par la scolarité.",
                    JustificationStatus.REJETEE,
                    2026, 6, 16, 7,
                    2026, 6, 16, 11
            );

            createdJustifications += addJustificationByEmail(
                    session,
                    "m2aes.04@etud.ut-capitole.fr",
                    "justifications/convocation_m2aes04.pdf",
                    "Convocation acceptée.",
                    JustificationStatus.APPROUVEE,
                    2026, 6, 18, 12,
                    2026, 6, 18, 16
            );

            tx.commit();
            out.println("Justificatifs d'absence créés : " + createdJustifications);
        } catch (Exception e) {
            out.println("Erreur Justification : " + e.getMessage());
        }
        // ============================================================
        // STEP 10 — Sessions with automatic attendance sheets
        // ============================================================
        int createdSessions = 0;
        ut1.appel.service.SessionService sessionService = new ut1.appel.service.SessionService();

        try {
            // 20 sessions before 22/05/2026
            createdSessions += createSession(sessionService, coursIntroDroitPrive, profBerro, 2026, 4, 20, 8, 10, null);
            createdSessions += createSession(sessionService, coursInstitutionsJudiciaires, profBour, 2026, 4, 21, 10, 12, groupL1DroitTD1);
            createdSessions += createSession(sessionService, coursDroitObligations, profBernard, 2026, 4, 22, 13, 15, null);
            createdSessions += createSession(sessionService, coursDroitAdministratif, profRavat, 2026, 4, 23, 15, 17, groupL2DroitTD1);
            createdSessions += createSession(sessionService, coursDroitSocietes, profAndonnof, 2026, 4, 24, 8, 10, null);
            createdSessions += createSession(sessionService, coursProcedureCivile, profNavard, 2026, 4, 27, 10, 12, groupL3DroitTD1);
            createdSessions += createSession(sessionService, coursStatsAppliquees, profParpex, 2026, 4, 28, 13, 15, groupL3MIASHSTD1);
            createdSessions += createSession(sessionService, coursBasesDonnees, profBernard, 2026, 4, 29, 15, 17, null);
            createdSessions += createSession(sessionService, coursProcessusMetier, profBour, 2026, 4, 30, 8, 12, null);
            createdSessions += createSession(sessionService, coursDevWebAvance, profRavat, 2026, 5, 4, 13, 17, groupMIAGE1TD1);
            createdSessions += createSession(sessionService, coursGestionProjetAgile, profBour, 2026, 5, 5, 8, 10, groupMIAGE2TD1);
            createdSessions += createSession(sessionService, coursArchitectureSI, profBernard, 2026, 5, 6, 10, 12, null);
            createdSessions += createSession(sessionService, coursAlgorithmiqueAvancee, profParpex, 2026, 5, 7, 13, 15, groupM1InformatiqueTD1);
            createdSessions += createSession(sessionService, coursGenieLogiciel, profRavat, 2026, 5, 8, 15, 17, null);
            createdSessions += createSession(sessionService, coursArchitectureLogicielle, profBernard, 2026, 5, 11, 8, 12, groupM2InformatiqueTD1);
            createdSessions += createSession(sessionService, coursSecuriteSystemes, profNavard, 2026, 5, 12, 13, 17, null);
            createdSessions += createSession(sessionService, coursEconomieOrganisations, profBour, 2026, 5, 13, 8, 10, groupM1AESTD1);
            createdSessions += createSession(sessionService, coursDroitTravail, profBerro, 2026, 5, 18, 10, 12, null);
            createdSessions += createSession(sessionService, coursManagementPublic, profBour, 2026, 5, 19, 13, 15, null);
            createdSessions += createSession(sessionService, coursControleGestion, profParpex, 2026, 5, 21, 15, 17, null);

            // Required session: whole M2 MIAGE class
            createdSessions += createSession(sessionService, coursGestionProjetAgile, profBour, 2026, 5, 22, 13, 17, null);

            // 20 sessions after 22/05/2026
            createdSessions += createSession(sessionService, coursIntroDroitPrive, profBerro, 2026, 5, 25, 8, 10, groupL1DroitTD2);
            createdSessions += createSession(sessionService, coursInstitutionsJudiciaires, profBour, 2026, 5, 26, 10, 12, null);
            createdSessions += createSession(sessionService, coursDroitObligations, profBernard, 2026, 5, 27, 13, 15, groupL2DroitTD3);
            createdSessions += createSession(sessionService, coursDroitAdministratif, profRavat, 2026, 5, 28, 15, 17, null);
            createdSessions += createSession(sessionService, coursDroitSocietes, profAndonnof, 2026, 5, 29, 8, 10, groupL3DroitTD2);
            createdSessions += createSession(sessionService, coursProcedureCivile, profNavard, 2026, 6, 1, 10, 12, null);
            createdSessions += createSession(sessionService, coursStatsAppliquees, profParpex, 2026, 6, 2, 13, 15, groupL3MIASHSTD2);
            createdSessions += createSession(sessionService, coursBasesDonnees, profBernard, 2026, 6, 3, 15, 17, null);
            createdSessions += createSession(sessionService, coursProcessusMetier, profBour, 2026, 6, 4, 8, 12, groupMIAGE1TD2);
            createdSessions += createSession(sessionService, coursDevWebAvance, profRavat, 2026, 6, 5, 13, 17, null);
            createdSessions += createSession(sessionService, coursGestionProjetAgile, profBour, 2026, 6, 8, 8, 12, groupMIAGE2TD2);
            createdSessions += createSession(sessionService, coursArchitectureSI, profBernard, 2026, 6, 9, 13, 17, null);
            createdSessions += createSession(sessionService, coursAlgorithmiqueAvancee, profParpex, 2026, 6, 10, 8, 10, groupM1InformatiqueTD2);
            createdSessions += createSession(sessionService, coursGenieLogiciel, profRavat, 2026, 6, 11, 10, 12, null);
            createdSessions += createSession(sessionService, coursArchitectureLogicielle, profBernard, 2026, 6, 12, 13, 15, groupM2InformatiqueTD1);
            createdSessions += createSession(sessionService, coursSecuriteSystemes, profNavard, 2026, 6, 15, 15, 17, null);
            createdSessions += createSession(sessionService, coursEconomieOrganisations, profBour, 2026, 6, 16, 8, 10, groupM1AESTD2);
            createdSessions += createSession(sessionService, coursDroitTravail, profBerro, 2026, 6, 17, 10, 12, null);
            createdSessions += createSession(sessionService, coursManagementPublic, profBour, 2026, 6, 18, 13, 15, null);
            createdSessions += createSession(sessionService, coursControleGestion, profParpex, 2026, 6, 19, 15, 17, null);

            out.println("Séances créées avec feuilles d'appel : " + createdSessions);
        } catch (Exception e) {
            out.println("Erreur Séances après " + createdSessions + " création(s) : " + e.getMessage());
        }

        // ============================================================
        // STEP 11 — Absences répétées pour le bilan (Andonnof)
        // ============================================================
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();

            Users cible = session.createQuery(
                            "FROM Users u WHERE u.email = :email", Users.class)
                    .setParameter("email", "m1aes.01@etud.ut-capitole.fr")
                    .uniqueResult();

            Course cours = session.get(Course.class, coursEconomieOrganisations.getId());
            Users prof   = session.get(Users.class, profBour.getId());

            if (cible == null || cours == null || prof == null) {
                out.println("STEP 11 — entité introuvable, annulé.");
                tx.rollback();
            } else {
                java.time.LocalDate[] dates = {
                        java.time.LocalDate.of(2026, 3, 10),
                        java.time.LocalDate.of(2026, 3, 17),
                        java.time.LocalDate.of(2026, 3, 24)
                };

                for (java.time.LocalDate date : dates) {
                    ut1.appel.entity.Session s = new ut1.appel.entity.Session();
                    s.setCourse(cours);
                    s.setTeacher(prof);
                    s.setSessionDate(date);
                    s.setStartTime(java.time.LocalTime.of(8, 0));
                    s.setEndTime(java.time.LocalTime.of(10, 0));
                    s.setStudentClasses(new java.util.HashSet<>(List.of(session.get(StudentClass.class, classM1AES.getId()))));
                    s.setStudentGroups(new java.util.HashSet<>());
                    session.persist(s);

                    AttendanceSheet sheet = new AttendanceSheet();
                    sheet.setSession(s);
                    sheet.setIsSigned(true);
                    sheet.setLastModificationDate(java.time.LocalDateTime.now().minusDays(1));
                    sheet.setAttendanceRows(new java.util.ArrayList<>());
                    session.persist(sheet);

                    List<Users> etudiants = session.createQuery(
                                    "FROM Users u WHERE u.studentClass.id = :cid", Users.class)
                            .setParameter("cid", classM1AES.getId())
                            .list();

                    for (Users etudiant : etudiants) {
                        AttendanceRow row = new AttendanceRow();
                        row.setAttendanceSheet(sheet);
                        row.setUser(etudiant);
                        row.setChangedGroup(false);
                        row.setJustification(null);
                        row.setStatus(etudiant.getId().equals(cible.getId())
                                ? ut1.appel.enums.AttendanceRowStatus.ABSENT
                                : ut1.appel.enums.AttendanceRowStatus.PRESENT);
                        session.persist(row);
                    }
                }

                tx.commit();
                out.println("STEP 11 — 3 séances créées, " + cible.getFirstName()
                        + " " + cible.getLastName() + " ABSENT sur les 3, fiches signées.");
            }
        } catch (Exception e) {
            out.println("Erreur STEP 11 : " + e.getMessage());
        }

        // ============================================================
        // Récapitulatif
        // ============================================================
        printSummary(out);
    }

    private void clearDatabase(PrintWriter out) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();

            try {
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
                if (tx.isActive()) {
                    tx.rollback();
                }
                out.println("Erreur suppression : " + e.getMessage() + "\n");
            }
        }
    }

    private void persistEntities(List<?> entities, PrintWriter out, String successMessage, String errorPrefix) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();

            try {
                entities.forEach(session::persist);
                tx.commit();
                out.println(successMessage);
            } catch (Exception e) {
                if (tx.isActive()) {
                    tx.rollback();
                }
                out.println(errorPrefix + e.getMessage());
            }
        }
    }

    private StudentClass makeStudentClass(String name) {
        StudentClass studentClass = new StudentClass();
        studentClass.setName(name);
        return studentClass;
    }

    private StudentGroup makeStudentGroup(String name, StudentClass studentClass) {
        StudentGroup studentGroup = new StudentGroup();
        studentGroup.setName(name);
        studentGroup.setStudentClass(studentClass);
        return studentGroup;
    }

    private Users makeUser(String email, String password, String firstName, String lastName,
                           Role role, StudentClass studentClass, StudentGroup studentGroup) {
        Users user = new Users();
        user.setEmail(email);
        user.setPassword(UserService.hashPassword(password));
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setRole(role);
        user.setStudentClass(studentClass);
        user.setStudentGroup(studentGroup);
        user.setPicturePath("images/users/default.jpg");
        return user;
    }

    private void addGeneratedStudents(List<Users> students, String emailPrefix, int count, Role role,
                                      StudentClass studentClass, StudentGroup[] groups, int offset) {
        String[] firstNames = {
                "Lucas", "Emma", "Hugo", "Chloé", "Nathan", "Inès", "Louis", "Camille",
                "Gabriel", "Manon", "Arthur", "Sarah", "Jules", "Lina", "Noah", "Léna",
                "Tom", "Jade", "Ethan", "Louise", "Adam", "Zoé", "Maël", "Clara",
                "Raphaël", "Eva", "Sacha", "Nina", "Mathis", "Ambre", "Paul", "Romane",
                "Enzo", "Julie", "Antoine", "Mila", "Théo", "Anna", "Maxime", "Lola"
        };

        String[] lastNames = {
                "Martin", "Bernard", "Thomas", "Petit", "Robert", "Richard", "Durand", "Dubois",
                "Moreau", "Laurent", "Simon", "Michel", "Lefebvre", "Leroy", "Roux", "David",
                "Bertrand", "Morel", "Fournier", "Girard", "Bonnet", "Dupont", "Lambert", "Fontaine",
                "Rousseau", "Vincent", "Muller", "Lefevre", "Faure", "Andre", "Mercier", "Blanc",
                "Guerin", "Boyer", "Garnier", "Chevalier", "Francois", "Legrand", "Gauthier", "Garcia"
        };

        for (int i = 0; i < count; i++) {
            String firstName = firstNames[(offset + i) % firstNames.length];
            String lastName = lastNames[(offset + i * 3) % lastNames.length];
            StudentGroup group = groups.length == 0 ? null : groups[i % groups.length];

            students.add(makeUser(
                    emailPrefix + "." + String.format("%02d", i + 1) + "@etud.ut-capitole.fr",
                    "etud123",
                    firstName,
                    lastName,
                    role,
                    studentClass,
                    group
            ));
        }
    }

    private Course makeCourse(String name, Users responsable, StudentClass studentClass) {
        Course course = new Course();
        course.setName(name);
        course.setResponsable(responsable);
        course.setStudentClass(studentClass);
        return course;
    }

    private int createSession(
            ut1.appel.service.SessionService sessionService,
            Course course,
            Users teacher,
            int year,
            int month,
            int day,
            int startHour,
            int endHour,
            StudentGroup group
    ) {
        sessionService.create(
                course.getId(),
                teacher.getId(),
                java.time.LocalDate.of(year, month, day),
                java.time.LocalTime.of(startHour, 0),
                java.time.LocalTime.of(endHour, 0),
                group == null ? null : group.getId()
        );

        return 1;
    }

    private int addJustification(
            Session session,
            Users user,
            String fileUrl,
            String comment,
            JustificationStatus status,
            int startYear,
            int startMonth,
            int startDay,
            int startHour,
            int endYear,
            int endMonth,
            int endDay,
            int endHour
    ) {
        if (user == null || user.getId() == null) {
            return 0;
        }

        Users managedUser = session.get(Users.class, user.getId());
        if (managedUser == null) {
            return 0;
        }

        Justification justification = new Justification();
        justification.setUser(managedUser);
        justification.setFileUrl(fileUrl);
        justification.setDepositDate(java.time.LocalDateTime.now().minusDays(2));
        justification.setComment(comment);
        justification.setStatus(status);
        if (status == JustificationStatus.APPROUVEE) {
            justification.setStartDate(java.time.LocalDateTime.of(startYear, startMonth, startDay, startHour, 0));
            justification.setEndDate(java.time.LocalDateTime.of(endYear, endMonth, endDay, endHour, 59));
        }
        if (status != JustificationStatus.EN_ATTENTE) {
            justification.setProcessedDate(java.time.LocalDateTime.now().minusDays(1));
        }

        session.persist(justification);
        return 1;
    }

    private int addJustificationByEmail(
            Session session,
            String email,
            String fileUrl,
            String comment,
            JustificationStatus status,
            int startYear,
            int startMonth,
            int startDay,
            int startHour,
            int endYear,
            int endMonth,
            int endDay,
            int endHour
    ) {
        Users user = session.createQuery(
                        "FROM Users u WHERE u.email = :email",
                        Users.class
                )
                .setParameter("email", email)
                .uniqueResult();

        return addJustification(
                session,
                user,
                fileUrl,
                comment,
                status,
                startYear,
                startMonth,
                startDay,
                startHour,
                endYear,
                endMonth,
                endDay,
                endHour
        );
    }

    private void printSummary(PrintWriter out) {
        out.println("\n=== RÉCAPITULATIF BD ===");

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            out.println("Utilisateurs              : " + session.createQuery("SELECT COUNT(u) FROM Users u", Long.class).uniqueResult());
            out.println("Classes                   : " + session.createQuery("SELECT COUNT(c) FROM StudentClass c", Long.class).uniqueResult());
            out.println("Groupes                   : " + session.createQuery("SELECT COUNT(g) FROM StudentGroup g", Long.class).uniqueResult());
            out.println("Cours                     : " + session.createQuery("SELECT COUNT(c) FROM Course c", Long.class).uniqueResult());
            out.println("Séances                   : " + session.createQuery("SELECT COUNT(s) FROM Session s", Long.class).uniqueResult());
            out.println("Feuilles de présence      : " + session.createQuery("SELECT COUNT(a) FROM AttendanceSheet a", Long.class).uniqueResult());
            out.println("Justificatifs d'absence   : " + session.createQuery("SELECT COUNT(j) FROM Justification j", Long.class).uniqueResult());
            out.println("Lignes de présence        : " + session.createQuery("SELECT COUNT(r) FROM AttendanceRow r", Long.class).uniqueResult());
        } catch (Exception e) {
            out.println("Erreur récap : " + e.getMessage());
        }
    }
}
