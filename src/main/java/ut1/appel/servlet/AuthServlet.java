package ut1.appel.servlet;

import ut1.appel.entity.Users;
import ut1.appel.filter.AuthFilter;
import ut1.appel.service.UserService;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/auth/*")
public class AuthServlet extends HttpServlet {

    private final UserService userService = new UserService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String action = req.getPathInfo();

        switch (action == null ? "/login" : action) {
            case "/register" ->
                    req.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(req, resp);
            case "/pending" ->
                    req.getRequestDispatcher("/WEB-INF/views/home/pending.jsp").forward(req, resp);
            case "/logout" -> {
                HttpSession session = req.getSession(false);
                if (session != null) session.invalidate();
                resp.sendRedirect(req.getContextPath() + "/auth/login");
            }
            default ->
                    req.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String action = req.getPathInfo();

        switch (action == null ? "/login" : action) {
            case "/register" -> handleRegister(req, resp);
            case "/login"    -> handleLogin(req, resp);
        }
    }

    private void handleRegister(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String firstName = req.getParameter("firstName").trim();
        String lastName  = req.getParameter("lastName").trim();
        String email     = req.getParameter("email").trim();
        String password  = req.getParameter("password");
        String confirm   = req.getParameter("confirmPassword");

        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty()) {
            req.setAttribute("error", "Tous les champs sont obligatoires.");
            req.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(req, resp);
            return;
        }

        if (!password.equals(confirm)) {
            req.setAttribute("error", "Les mots de passe ne correspondent pas.");
            req.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(req, resp);
            return;
        }

        if (userService.emailExists(email)) {
            req.setAttribute("error", "Cet email est déjà utilisé.");
            req.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(req, resp);
            return;
        }

        Users newUser = userService.register(firstName, lastName, email, password);
        req.getSession().setAttribute("currentUser", newUser);
        resp.sendRedirect(req.getContextPath() + "/auth/pending");
    }

    private void handleLogin(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String email    = req.getParameter("email").trim();
        String password = req.getParameter("password");

        Users user = userService.findByEmail(email);

        if (user == null) {
            req.setAttribute("error", "Aucun compte trouvé avec cet email.");
            req.setAttribute("emailValue", email);
            req.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(req, resp);
            return;
        }

        if (!user.getPassword().equals(UserService.hashPassword(password))) {
            req.setAttribute("error", "Mot de passe incorrect.");
            req.setAttribute("emailValue", email);
            req.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(req, resp);
            return;
        }

        req.getSession().setAttribute("currentUser", user);
        AuthFilter.redirectByRole(user.getRole(), req, resp);
    }
}