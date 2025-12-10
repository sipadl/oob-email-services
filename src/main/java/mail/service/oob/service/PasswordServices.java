package mail.service.oob.service;

import com.google.gson.Gson;
import mail.service.oob.interfaces.UserLoginInfo;
import mail.service.oob.models.EncryptDataFiller;
import mail.service.oob.models.LinkChangePassDto;
import mail.service.oob.models.LogEmailModels;
import mail.service.oob.repository.LogEmailRepo;
import mail.service.oob.repository.UserRepository;
import mail.service.oob.utils.Helper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class PasswordServices extends Helper {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailServices emailServices;

    @Autowired
    private LogEmailRepo logEmailRepo;

    @Value("${oob_dns}")
    protected  String OOB_DNS;

    public List<?> checkUserLastLogin85Days(Long value, Long type) {
        List<UserLoginInfo> data = userRepository.findAllUserByLastLogin(value);
        List<?> result = data.stream()
                .filter(item -> item.getSelisih() >= value
                ).map(items -> {
                   this.updateBulkPassword(items, type);
                   return items;
                }).toList();
        int userProcess = result.size();
        System.out.println("total user job 85 days processed: " + userProcess);
        return result;
    }

    public List<?> checkUserLastLogin90Days(Long value, Long type) {
        List<UserLoginInfo> data = userRepository.findAllUserByLastLogin90Days(value);
        List<?> result = data.stream()
                .filter(item -> item.getSelisih() >= value
                ).map(items -> {
                    this.updateBulkPassword(items, type);
                    return items;
                }).toList();

        int userProcess = result.size();
        System.out.println("total user job 90 days processed: " + userProcess);
        return result;
    }

    protected String getSaltString(Integer length) {
        String[] saltResult = new String[length];
        String resultSalt = "";

        for(int i = 0; i < saltResult.length; ++i) {
            String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
            StringBuilder salt = new StringBuilder();
            Random rnd = new Random();

            while(salt.length() < 5) {
                int index = (int)(rnd.nextFloat() * (float)SALTCHARS.length());
                salt.append(SALTCHARS.charAt(index));
            }

            resultSalt = resultSalt + salt.toString();
            if (i != saltResult.length - 1) {
                resultSalt = resultSalt + "-";
            }
        }

        return resultSalt;
    }

    public void updateBulkPassword(UserLoginInfo values, Long type) {
        try {
            String nomerHandphone = Helper.maskPhone(values.getNoHandphone());
            String tempPassword = Helper.generateTemporaryPassword(10, true, true);
            if (type.equals(1L)) {
                String body = Helper.template85Days(values.getNamaPemilikUsaha());
                emailServices.sendSimpleEmail(values.getEmailPemilikUsaha(), "Pemberitahuan Akun Livin Usaha", body);
                userRepository.updateIsReminder(values.getId());
            } else if (type.equals(2L)) {

                LinkChangePassDto linkChangePassDto = new LinkChangePassDto();
                linkChangePassDto.setId(values.getId());
                linkChangePassDto.setDate(new SimpleDateFormat("ddMMyyyyHHmmss").format(new Date()));
                String secretKey = getSaltString(4).replace("-", "").toUpperCase();
                String encResult = EncryptDataFiller.encrypt(new Gson().toJson(linkChangePassDto), secretKey);
                String randomNumb = String.valueOf(ThreadLocalRandom.current().nextInt(1000, 10000 +1));
                String codeEncode = values.getId() + "|" + randomNumb;
                String secureToken = Base64.getEncoder().encodeToString(codeEncode.getBytes());


                String urlUbahPassword = OOB_DNS + "/forgetPassword?_tracQ=" + encResult + "&_sk=" + secretKey + "&_inv=" +  secureToken;

                String body = Helper.templateActivation(values.getNamaPemilikUsaha(), nomerHandphone, tempPassword, urlUbahPassword);
                emailServices.sendSimpleEmail(values.getEmailPemilikUsaha(), "Perubahan Password Livin Usaha", body);
                userRepository.updatePasswordUser(tempPassword, values.getId());
                userRepository.updateStatusById("-2", values.getId());
            }
            ;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void updateUserOneByOne(String email) {
        try {
<<<<<<< HEAD
            UserLoginInfo values = userRepository.findByEmail(email);
            System.out.println(values);
=======
            UserLoginInfo values = userRepository.findOneByEmail(email);
>>>>>>> 669fac641581d57a8e0595334726588fd0cac92a
            if(values == null ) {
                throw new RuntimeException("Email Not Found");
            }
            String nomerHandphone = Helper.maskPhone(values.getNoHandphone());
            String tempPassword = Helper.generateTemporaryPassword(10, true, true);

            LinkChangePassDto linkChangePassDto = new LinkChangePassDto();
            linkChangePassDto.setId(values.getId());
            linkChangePassDto.setDate(new SimpleDateFormat("ddMMyyyyHHmmss").format(new Date()));
            String secretKey = getSaltString(4).replace("-", "").toUpperCase();
            String encResult = EncryptDataFiller.encrypt(new Gson().toJson(linkChangePassDto), secretKey);

            String randomNumb = String.valueOf(ThreadLocalRandom.current().nextInt(1000, 10000 +1));
            String codeEncode = values.getId() + "|" + randomNumb;
            String secureToken = Base64.getEncoder().encodeToString(codeEncode.getBytes());

            String urlUbahPassword = OOB_DNS + "/forgetPassword?_tracQ=" + encResult + "&_sk=" + secretKey + "&_inv=" +  secureToken;
            String body = Helper.templateActivation(values.getNamaPemilikUsaha(), nomerHandphone, tempPassword, urlUbahPassword);
            emailServices.sendSimpleEmail(values.getEmailPemilikUsaha(), "Perubahan Password Livin Usaha", body);
            userRepository.updatePasswordUser(tempPassword, values.getId());

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
