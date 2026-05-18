package ut1.appel.servlet;

import ut1.appel.entity.Users;
import ut1.appel.filter.AuthFilter;
import ut1.appel.service.UserService;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;
import java.util.UUID;

@WebServlet("/auth/*")
@MultipartConfig
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

        String firstName = req.getParameter("firstName");
        String lastName  = req.getParameter("lastName");
        String email     = req.getParameter("email");
        String password  = req.getParameter("password");
        String confirm   = req.getParameter("confirmPassword");

        // Vérification null avant trim()
        if (firstName == null || lastName == null || email == null || password == null || confirm == null) {
            req.setAttribute("error", "Tous les champs sont obligatoires.");
            req.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(req, resp);
            return;
        }

        firstName = firstName.trim();
        lastName  = lastName.trim();
        email     = email.trim();

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

        String picturePath = "images/users/default.jpg";

        Part filePart = req.getPart("profilePicture");
        if (filePart != null && filePart.getSize() > 0) {
            // Récupère l'extension du fichier original
            String originalFileName = filePart.getSubmittedFileName();
            String extension = "";
            if (originalFileName != null && originalFileName.contains(".")) {
                extension = originalFileName.substring(originalFileName.lastIndexOf("."));
            }

            String uniqueFileName = UUID.randomUUID().toString() + extension;

            // Chemin lu depuis web.xml, portable sur toutes les machines
            String uploadDir = getServletContext().getInitParameter("uploadDir");
            File uploadFolder = new File(uploadDir);
            if (!uploadFolder.exists()) {
                uploadFolder.mkdirs();
            }

            File file = new File(uploadFolder, uniqueFileName);
            try (InputStream input = filePart.getInputStream();
                 OutputStream output = new FileOutputStream(file)) {
                input.transferTo(output);
            }

            picturePath = "images/users/" + uniqueFileName;
        }

        Users newUser = userService.register(firstName, lastName, email, password, picturePath);
        req.getSession().setAttribute("currentUser", newUser);
        resp.sendRedirect(req.getContextPath() + "/auth/pending");
    }


    private void handleLogin(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String email    = req.getParameter("email");
        String password = req.getParameter("password");

        if (email == null || password == null) {
            req.setAttribute("error", "Champs manquants.");
            req.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(req, resp);
            return;
        }

        email = email.trim();
        Users user = userService.findByEmail(email);

        if (user == null || !user.getPassword().equals(UserService.hashPassword(password))) {
            req.setAttribute("error", "Mot de passe ou email incorrect.");
            req.setAttribute("emailValue", email);
            req.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(req, resp);
            return;
        }

        req.getSession().setAttribute("currentUser", user);
        AuthFilter.redirectByRole(user.getRole(), req, resp);
    }
}