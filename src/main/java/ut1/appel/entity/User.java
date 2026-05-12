package ut1.appel.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ut1.appel.enums.Role;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.PENDING;

    @Column(nullable = false)
    private String photoPath;

    @ManyToOne
    @JoinColumn(name = "class_id")
    private StudentClass studentClassId;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private StudentGroup studentGroupId;

    public boolean isAdmin() { return role == Role.ADMIN; }
    public boolean isPending() { return role == Role.PENDING; }
    public boolean isEtudiantFI() { return role == Role.ETUDIANT_FI; }
    public boolean isEtudiantFA() { return role == Role.ETUDIANT_FA; }
    public boolean isScolarite() { return role == Role.SCOLARITE; }
    public boolean isEnseignant() { return role == Role.ENSEIGNANT; }
}