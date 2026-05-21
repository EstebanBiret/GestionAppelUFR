package ut1.appel.servlet;

import ut1.appel.entity.AttendanceSheet;
import ut1.appel.entity.Users;
import ut1.appel.service.AttendanceSheetService;
import ut1.appel.service.TeacherService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/enseignant/appel")
public class AttendanceSheetServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession httpSession = req.getSession();
        Users me = (Users) httpSession.getAttribute("currentUser");

        if (me == null) {
            resp.sendRedirect(req.getContextPath() + "/auth/login");
            return;
        }

        String sessionIdParam = req.getParameter("sessionId");
        if (sessionIdParam == null || sessionIdParam.isBlank()) {
            resp.sendRedirect(req.getContextPath() + "/enseignant");
            return;
        }

        Long sessionId;
        try {
            sessionId = Long.parseLong(sessionIdParam);
        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/enseignant");
            return;
        }

        ut1.appel.entity.Session courseSession = TeacherService.getSessionById(sessionId);

        if (courseSession == null) {
            resp.sendRedirect(req.getContextPath() + "/enseignant");
            return;
        }

        if (!courseSession.getTeacher().getId().equals(me.getId())) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Accès refusé.");
            return;
        }

        AttendanceSheet sheet = AttendanceSheetService.getSheetBySessionId(sessionId);

        if (sheet == null) {
            req.setAttribute("errorMessage", "Aucune feuille d'appel trouvée pour cette session.");
            req.setAttribute("courseSession", courseSession);
            req.getRequestDispatcher("/WEB-INF/views/teacher/attendanceSheet.jsp").forward(req, resp);
            return;
        }

        req.setAttribute("courseSession", courseSession);
        req.setAttribute("sheet", sheet);
        req.getRequestDispatcher("/WEB-INF/views/teacher/attendanceSheet.jsp").forward(req, resp);
    }
}