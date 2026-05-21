package ut1.appel.servlet;

import ut1.appel.entity.Justification;
import ut1.appel.entity.Users;
import ut1.appel.service.JustificationService;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@WebServlet("/etudiant/justification/*")
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024 * 2,
        maxFileSize = 1024 * 1024 * 10,
        maxRequestSize = 1024 * 1024 * 15
)
public class JustificationServlet extends HttpServlet {

    private final JustificationService justificationService = new JustificationService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String action = req.getPathInfo() == null ? "/" : req.getPathInfo();
        Users currentUser = (Users) req.getSession().getAttribute("currentUser");

        switch (action) {
            case "/liste":
                List<Justification> list = justificationService.findByUser(currentUser);
                req.setAttribute("justifications", list);
                if ("true".equals(req.getParameter("success"))) {
                    req.setAttribute("success", "Votre justificatif a été transmis avec succès à la scolarité.");
                }
                req.getRequestDispatcher("/WEB-INF/views/student/justificationsList.jsp").forward(req, resp);
                break;

            case "/nouveau":
            case "/":
            default:
                req.getRequestDispatcher("/WEB-INF/views/student/justification.jsp").forward(req, resp);
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String action = req.getPathInfo() == null ? "/" : req.getPathInfo();

        if ("/save".equals(action)) {
            handleSaveJustification(req, resp);
        } else {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Action non reconnue.");
        }
    }

    private void handleSaveJustification(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        Users currentUser = (Users) req.getSession().getAttribute("currentUser");
        Part filePart = req.getPart("justificatifFile");
        String comment = req.getParameter("comment");

        if (filePart == null || filePart.getSize() == 0) {
            req.setAttribute("error", "Veuillez joindre un document justificatif.");
            req.getRequestDispatcher("/WEB-INF/views/student/justification.jsp").forward(req, resp);
            return;
        }

        try {
            String originalFileName = filePart.getSubmittedFileName();
            String extension = "";
            if (originalFileName != null && originalFileName.contains(".")) {
                extension = originalFileName.substring(originalFileName.lastIndexOf("."));
            }

            String uniqueFileName = "justif_" + UUID.randomUUID() + extension;

            String uploadDir = getServletContext().getInitParameter("uploadDirJustif");
            Path uploadPath = Paths.get(uploadDir);

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            Path targetFilePath = uploadPath.resolve(uniqueFileName);
            Files.copy(filePart.getInputStream(), targetFilePath, StandardCopyOption.REPLACE_EXISTING);

            String fileUrl = "justifications/" + uniqueFileName;

            justificationService.save(currentUser, fileUrl, comment);
            resp.sendRedirect(req.getContextPath() + "/etudiant/justification/liste?success=true");
        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("error", "Une erreur technique est survenue lors de l'envoi de votre fichier.");
            req.getRequestDispatcher("/WEB-INF/views/student/justification.jsp").forward(req, resp);
        }
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            super.service(req, resp);
        } catch (Exception e) {
            Throwable cause = e.getCause() != null ? e.getCause() : e;
            if (cause instanceof IllegalStateException || cause.getMessage() != null
                    && cause.getMessage().toLowerCase().contains("size")) {
                req.setAttribute("error", "Le fichier est trop volumineux. La taille maximale autorisée est de 10 Mo.");
                req.getRequestDispatcher("/WEB-INF/views/student/justification.jsp").forward(req, resp);
            } else {
                throw e;
            }
        }
    }
}