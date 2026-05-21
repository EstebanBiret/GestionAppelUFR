package ut1.appel.servlet;

import ut1.appel.entity.Users;
import ut1.appel.service.UserService;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@WebServlet("/profil/*")
@MultipartConfig
public class ProfileServlet extends HttpServlet {

    private final UserService userService = new UserService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String action = req.getPathInfo();

        switch (action == null ? "/voir" : action) {
            case "/modifier" ->
                    req.getRequestDispatcher("/WEB-INF/views/profil/modifier.jsp").forward(req, resp);
            default ->
                    req.getRequestDispatcher("/WEB-INF/views/profil/voir.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String action = req.getPathInfo();

        if ("/modifier".equals(action)) {
            handleModifier(req, resp);
        }
    }

    private void handleModifier(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        Users currentUser = (session != null) ? (Users) session.getAttribute("currentUser") : null;

        if (currentUser == null) {
            resp.sendRedirect(req.getContextPath() + "/auth/login");
            return;
        }

        String newEmail = req.getParameter("email");
        if (newEmail != null) newEmail = newEmail.trim();

        if (newEmail != null && !newEmail.isBlank()
                && !newEmail.equals(currentUser.getEmail())
                && userService.emailExists(newEmail)) {
            req.setAttribute("error", "Cet email est déjà utilisé par un autre compte.");
            req.getRequestDispatcher("/WEB-INF/views/profil/modifier.jsp").forward(req, resp);
            return;
        }

        // Handle photo upload
        String newPicturePath = null;
        Part filePart = req.getPart("photo");

        if (filePart != null && filePart.getSize() > 0) {
            String originalName = filePart.getSubmittedFileName();
            String extension = "";
            if (originalName != null && originalName.contains(".")) {
                extension = originalName.substring(originalName.lastIndexOf("."));
            }

            String uniqueFileName = UUID.randomUUID() + extension;
            String uploadDir = getServletContext().getInitParameter("uploadDir");
            Path uploadPath = Paths.get(uploadDir);

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            Files.copy(filePart.getInputStream(),
                    uploadPath.resolve(uniqueFileName),
                    StandardCopyOption.REPLACE_EXISTING);

            newPicturePath = "images/users/" + uniqueFileName;
        }

        Users updatedUser = userService.updateProfile(currentUser.getId(), newEmail, newPicturePath);

        // Refresh session with updated user
        session.setAttribute("currentUser", updatedUser);

        resp.sendRedirect(req.getContextPath() + "/profil/voir?success=1");
    }
}