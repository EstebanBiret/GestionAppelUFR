package ut1.appel.servlet;

import ut1.appel.entity.*;
import ut1.appel.service.*;
import ut1.appel.enums.JustificationStatus;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@WebServlet("/scolarite/*")
public class ScholarshipServlet extends HttpServlet {

    private final StudentClassService   classService   = new StudentClassService();
    private final StudentGroupService   groupService   = new StudentGroupService();
    private final CourseService         courseService  = new CourseService();
    private final SessionService        sessionService = new SessionService();
    private final JustificationService justificationService = new JustificationService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String action = req.getPathInfo() == null ? "/" : req.getPathInfo();
        switch (action) {
            case "/classes"                   -> handleClassesList(req, resp);
            case "/classes/form"              -> handleClassesForm(req, resp);
            case "/groupes"                   -> handleGroupsList(req, resp);
            case "/groupes/form"              -> handleGroupsForm(req, resp);
            case "/groupes/students-by-class" -> handleStudentsByClass(req, resp);
            case "/cours"                     -> handleCourseList(req, resp);
            case "/cours/form"                -> handleCourseForm(req, resp);
            case "/seances"                   -> handleSessionsList(req, resp);
            case "/seances/form"              -> handleSessionsForm(req, resp);
            case "/justificatifs"             -> handleJustifList(req, resp);
            case "/justificatifs/form"        -> handleJustifForm(req, resp);
            default -> req.getRequestDispatcher("/WEB-INF/views/home/scholarship.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String action = req.getPathInfo() == null ? "/" : req.getPathInfo();
        switch (action) {
            case "/classes/save"          -> handleSave(req, resp);
            case "/classes/save-students" -> handleSaveStudents(req, resp);
            case "/groupes/save"          -> handleGroupSave(req, resp);
            case "/cours/save"            -> handleCourseSave(req, resp);
            case "/seances/save"          -> handleSessionSave(req, resp);
            case "/justificatifs/save"    -> handleJustifSave(req, resp);
        }
    }

    // ===== Classes ===== //

    private void handleClassesList(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setAttribute("classes", classService.findAll());
        req.getRequestDispatcher("/WEB-INF/views/scholarship/classes.jsp").forward(req, resp);
    }

    private void handleClassesForm(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String idParam = req.getParameter("id");
        if (idParam != null) {
            try {
                StudentClass c = classService.findById(Long.parseLong(idParam));
                if (c == null) { resp.sendRedirect(req.getContextPath() + "/scolarite/classes"); return; }
                req.setAttribute("studentClass", c);
            } catch (NumberFormatException e) {
                resp.sendRedirect(req.getContextPath() + "/scolarite/classes"); return;
            }
        }
        req.setAttribute("classes", classService.findAll());
        req.setAttribute("students", classService.findAllStudents());
        req.getRequestDispatcher("/WEB-INF/views/scholarship/classForm.jsp").forward(req, resp);
    }

    private void handleSave(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String idParam = req.getParameter("id");
        String name    = req.getParameter("name").trim();
        Long id = (idParam != null && !idParam.isEmpty()) ? Long.parseLong(idParam) : null;
        if (name.isEmpty()) {
            req.setAttribute("error", "Le nom de la classe est obligatoire.");
            handleClassesForm(req, resp); return;
        }
        if (classService.nameExists(name, id)) {
            req.setAttribute("error", "Une classe avec ce nom existe déjà.");
            if (id != null) req.setAttribute("studentClass", classService.findById(id));
            req.setAttribute("classes", classService.findAll());
            req.setAttribute("students", classService.findAllStudents());
            req.getRequestDispatcher("/WEB-INF/views/scholarship/classForm.jsp").forward(req, resp);
            return;
        }
        if (id == null) classService.create(name);
        else            classService.update(id, name);
        resp.sendRedirect(req.getContextPath() + "/scolarite/classes");
    }

    private void handleSaveStudents(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        Long classId = Long.parseLong(req.getParameter("classId"));
        String[] checkedIds = req.getParameterValues("checkedStudents");
        classService.saveStudentAssignments(classId, checkedIds);
        resp.sendRedirect(req.getContextPath() + "/scolarite/classes");
    }

    // ===== Groupes ===== //

    private void handleGroupsList(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setAttribute("groupes", groupService.findAll());
        req.getRequestDispatcher("/WEB-INF/views/scholarship/groups.jsp").forward(req, resp);
    }

    private void handleGroupsForm(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String idParam = req.getParameter("id");
        if (idParam != null) {
            try {
                StudentGroup g = groupService.findById(Long.parseLong(idParam));
                if (g == null) { resp.sendRedirect(req.getContextPath() + "/scolarite/groupes"); return; }
                req.setAttribute("groupe", g);
                req.setAttribute("students", groupService.findStudentsByClass(g.getStudentClass().getId()));
            } catch (NumberFormatException e) {
                resp.sendRedirect(req.getContextPath() + "/scolarite/groupes"); return;
            }
        }
        req.setAttribute("classes", classService.findAll());
        req.getRequestDispatcher("/WEB-INF/views/scholarship/groupForm.jsp").forward(req, resp);
    }

    private void handleStudentsByClass(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        String classIdParam = req.getParameter("classId");
        resp.setContentType("application/json;charset=UTF-8");
        if (classIdParam == null || classIdParam.isEmpty()) { resp.getWriter().write("[]"); return; }
        List<Users> students = groupService.findStudentsByClass(Long.parseLong(classIdParam));
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < students.size(); i++) {
            Users s = students.get(i);
            if (i > 0) json.append(",");
            String groupName = s.getStudentGroup() != null ? s.getStudentGroup().getName() : null;
            Long   groupId   = s.getStudentGroup() != null ? s.getStudentGroup().getId()   : null;
            json.append("{")
                    .append("\"id\":").append(s.getId()).append(",")
                    .append("\"firstName\":\"").append(s.getFirstName()).append("\",")
                    .append("\"lastName\":\"").append(s.getLastName()).append("\",")
                    .append("\"email\":\"").append(s.getEmail()).append("\",")
                    .append("\"picturePath\":").append(s.getPicturePath() != null ? "\"" + s.getPicturePath() + "\"" : "null").append(",")
                    .append("\"role\":\"").append(s.getRole()).append("\",")
                    .append("\"groupId\":").append(groupId != null ? groupId : "null").append(",")
                    .append("\"groupName\":").append(groupName != null ? "\"" + groupName + "\"" : "null")
                    .append("}");
        }
        resp.getWriter().write(json.append("]").toString());
    }

