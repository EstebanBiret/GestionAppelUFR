package ut1.appel.servlet;

import ut1.appel.entity.Users;
import ut1.appel.entity.Session;
import ut1.appel.service.TeacherService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet("/enseignant")
public class TeacherServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession httpSession = req.getSession();
        Users me = (Users) httpSession.getAttribute("currentUser");

        if (me == null) {
            resp.sendRedirect(req.getContextPath() + "/auth/login");
            return;
        }

        String action = req.getParameter("action");

        if ("upcoming".equals(action)) {
            List<Session> sessions = TeacherService.getAllUpcomingSessionsForTeacher(me.getId());
            req.setAttribute("sessions", sessions);
            req.setAttribute("mode", "upcoming");
            req.getRequestDispatcher("/WEB-INF/views/teacher/sessionList.jsp").forward(req, resp);
            return;
        }

        if ("past".equals(action)) {
            List<Session> sessions = TeacherService.getAllPastSessionsForTeacher(me.getId());
            req.setAttribute("sessions", sessions);
            req.setAttribute("mode", "past");
            req.getRequestDispatcher("/WEB-INF/views/teacher/sessionList.jsp").forward(req, resp);
            return;
        }

        if (!"home".equals(action)) {
            Session current = TeacherService.getCurrentSession(me.getId());
            if (current != null) {
                resp.sendRedirect(req.getContextPath() + "/enseignant/appel?sessionId=" + current.getId());
                return;
            }
        }

        req.getRequestDispatcher("/WEB-INF/views/home/teacher.jsp").forward(req, resp);
    }
}