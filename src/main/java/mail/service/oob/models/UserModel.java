package mail.service.oob.models;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "USER_OOB_TABLE")
public class UserModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "EMAIL_PEMILIK_USAHA")
    private String email;

    @ManyToOne
    @JoinColumn(name = "ID_USER_OOB")
    private Auth auth;
}