    private void handleGroupSave(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String idParam    = req.getParameter("id");
        String name       = req.getParameter("name").trim();
        String classIdStr = req.getParameter("classId");
        Long id      = (idParam != null && !idParam.isEmpty()) ? Long.parseLong(idParam) : null;
        Long classId = (classIdStr != null && !classIdStr.isEmpty()) ? Long.parseLong(classIdStr) : null;
        if (name.isEmpty() || classId == null) {
            req.setAttribute("error", "Le nom et la classe sont obligatoires.");
            handleGroupsForm(req, resp); return;
        }
        if (groupService.nameExistsInClass(name, classId, id)) {
            req.setAttribute("error", "Un groupe avec ce nom existe déjà dans cette classe.");
            if (id != null) req.setAttribute("groupe", groupService.findById(id));
            req.setAttribute("classes", classService.findAll());
            req.setAttribute("students", groupService.findStudentsByClass(classId));
            req.getRequestDispatcher("/WEB-INF/views/scholarship/groupForm.jsp").forward(req, resp);
            return;
        }
        String[] checkedIds = req.getParameterValues("checkedStudents");
        Long[] userIds = null;
        if (checkedIds != null) {
            userIds = new Long[checkedIds.length];
            for (int i = 0; i < checkedIds.length; i++) userIds[i] = Long.parseLong(checkedIds[i]);
        }
        if (id == null) groupService.create(name, classId, userIds);
        else            groupService.update(id, name, classId, userIds);
        resp.sendRedirect(req.getContextPath() + "/scolarite/groupes");
    }

