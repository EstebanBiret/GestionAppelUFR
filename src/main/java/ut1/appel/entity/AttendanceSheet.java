package ut1.appel.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceSheet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Boolean isValidated;
    private LocalDateTime validationDate;

    @ManyToOne
    @JoinColumn(name = "session_id")
    private Session sessionId;

    @OneToMany(mappedBy = "", cascade = CascadeType.ALL)
    private List<AttendanceRow> attendanceRows;
}