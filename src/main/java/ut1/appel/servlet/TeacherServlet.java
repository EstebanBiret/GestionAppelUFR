package ut1.appel.servlet;

import ut1.appel.entity.Users;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/enseignant/*")
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

        // Vérifier si on demande explicitement la page d'accueil
        String action = req.getParameter("action");
        if ("home".equals(action)) {
            req.getRequestDispatcher("/WEB-INF/views/home/teacher.jsp").forward(req, resp);
            return;
        }

        if (ut1.appel.service.TeacherService.isCurrentSession(me.getId())) {
            req.getRequestDispatcher("/WEB-INF/views/teacher/attendanceSheet.jsp").forward(req, resp);
        } else {
            req.getRequestDispatcher("/WEB-INF/views/home/teacher.jsp").forward(req, resp);
        }
    }
}