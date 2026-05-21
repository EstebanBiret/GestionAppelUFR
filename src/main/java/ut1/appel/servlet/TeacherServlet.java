package ut1.appel.servlet;

import ut1.appel.entity.Course;
import ut1.appel.service.CourseService;
import ut1.appel.service.CourseStatsService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet("/enseignant/*")
public class TeacherServlet extends HttpServlet {

    private final CourseService courseService = new CourseService();
    private final CourseStatsService statsService = new CourseStatsService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        System.out.println("DEBUG pathInfo: " + req.getPathInfo());

        String action = req.getPathInfo() == null ? "/" : req.getPathInfo();

        switch (action) {
            case "/cours/stats" -> handleCourseStats(req, resp);
            default -> req.getRequestDispatcher("/WEB-INF/views/home/teacher.jsp").forward(req, resp);
        }
    }

    private void handleCourseStats(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String idParam = req.getParameter("courseId");
        if (idParam == null) {
            resp.sendRedirect(req.getContextPath() + "/enseignant/");
            return;
        }

        Long courseId = Long.parseLong(idParam);
        Course course = courseService.findById(courseId);
        if (course == null) {
            resp.sendRedirect(req.getContextPath() + "/enseignant/");
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