package ut1.appel.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceRow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Enum status; // Ex: PRESENT, ABSENT, RETARD
    private Boolean changedGroup;

    // L'élève concerné
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "attendance_sheet_id")
    private AttendanceSheet attendanceSheet;

    @ManyToOne
    @JoinColumn(name = "justification_id")
    private Justification justification;
}