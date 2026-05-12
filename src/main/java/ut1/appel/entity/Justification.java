package ut1.appel.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;
import java.util.List;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Justification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileUrl; // Le chemin du fichier (ex: le PDF du médecin)
    private LocalDate depositDate;
    private String status; // Ex: EN_ATTENTE, VALIDE, REFUSE

    // Le justificatif a été déposé par UN utilisateur
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "justification")
    private List<AttendanceRow> attendanceLines;
}