package ut1.appel.entity;

import jakarta.persistence.*;
import ut1.appel.enums.Role;

@Entity
@Table(name = "user")
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

    public Long getId() { return id; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public boolean isAdmin() { return role == Role.ADMIN; }
    public boolean isPending() { return role == Role.PENDING; }
    public boolean isEtudiantFI() { return role == Role.ETUDIANT_FI; }
    public boolean isEtudiantFA() { return role == Role.ETUDIANT_FA; }
    public boolean isScolarite() { return role == Role.SCOLARITE; }
    public boolean isEnseignant() { return role == Role.ENSEIGNANT; }
}