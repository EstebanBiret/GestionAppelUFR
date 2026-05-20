package ut1.appel.servlet;

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
import java.util.UUID;

@WebServlet("/etudiant/justification")
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
        req.getRequestDispatcher("/WEB-INF/views/student/justification.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        Users currentUser = (Users) req.getSession().getAttribute("currentUser");

        Part filePart = req.getPart("justificatifFile");

        // NOUVEAU : Récupération du commentaire
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

            String fileUrl = "justificatifs/" + uniqueFileName;

            justificationService.declareAbsence(currentUser, fileUrl, comment);

            req.setAttribute("success", "Votre justificatif a été transmis avec succès à la scolarité.");
            req.getRequestDispatcher("/WEB-INF/views/student/justification.jsp").forward(req, resp);

        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("error", "Une erreur technique est survenue lors de l'envoi de votre fichier.");
            req.getRequestDispatcher("/WEB-INF/views/student/justification.jsp").forward(req, resp);
        }
    }
}