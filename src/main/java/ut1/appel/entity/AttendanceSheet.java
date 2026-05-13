package ut1.appel.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
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

    private LocalDate validationDate;

    @OneToOne
    @JoinColumn(name = "session_id", unique = true)
    private Session session;

    @OneToMany(mappedBy = "attendanceSheet", cascade = CascadeType.ALL)
    private List<AttendanceRow> attendanceRows;
}