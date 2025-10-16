package mail.service.oob.config;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.InputStream;
import java.util.*;

public class SftpExcelReader {

    public List<Map<String, Object>> readExcelFromSftp(
            String host, int port, String user, String password,
            String folder, String fileName) throws Exception {

        List<Map<String, Object>> dataList = new ArrayList<>();

        JSch jsch = new JSch();
        Session session = jsch.getSession(user, host, port);
        session.setPassword(password);
        session.setConfig("StrictHostKeyChecking", "no");
        session.connect();

        Channel channel = session.openChannel("sftp");
        channel.connect();
        ChannelSftp sftpChannel = (ChannelSftp) channel;

        // Baca file spesifik
        String filePath = folder + "/" + fileName;
        try (InputStream inputStream = sftpChannel.get(filePath);
             Workbook workbook = new XSSFWorkbook(inputStream)) {

            Sheet sheet = workbook.getSheetAt(0);

            // header di row pertama
            Row headerRow = sheet.getRow(0);
            List<String> headers = new ArrayList<>();
            for (Cell cell : headerRow) {
                headers.add(cell.getStringCellValue());
            }

            // data mulai row kedua
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                Map<String, Object> rowData = new LinkedHashMap<>();
                for (int j = 0; j < headers.size(); j++) {
                    Cell cell = row.getCell(j);
                    Object value = null;
                    if (cell != null) {
                        switch (cell.getCellType()) {
                            case STRING -> value = cell.getStringCellValue();
                            case NUMERIC -> value = cell.getNumericCellValue();
                            case BOOLEAN -> value = cell.getBooleanCellValue();
                            default -> value = null;
                        }
                    }
                    rowData.put(headers.get(j), value);
                }
                dataList.add(rowData);
            }
        }

        sftpChannel.exit();
        session.disconnect();

        return dataList;
    }
}
