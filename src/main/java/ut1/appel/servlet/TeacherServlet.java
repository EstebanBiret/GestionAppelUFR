package ut1.appel.servlet;

import ut1.appel.entity.Course;
import ut1.appel.service.CourseService;
import ut1.appel.service.CourseStatsService;

import ut1.appel.entity.Users;
import ut1.appel.entity.Session;
import ut1.appel.service.TeacherService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet("/enseignant")
public class TeacherServlet extends HttpServlet {

    private final CourseService courseService = new CourseService();
    private final CourseStatsService statsService = new CourseStatsService();

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

        // Handle US16 stats page
        if ("stats".equals(action)) {
            handleCourseStats(req, resp);
            return;
        }

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

    private void handleCourseStats(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String idParam = req.getParameter("courseId");
        if (idParam == null) {
            resp.sendRedirect(req.getContextPath() + "/enseignant");
            return;
        }

        Long courseId = Long.parseLong(idParam);
        Course course = courseService.findById(courseId);
        if (course == null) {
            resp.sendRedirect(req.getContextPath() + "/enseignant");
            return;
        }

        // Load stats and pass them to the view
        req.setAttribute("course", course);
        req.setAttribute("absencePerSession", statsService.getAbsencePerSession(courseId));
        req.setAttribute("studentsAtRisk", statsService.getStudentsWithManyAbsences(courseId));
        req.setAttribute("averageAbsences", statsService.getAverageAbsences(courseId));

        req.getRequestDispatcher("/WEB-INF/views/teacher/courseStats.jsp").forward(req, resp);
    }
}