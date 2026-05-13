package ut1.appel.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ut1.appel.enums.Role;


@Getter
@Setter
@NoArgsConstructor
@Entity
public class Users {

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

    @ManyToOne
    @JoinColumn(name = "class_id")
    private StudentClass studentClass;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private StudentGroup studentGroup;

    private String picturePath;

    public boolean isAdmin() { return role == Role.ADMIN; }
    public boolean isPending() { return role == Role.PENDING; }
    public boolean isEtudiantFI() { return role == Role.ETUDIANT_FI; }
    public boolean isEtudiantFA() { return role == Role.ETUDIANT_FA; }
    public boolean isScolarite() { return role == Role.SCOLARITE; }
    public boolean isEnseignant() { return role == Role.ENSEIGNANT; }
}