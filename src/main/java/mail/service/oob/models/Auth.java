package mail.service.oob.models;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;

@Data
@Entity
@Table(name = "USER_OOB_TABLE_AUTH_DTL")
public class Auth {

    @Id
    @Column(name = "ID_USER_OOB")
    private Long idOob;

    @Column(name = "IS_FIRST_LOGIN")
    private long isFirstLogin;

    @Column(name = "IS_LOCKED")
    private Long isLocked;

    @Column(name = "LAST_LOGIN")
    private Timestamp lastLogin;

    @Column(name = "WRONG_PASSWORD_COUNT")
    private Long wrongPasswordCount;

    @Column(name = "SEND_MAIL_COUNT")
    private Long sendMailCount;
}
