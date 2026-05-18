package ut1.appel.servlet;

import ut1.appel.entity.StudentClass;
import ut1.appel.entity.StudentGroup;
import ut1.appel.entity.Users;
import ut1.appel.service.StudentClassService;
import ut1.appel.service.StudentGroupService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet("/scolarite/*")
public class ScolariteServlet extends HttpServlet {

    private final StudentClassService classService = new StudentClassService();
    private final StudentGroupService groupService = new StudentGroupService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String action = req.getPathInfo() == null ? "/" : req.getPathInfo();
        switch (action) {
            case "/classes"                   -> handleClassesList(req, resp);
            case "/classes/form"              -> handleClassesForm(req, resp);
            case "/groupes"                   -> handleGroupesList(req, resp);
            case "/groupes/form"              -> handleGroupesForm(req, resp);
            case "/groupes/students-by-class" -> handleStudentsByClass(req, resp);
            default -> req.getRequestDispatcher("/WEB-INF/views/home/scolarite.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String action = req.getPathInfo() == null ? "/" : req.getPathInfo();
        switch (action) {
            case "/classes/save"             -> handleSave(req, resp);
            case "/classes/save-students"    -> handleSaveStudents(req, resp);
            case "/groupes/save"             -> handleGroupeSave(req, resp);
        }
    }

    // ===== Classes ===== //

    private void handleClassesList(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setAttribute("classes", classService.findAll());
        req.getRequestDispatcher("/WEB-INF/views/scolarite/classes.jsp").forward(req, resp);
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
        req.getRequestDispatcher("/WEB-INF/views/scolarite/classForm.jsp").forward(req, resp);
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
            req.getRequestDispatcher("/WEB-INF/views/scolarite/classForm.jsp").forward(req, resp);
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

    //====== Groupes ======//

    private void handleGroupesList(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setAttribute("groupes", groupService.findAll());
        req.getRequestDispatcher("/WEB-INF/views/scolarite/groupes.jsp").forward(req, resp);
    }

    private void handleGroupesForm(HttpServletRequest req, HttpServletResponse resp)
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
        req.getRequestDispatcher("/WEB-INF/views/scolarite/groupForm.jsp").forward(req, resp);
    }

    private void handleStudentsByClass(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        String classIdParam = req.getParameter("classId");
        resp.setContentType("application/json;charset=UTF-8");
        if (classIdParam == null || classIdParam.isEmpty()) {
            resp.getWriter().write("[]"); return;
        }
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
                    .append("\"role\":\"").append(s.getRole()).append("\",")
                    .append("\"groupId\":").append(groupId != null ? groupId : "null").append(",")
                    .append("\"groupName\":").append(groupName != null ? "\"" + groupName + "\"" : "null")
                    .append("}");
        }
        json.append("]");
        resp.getWriter().write(json.toString());
    }

    private void handleGroupeSave(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String idParam  = req.getParameter("id");
        String name     = req.getParameter("name").trim();
        String classIdStr = req.getParameter("classId");
        Long id      = (idParam != null && !idParam.isEmpty()) ? Long.parseLong(idParam) : null;
        Long classId = (classIdStr != null && !classIdStr.isEmpty()) ? Long.parseLong(classIdStr) : null;

        if (name.isEmpty() || classId == null) {
            req.setAttribute("error", "Le nom et la classe sont obligatoires.");
            handleGroupesForm(req, resp); return;
        }
        if (groupService.nameExistsInClass(name, classId, id)) {
            req.setAttribute("error", "Un groupe avec ce nom existe déjà dans cette classe.");
            if (id != null) req.setAttribute("groupe", groupService.findById(id));
            req.setAttribute("classes", classService.findAll());
            req.setAttribute("students", groupService.findStudentsByClass(classId));
            req.getRequestDispatcher("/WEB-INF/views/scolarite/groupForm.jsp").forward(req, resp);
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
}