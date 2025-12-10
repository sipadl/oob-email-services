package mail.service.oob.service;

import mail.service.oob.config.SftpExcelReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class ExcelServices {

    @Value("${spring.sftp.host}")
    private String host;

    @Value("${spring.sftp.user}")
    private String user;

    @Value("${spring.sftp.password}")
    private String password;

    public List<Map<String, Object>> processExcelFiles() throws Exception {
        try {
            SftpExcelReader reader = new SftpExcelReader();
            // format tanggal sesuai yyyyMMdd (contoh: 20250923)
            String fileName = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".xlsx";
            return reader.readExcelFromSftp(
                    host,                   // host
                    22,                     // port
                    user,                   // user
                    password,               // password
                    "/MTI/DEV/OOB/batch",   // folder path
                    fileName                // filename
            );

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
