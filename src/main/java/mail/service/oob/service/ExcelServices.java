package mail.service.oob.service;

import com.jcraft.jsch.*;
import mail.service.oob.config.SftpExcelReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;

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

    public void renameExcelAfterProcess() {
        SftpExcelReader reader = new SftpExcelReader();
        String remoteFolderPath = "/MTI/DEV/OOB/batch";

        // Nama file asli (e.g., "20250923.xlsx")
        String originalFileName = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".xlsx";

        // Nama file baru setelah diproses (e.g., "20250923_PROCESSED.xlsx")
        String newFileName = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "_PROCESSED.xlsx";

        try {
            // Panggilan ke metode yang baru saja kita buat
            renameFileOnSftp(
                    host,                       // host
                    22,                         // port
                    user,                       // user
                    password,                   // password
                    remoteFolderPath,           // folder path
                    originalFileName,           // nama file lama
                    newFileName                 // nama file baru
            );
            System.out.println("Berhasil me-rename file: " + originalFileName + " -> " + newFileName);
        } catch (Exception e) {
            // Tangani atau lempar RuntimeException jika gagal
            throw new RuntimeException("Gagal mengganti nama file SFTP: " + originalFileName, e);
        }
    }

    public void renameFileOnSftp(
            String host,
            int port,
            String user,
            String password,
            String remoteDir,
            String oldFilename,
            String newFilename) throws Exception {

        Session session = null;
        ChannelSftp channelSftp = null;

        try {
            JSch jsch = new JSch();

            // 1. Setup Session
            session = jsch.getSession(user, host, port);
            session.setPassword(password);

            // Konfigurasi untuk menonaktifkan pemeriksaan kunci host.
            // PENTING: Untuk PRODUKSI, pertimbangkan menggunakan known_hosts yang benar.
            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);

            // 2. Connect Session
            session.connect();

            // 3. Open SFTP Channel
            Channel channel = session.openChannel("sftp");
            channel.connect();
            channelSftp = (ChannelSftp) channel;

            // Buat path lengkap untuk file lama dan baru
            // ChannelSftp.rename memerlukan path lengkap dari root
            String oldPath = remoteDir + "/" + oldFilename;
            String newPath = remoteDir + "/" + newFilename;

            // 4. Melakukan operasi rename (setara dengan mv di Linux)
            channelSftp.rename(oldPath, newPath);

        } catch (JSchException | SftpException e) {
            // Tangani error spesifik SFTP
            throw new Exception("Gagal mengganti nama file di SFTP: " + oldFilename, e);
        } finally {
            // 5. Cleanup Resources
            if (channelSftp != null && channelSftp.isConnected()) {
                channelSftp.exit();
            }
            if (session != null && session.isConnected()) {
                session.disconnect();
            }
        }
    }

}
