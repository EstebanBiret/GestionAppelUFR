package ut1.appel.servlet;

import ut1.appel.entity.Users;
import ut1.appel.service.UserService;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;
import java.nio.file.*;
import java.util.UUID;
import java.nio.file.StandardCopyOption;

@WebServlet("/profil/*")
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024,
        maxFileSize       = 5 * 1024 * 1024,
        maxRequestSize    = 10 * 1024 * 1024
)
public class ProfileServlet extends HttpServlet {

    private final UserService userService = new UserService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String action = req.getPathInfo() == null ? "/" : req.getPathInfo();
        switch (action) {
            case "/voir"     -> handleView(req, resp);
            case "/modifier" -> handleEditForm(req, resp);
            default          -> resp.sendRedirect(req.getContextPath() + "/profil/voir");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if ("/save".equals(req.getPathInfo())) handleSave(req, resp);
        else resp.sendRedirect(req.getContextPath() + "/profil/voir");
    }

    private void handleView(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/views/profile/view.jsp").forward(req, resp);
    }

    private void handleEditForm(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/views/profile/edit.jsp").forward(req, resp);
    }

    private void handleSave(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Users u     = (Users) req.getSession().getAttribute("currentUser");
        String email = req.getParameter("email");

        if (email == null || email.isBlank()) {
            req.setAttribute("error", "L'adresse email est obligatoire.");
            req.getRequestDispatcher("/WEB-INF/views/profile/edit.jsp").forward(req, resp);
            return;
        }
        if (!email.equalsIgnoreCase(u.getEmail())
                && userService.emailExists(email.trim().toLowerCase())) {
            req.setAttribute("error", "Cette adresse email est déjà utilisée.");
            req.getRequestDispatcher("/WEB-INF/views/profile/edit.jsp").forward(req, resp);
            return;
        }

        String newPicturePath = null;
        Part photoPart = req.getPart("photo");
        if (photoPart != null && photoPart.getSize() > 0) {
            String originalName = photoPart.getSubmittedFileName();
            String ext = (originalName != null && originalName.contains("."))
                    ? originalName.substring(originalName.lastIndexOf('.'))
                    : ".jpg";
            if (!ext.toLowerCase().matches("\\.(jpg|jpeg|png|gif|webp)")) {
                req.setAttribute("error", "Format non supporté (jpg, png, gif, webp).");
                req.getRequestDispatcher("/WEB-INF/views/profile/edit.jsp").forward(req, resp);
                return;
            }

            String fileName  = UUID.randomUUID() + ext;
            String uploadDir = getServletContext().getInitParameter("uploadDir");
            Path uploadPath  = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) Files.createDirectories(uploadPath);
            Files.copy(photoPart.getInputStream(),
                    uploadPath.resolve(fileName),
                    StandardCopyOption.REPLACE_EXISTING);

            newPicturePath = "images/users/" + fileName;
        }

        Users updated = userService.updateProfile(u.getId(), email, newPicturePath);
        req.getSession().setAttribute("currentUser", updated);
        resp.sendRedirect(req.getContextPath() + "/profil/voir?success=1");
    }
}