    // ===== Cours ===== //

    private void handleCourseList(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setAttribute("cours", courseService.findAll());
        req.getRequestDispatcher("/WEB-INF/views/scholarship/course.jsp").forward(req, resp);
    }

    private void handleCourseForm(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setAttribute("classes",     classService.findAll());
        req.setAttribute("enseignants", courseService.findAllTeachers());
        req.getRequestDispatcher("/WEB-INF/views/scholarship/courseForm.jsp").forward(req, resp);
    }

    private void handleCourseSave(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String name       = req.getParameter("name").trim();
        String classIdStr = req.getParameter("classId");
        String respIdStr  = req.getParameter("responsableId");
        Long classId = (classIdStr != null && !classIdStr.isEmpty()) ? Long.parseLong(classIdStr) : null;
        Long respId  = (respIdStr  != null && !respIdStr.isEmpty())  ? Long.parseLong(respIdStr)  : null;
        if (name.isEmpty() || classId == null || respId == null) {
            req.setAttribute("error", "Tous les champs sont obligatoires.");
            req.setAttribute("classes",     classService.findAll());
            req.setAttribute("enseignants", courseService.findAllTeachers());
            req.getRequestDispatcher("/WEB-INF/views/scholarship/courseForm.jsp").forward(req, resp);
            return;
        }
        if (courseService.existsForClass(name, classId, null)) {
            req.setAttribute("error", "Un cours « " + name + " » existe déjà pour cette classe.");
            req.setAttribute("classes",     classService.findAll());
            req.setAttribute("enseignants", courseService.findAllTeachers());
            req.getRequestDispatcher("/WEB-INF/views/scholarship/courseForm.jsp").forward(req, resp);
            return;
        }
        Long newCourseId = courseService.create(name, classId, respId);
        resp.sendRedirect(req.getContextPath() + "/scolarite/cours?courseId=" + newCourseId + "&success=cours");

    }

    // ===== Séances ===== //

    private void handleSessionsList(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        String courseIdParam = req.getParameter("courseId");
        resp.setContentType("application/json;charset=UTF-8");
        if (courseIdParam == null || courseIdParam.isEmpty()) { resp.getWriter().write("[]"); return; }
        List<Session> sessions = sessionService.findByCourse(Long.parseLong(courseIdParam));
        resp.getWriter().write(sessionService.toJsonArray(sessions));
    }

    private void handleSessionsForm(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String courseIdParam = req.getParameter("courseId");
        if (courseIdParam == null || courseIdParam.isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/scolarite/cours"); return;
        }
        Course course = courseService.findById(Long.parseLong(courseIdParam));
        if (course == null) {
            resp.sendRedirect(req.getContextPath() + "/scolarite/cours"); return;
        }
        req.setAttribute("course",      course);
        req.setAttribute("enseignants", courseService.findAllTeachers());
        req.setAttribute("groupes",     groupService.findByClass(course.getStudentClass().getId()));
        req.getRequestDispatcher("/WEB-INF/views/scholarship/sessionForm.jsp").forward(req, resp);
    }

