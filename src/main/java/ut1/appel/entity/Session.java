package ut1.appel.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Session {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate sessionDate;

    @Column(columnDefinition = "TIME")
    private LocalTime startTime;

    @Column(columnDefinition = "TIME")
    private LocalTime endTime;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private Users teacher;

    @OneToOne(mappedBy = "session", cascade = CascadeType.ALL)
    private AttendanceSheet attendanceSheet;

    @ManyToMany
    @JoinTable(
            name = "session_classes",
            joinColumns = @JoinColumn(name = "session_id"),
            inverseJoinColumns = @JoinColumn(name = "class_id")
    )
    private Set<StudentClass> studentClasses;

    @ManyToMany
    @JoinTable(
            name = "session_groups",
            joinColumns = @JoinColumn(name = "session_id"),
            inverseJoinColumns = @JoinColumn(name = "group_id")
    )
    private Set<StudentGroup> studentGroups;
}