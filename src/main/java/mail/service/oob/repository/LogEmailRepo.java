package mail.service.oob.repository;

import mail.service.oob.models.LogEmailModels;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LogEmailRepo extends JpaRepository <LogEmailModels, Long> {
}
