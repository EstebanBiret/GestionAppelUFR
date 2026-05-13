package ut1.appel.entity;

import jakarta.persistence.*;
import ut1.appel.enums.AttendanceRowStatus;
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

    @Enumerated(EnumType.STRING)
    private AttendanceRowStatus status;

    private Boolean changedGroup;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users user;

    @ManyToOne
    @JoinColumn(name = "attendance_sheet_id")
    private AttendanceSheet attendanceSheet;

    @ManyToOne
    @JoinColumn(name = "justification_id")
    private Justification justification;
}