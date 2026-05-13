package ut1.appel.filter;

import ut1.appel.enums.Role;
import ut1.appel.entity.Users;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

@WebFilter("/*")
public class AuthFilter implements Filter {

    private static final Map<String, Set<Role>> ACCESS_RULES = Map.of(
            "/admin",      Set.of(Role.ADMIN),
            "/scolarite",  Set.of(Role.SCOLARITE, Role.ADMIN),
            "/enseignant", Set.of(Role.ENSEIGNANT, Role.ADMIN),
            "/etudiant",   Set.of(Role.ETUDIANT_FA, Role.ETUDIANT_FI, Role.ADMIN)
    );

    public static void redirectByRole(Role role, HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        String ctx = req.getContextPath();
        switch (role) {
            case ADMIN                    -> resp.sendRedirect(ctx + "/admin");
            case ENSEIGNANT               -> resp.sendRedirect(ctx + "/enseignant");
            case ETUDIANT_FI, ETUDIANT_FA -> resp.sendRedirect(ctx + "/etudiant");
            case SCOLARITE                -> resp.sendRedirect(ctx + "/scolarite");
            case PENDING                  -> resp.sendRedirect(ctx + "/auth/pending");
        }
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest  request  = (HttpServletRequest)  req;
        HttpServletResponse response = (HttpServletResponse) res;

        String path = request.getServletPath()
                + (request.getPathInfo() == null ? "" : request.getPathInfo());

        Users user = getSessionUser(request);

        if (path.startsWith("/css") || path.startsWith("/js") || path.startsWith("/img") || path.startsWith("/images")) {
            chain.doFilter(req, res);
            return;
        }

        if (path.startsWith("/test")) {
            chain.doFilter(req, res);
            return;
        }

        if (path.startsWith("/auth/")) {
            if (path.equals("/auth/logout")) {
                chain.doFilter(req, res);
                return;
            }
            if (user == null) {
                chain.doFilter(req, res);
                return;
            }
            if (user.getRole() != Role.PENDING
                    && (path.equals("/auth/login")
                    || path.equals("/auth/register"))) {

                redirectByRole(user.getRole(), request, response);
                return;
            }
            chain.doFilter(req, res);
            return;
        }

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/auth/login");
            return;
        }

        if (user.getRole() == Role.PENDING) {
            response.sendRedirect(request.getContextPath() + "/auth/pending");
            return;
        }

        for (Map.Entry<String, Set<Role>> rule : ACCESS_RULES.entrySet()) {
            if (path.startsWith(rule.getKey()) && !rule.getValue().contains(user.getRole())) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Accès non autorisé");
                return;
            }
        }

        chain.doFilter(req, res);
    }

    private Users getSessionUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return (session != null) ? (Users) session.getAttribute("currentUser") : null;
    }
}