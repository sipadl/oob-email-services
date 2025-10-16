package mail.service.oob.utils;

import java.security.SecureRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Helper {
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String DIGITS = "0123456789";
    private static final String SPECIAL = "!@#$%&*()-_=+";

    /**
     * Masking nomor HP seperti contoh: "0822*****912"
     * - Jika panjang >= 7: tampilkan 4 digit pertama, 3 digit terakhir, sisanya diganti '*'
     * - Jika panjang antara 5..6: tampilkan 2 digit pertama dan 1 digit terakhir, sisanya '*'
     * - Jika panjang <=4: ganti semua menjadi '*'
     */
    public static String maskPhone(String phone) {
        if (phone == null) return null;
        String digits = phone.trim();
        int len = digits.length();

        if (len <= 4) {
            return "*".repeat(len);
        }

        if (len >= 7) {
            String first = digits.substring(0, 4);
            String last = digits.substring(len - 3);
            int middleCount = len - 7; // bagian yang dimask
            return first + "*".repeat(Math.max(1, middleCount + 1)) + last;
            // Note: +1 supaya setidaknya ada beberapa bintang jika len==7
        } else {
            // len == 5 or 6
            String first = digits.substring(0, 2);
            String last = digits.substring(len - 1);
            int middleCount = len - 3;
            return first + "*".repeat(Math.max(1, middleCount)) + last;
        }
    }

    /**
     * Generate temporary password random.
     * Default: length 8, menggunakan uppercase + digits.
     * Bisa request includeLower or includeSpecial.
     */
    public static String generateTemporaryPassword(int length, boolean includeLower, boolean includeSpecial) {
        if (length < 4) throw new IllegalArgumentException("length must be >= 4");
        StringBuilder pool = new StringBuilder();
        pool.append(UPPER).append(DIGITS);
        if (includeLower) pool.append(LOWER);
        if (includeSpecial) pool.append(SPECIAL);

        String charPool = pool.toString();

        // ensure at least one from mandatory groups: UPPER and DIGITS
        StringBuilder pass = new StringBuilder(length);
        pass.append(randomCharFrom(UPPER, RANDOM));
        pass.append(randomCharFrom(DIGITS, RANDOM));
        if (includeLower) pass.append(randomCharFrom(LOWER, RANDOM));
        if (includeSpecial) pass.append(randomCharFrom(SPECIAL, RANDOM));

        // fill remaining
        int remaining = length - pass.length();
        for (int i = 0; i < remaining; i++) {
            pass.append(randomCharFrom(charPool, RANDOM));
        }

        // shuffle result to avoid predictable prefix order
        String shuffled = shuffleString(pass.toString());
        return shuffled;
    }

    // Overload: default 8 chars, uppercase+digits (mirip contoh)
    public static String generateTemporaryPassword() {
        return generateTemporaryPassword(8, false, false);
    }

    // Helpers
    private static char randomCharFrom(String s, SecureRandom rand) {
        return s.charAt(rand.nextInt(s.length()));
    }

    private static String shuffleString(String input) {
        return IntStream.range(0, input.length())
                .mapToObj(i -> input.charAt(i))
                .collect(Collectors.collectingAndThen(
                        Collectors.toList(),
                        list -> {
                            java.util.Collections.shuffle(list, RANDOM);
                            return list.stream().map(String::valueOf).collect(Collectors.joining());
                        }
                ));
    }

    // Simple demo main
    public static void main(String[] args) {
        String hp = "08221234912";
        String masked = maskPhone(hp);
        String tempPass1 = generateTemporaryPassword(); // default 8 chars uppercase+digits
        String tempPass2 = generateTemporaryPassword(10, true, true); // contoh 10 chars with lower+special

        System.out.println("Original: " + hp);
        System.out.println("Masked  : " + masked);
        System.out.println("TempPw1 : " + tempPass1);
        System.out.println("TempPw2 : " + tempPass2);

        // Contoh pakai dengan template
        String templateActivation = "Hai %s,\n\nUsername: %s\nPassword sementara: %s\n\nSalam,\nYokke";
        String body = String.format(templateActivation, "Bambang", masked, tempPass1);
        System.out.println("\nEmail body:\n" + body);
    }

    public static String template85Days(String namaPemilikUsaha) {
        return String.format("""
        <body style="font-family: Arial, sans-serif;">
          <table width="100%%" cellpadding="0" cellspacing="0">
            <tr>
              <td align="start">
                <table width="600" cellpadding="0" cellspacing="0" style="background-color: #ffffff; border-radius: 8px; padding: 20px;">
                  <tr>
                    <td style="font-size: 16px; color: #333333;">
                      <p>Hai %s,</p>
                      <p>
                        Password Akun Livin’ Usaha 5 hari lagi akan kadaluarsa. Untuk keamanan akun anda segera Klik link berikut untuk perbaharui password:
                      </p>
                        <a href="https://qris.bankmandiri.co.id/">
                          https://qris.bankmandiri.co.id/changePassword
                        </a>
                      <p>Salam,<br/>Yokke</p>
                    </td>
                  </tr>
                </table>
              </td>
            </tr>
          </table>
        </body>
        """, namaPemilikUsaha);
    }

    public static String templateActivation(String namaNasabah, String username, String tempPassword, String url) {
        return String.format("""
        <div>Hai, %s,</div>
        <br>
        <div>Password Akun Livin’ Usaha Anda perlu di perbaharui. Klik link berikut untuk melakukan aktivasi ulang akun Anda:</div>
        <br>
        <div><a href="https://qris.bankmandiri.co.id/login">https://qris.bankmandiri.co.id/login</a></div>
        <br>
        <div>dan login dengan password sementara berikut:</div>
        <div><b>username :</b> %s</div>
        <div><b>password sementara :</b> %s</div>
        <br>
        <div>Link dan password sementara ini berlaku selama 2x24 jam. Setelah login, mohon ubah password sesuai dengan kriteria keamanan kami dan mudah Anda ingat.</div>
        <br>
        <div>Klik link berikut jika password sementara tidak berlaku: 
            <a href="%s">
                https://qris.bankmandiri.co.id/changePassword
            </a>
        </div>
        <br>
        <div>Salam,</div>
        <div>Yokke</div>
        """, namaNasabah, username, tempPassword, url);
    }

}