    private void handleSessionSave(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Long courseId;
        try {
            courseId = Long.parseLong(req.getParameter("courseId"));
        } catch (Exception e) {
            resp.sendRedirect(req.getContextPath() + "/scolarite/cours"); return;
        }

        String dateParam      = req.getParameter("sessionDate");
        String startParam     = req.getParameter("startTime");
        String endParam       = req.getParameter("endTime");
        String teacherIdParam = req.getParameter("teacherId");
        String groupIdParam   = req.getParameter("groupId");

        String    error    = null;
        LocalDate date     = null;
        LocalTime start    = null;
        LocalTime end      = null;
        Long      teacher  = null;
        Long      groupId  = null;

        try { date    = LocalDate.parse(dateParam); }        catch (Exception e) { error = "Date invalide."; }
        try { start   = LocalTime.parse(startParam); }      catch (Exception e) { if (error == null) error = "Heure de début invalide."; }
        try { end     = LocalTime.parse(endParam); }        catch (Exception e) { if (error == null) error = "Heure de fin invalide."; }
        try { teacher = Long.parseLong(teacherIdParam); }   catch (Exception e) { if (error == null) error = "L'enseignant est obligatoire."; }

        if (groupIdParam != null && !groupIdParam.isEmpty()) {
            try { groupId = Long.parseLong(groupIdParam); } catch (Exception ignored) {}
        }

        if (error == null && date != null && date.isBefore(LocalDate.now()))
            error = "La date ne peut pas être dans le passé.";
        if (error == null && start != null && end != null && !end.isAfter(start))
            error = "L'heure de fin doit être supérieure à l'heure de début.";

        if (error != null) {
            req.setAttribute("error", error);
            Course course = courseService.findById(courseId);
            req.setAttribute("course",      course);
            req.setAttribute("enseignants", courseService.findAllTeachers());
            if (course != null && course.getStudentClass() != null)
                req.setAttribute("groupes", groupService.findByClass(course.getStudentClass().getId()));
            req.getRequestDispatcher("/WEB-INF/views/scholarship/sessionForm.jsp").forward(req, resp);
            return;
        }

        sessionService.create(courseId, teacher, date, start, end, groupId);
        resp.sendRedirect(req.getContextPath() + "/scolarite/cours?courseId=" + courseId + "&success=seance");
    }

    // ===== Justificatifs ===== //

    private void handleJustifList(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setAttribute("justificatifs", justificationService.findAll());
        if ("ok".equals(req.getParameter("success")))
            req.setAttribute("success", "Le justificatif a bien été traité.");
        req.getRequestDispatcher("/WEB-INF/views/scholarship/justificationsList.jsp").forward(req, resp);
    }

    private void handleJustifForm(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String idParam = req.getParameter("id");
        if (idParam == null) { resp.sendRedirect(req.getContextPath() + "/scolarite/justificatifs"); return; }
        Justification j = justificationService.findById(Long.parseLong(idParam));
        if (j == null || j.getStatus() != JustificationStatus.EN_ATTENTE) {
            resp.sendRedirect(req.getContextPath() + "/scolarite/justificatifs"); return;
        }
        req.setAttribute("justif", j);
        req.getRequestDispatcher("/WEB-INF/views/scholarship/justificationForm.jsp").forward(req, resp);
    }

    private void handleJustifSave(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Long id;
        try { id = Long.parseLong(req.getParameter("id")); }
        catch (Exception e) { resp.sendRedirect(req.getContextPath() + "/scolarite/justificatifs"); return; }

        String decision = req.getParameter("decision");
        String feedback = req.getParameter("feedback");

        if (!"APPROUVEE".equals(decision) && !"REJETEE".equals(decision)) {
            req.setAttribute("error", "Décision invalide.");
            req.setAttribute("justif", justificationService.findById(id));
            req.getRequestDispatcher("/WEB-INF/views/scholarship/justificationForm.jsp").forward(req, resp);
            return;
        }

        LocalDateTime start = null;
        LocalDateTime end   = null;

        if ("APPROUVEE".equals(decision)) {
            String startStr = req.getParameter("startDate");
            String endStr   = req.getParameter("endDate");
            String error    = null;

            try { start = LocalDateTime.parse(startStr); } catch (Exception e) { error = "Date de début invalide."; }
            try { end   = LocalDateTime.parse(endStr);   } catch (Exception e) { if (error == null) error = "Date de fin invalide."; }
            if (error == null && !end.isAfter(start)) error = "La date de fin doit être après la date de début.";

            if (error != null) {
                req.setAttribute("error", error);
                req.setAttribute("justif", justificationService.findById(id));
                req.getRequestDispatcher("/WEB-INF/views/scholarship/justificationForm.jsp").forward(req, resp);
                return;
            }
        }

        justificationService.process(id, JustificationStatus.valueOf(decision), start, end, feedback);
        resp.sendRedirect(req.getContextPath() + "/scolarite/justificatifs?success=ok");
    }
}