package mail.service.oob.interfaces;

public interface UserLoginInfo {
    Long getId();
    String getEmailPemilikUsaha();
    String getNamaPemilikUsaha();
    String getNoHandphone();
    Long getSelisih(); // atau tipe data yang sesuai dengan hasil pengurangan
    Long getIsReminder();
    Long getIsExpired();
    // Tambahkan semua getter untuk kolom 'a.*' yang Anda butuhkan
}