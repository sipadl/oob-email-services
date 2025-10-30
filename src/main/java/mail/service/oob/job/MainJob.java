package mail.service.oob.job;

import mail.service.oob.controller.MainController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class MainJob {

    @Autowired
    private MainController mainController;

    @Scheduled(cron = "#{@environment.getProperty('job.mandiri','0 0 */1 * * *')}")
    public void mainJob11Malem () {
        mainController.checkUserValid();
    }

    @Scheduled(cron = "#{@environment.getProperty('job.reminder','0 0 22 * * *')}")
    public void mainJobCisoMandiri() throws Exception {
        mainController.readExcel();
    }
}
