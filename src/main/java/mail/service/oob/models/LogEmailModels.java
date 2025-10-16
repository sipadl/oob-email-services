package mail.service.oob.models;

import jakarta.persistence.*;
import lombok.Data;
import org.w3c.dom.Text;

import java.sql.Timestamp;

@Data
@Entity
@Table(name = "EMAIL_LOG_TABLE")
public class LogEmailModels {
    @Id
    @Column(name = "LOG_ID")
    private Long id;

    @Column(name = "LOG_STATUS")
    private String logStatus;

    @Column(name = "LOG_SUBJECT")
    private String logSubject;

    @Column(name = "LOG_TGL_WAKTU")
    private Timestamp logTglWaktu;

    @Column(name = "LOG_TUJUAN")
    private String logTujuan;

    @Column(name = "LOG_USER")
    private String logUser;
}
