package mail.service.oob.models;

import org.springframework.data.relational.core.mapping.Column;

import java.sql.Timestamp;

public class CustomUserModel {
    @Column("SELISIH")
    private Long selisih;

    @Column("EMAIL_PEMILIK_USAHA")
    private String email;

    @Column("IS_FIRST_LOGIN")
    private long isFirstLogin;

    @Column("IS_LOCKED")
    private Long isLocked;

    @Column("LAST_LOGIN")
    private Timestamp lastLogin;

    @Column("WRONG_PASSWORD_COUNT")
    private Long wrongPasswordCount;

    @Column("SEND_MAIL_COUNT")
    private Long sendMailCount;
}
