package ut1.appel.servlet;

import ut1.appel.entity.StudentClass;
import ut1.appel.service.StudentClassService;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/scolarite/*")
public class ScolariteServlet extends HttpServlet {

    private final StudentClassService classService = new StudentClassService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String action = req.getPathInfo() == null ? "/" : req.getPathInfo();

        switch (action) {
            case "/classes"      -> handleClassesList(req, resp);
            case "/classes/form" -> handleClassesForm(req, resp);
            default -> req.getRequestDispatcher("/WEB-INF/views/home/scolarite.jsp")
                    .forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String action = req.getPathInfo() == null ? "/" : req.getPathInfo();

        switch (action) {
            case "/classes/save"            -> handleSave(req, resp);
            case "/classes/save-students"   -> handleSaveStudents(req, resp);
        }
    }

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
        String[] userIds  = req.getParameterValues("userId");
        String[] classIds = req.getParameterValues("classId");
        classService.saveStudentAssignments(userIds, classIds);
        resp.sendRedirect(req.getContextPath() + "/scolarite/classes");
    }
}