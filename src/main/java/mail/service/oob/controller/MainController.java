package mail.service.oob.controller;

import mail.service.oob.interfaces.UserLoginInfo;
import mail.service.oob.service.EmailServices;
import mail.service.oob.service.ExcelServices;
import mail.service.oob.service.PasswordServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class MainController {

    private final EmailServices emailService;

    @Autowired
    public ExcelServices excelServices;

    @Autowired
    public MainController(EmailServices emailService) {
        this.emailService = emailService;
    }

    @Autowired
    public PasswordServices passwordServices;

    @GetMapping("/send-test-email")
    public String sendTestEmail() {
        String to = "fadel.mm01@gmail.com";
        String subject = "Spring Boot Email Test";
        String body = "This is a test email sent from your Spring Boot application.";

        try {
            emailService.sendSimpleEmail(to, subject, body);
            return "Email sent successfully to " + to;
        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to send email: " + e.getMessage();
        }
    }

    @GetMapping("job/1")
    public void checkUserValid() {
        passwordServices.checkUserLastLogin85Days(85L, 1L);
        passwordServices.checkUserLastLogin90Days(90L, 2L);
    }

    @GetMapping("job/2")
    public void readExcel() throws Exception {
       List<Map<String, Object>> data = excelServices.processExcelFiles();
       data.forEach(items -> {
           passwordServices.updateUserOneByOne((String) items.get("identity"));
       });
       excelServices.renameExcelAfterProcess();
    }
}
