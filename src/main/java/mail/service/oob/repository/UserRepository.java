package mail.service.oob.repository;

import jakarta.transaction.Transactional;
import mail.service.oob.interfaces.UserLoginInfo;
import mail.service.oob.models.CustomUserModel;
import mail.service.oob.models.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository <UserModel, Long> {
    @Query(value = "SELECT * FROM (\n" +
            "SELECT \n" +
            " u.ID, \n" +
            " u.NAMA_PEMILIK_USAHA, \n" +
            " LOWER(u.EMAIL_PEMILIK_USAHA) as EMAIL_PEMILIK_USAHA,\n" +
            " u.NO_HANDPHONE,\n" +
            " a.IS_REMINDER,\n" +
            " a.IS_EXPIRED,\n" +
            " TRUNC(SYSDATE) - TRUNC(a.LAST_LOGIN) AS SELISIH\n" +
            "FROM \n" +
            " USER_OOB_TABLE u \n" +
            "LEFT JOIN \n" +
            " USER_OOB_TABLE_AUTH_DTL a ON u.ID = a.ID_USER_OOB \n" +
            "WHERE \n" +
            " a.LAST_LOGIN IS NOT NULL \n" +
            " AND u.EMAIL_PEMILIK_USAHA IS NOT NULL\n" +
            " AND u.ID_STATUS_PROSES = '3' \n" +
            " AND a.IS_REMINDER IS NULL )\n" +
            " WHERE rownum <= 200\n" +
            " AND SELISIH = ?1", nativeQuery = true)
    List<UserLoginInfo> findAllUserByLastLogin(Long Selisih);

    @Query(value = "SELECT * FROM (\n" +
            "SELECT \n" +
            " u.ID, \n" +
            " u.NAMA_PEMILIK_USAHA, \n" +
            " LOWER(u.EMAIL_PEMILIK_USAHA) as EMAIL_PEMILIK_USAHA,\n" +
            " u.NO_HANDPHONE,\n" +
            " a.IS_REMINDER,\n" +
            " a.IS_EXPIRED,\n" +
            " TRUNC(SYSDATE) - TRUNC(a.LAST_LOGIN) AS SELISIH\n" +
            "FROM \n" +
            " USER_OOB_TABLE u \n" +
            "LEFT JOIN \n" +
            " USER_OOB_TABLE_AUTH_DTL a ON u.ID = a.ID_USER_OOB \n" +
            "WHERE \n" +
            " a.LAST_LOGIN IS NOT NULL \n" +
            " AND u.EMAIL_PEMILIK_USAHA IS NOT NULL\n" +
            " AND u.ID_STATUS_PROSES = '3' \n" +
            " AND a.IS_REMINDER = 1 )\n" +
            " WHERE rownum <= 200\n" +
            " AND SELISIH = ?1", nativeQuery = true)
    List<UserLoginInfo> findAllUserByLastLogin90Days(Long Selisih);


    @Transactional
    @Modifying
    @Query(value = "UPDATE USER_OOB_TABLE_AUTH_DTL a SET LAST_LOGIN = LOCALTIMESTAMP, IS_FIRST_LOGIN = 1, IS_REMINDER = NULL, \n" +
            "PASSWORD = ?1, WRONG_PASSWORD_COUNT = 0, IS_LOCKED = 0, CREATE_DATE = LOCALTIMESTAMP \n" +
            "WHERE a.ID_USER_OOB = ?2", nativeQuery = true)
    void updatePasswordUser(String password, Long id);

    @Transactional
    @Modifying
    @Query(value = "UPDATE USER_OOB_TABLE  SET ID_STATUS_PROSES = ?1 WHERE ID = ?2", nativeQuery = true)
    void updateStatusById(String status, Long id);

    @Query(value = "SELECT \n" +
            "    u.ID, \n" +
            "    u.NAMA_PEMILIK_USAHA, \n" +
            "    LOWER(u.EMAIL_PEMILIK_USAHA) as EMAIL_PEMILIK_USAHA,\n" +
            "    u.NO_HANDPHONE,\n" +
            "    TRUNC(SYSDATE) - TRUNC(a.LAST_LOGIN) AS SELISIH\n" +
            "FROM \n" +
            "    USER_OOB_TABLE u \n" +
            "LEFT JOIN \n" +
            "    USER_OOB_TABLE_AUTH_DTL a ON u.ID = a.ID_USER_OOB \n" +
            "WHERE \n" +
            "    LOWER(u.EMAIL_PEMILIK_USAHA) = LOWER(?1) \n" +
            "    AND a.LAST_LOGIN IS NOT NULL \n" +
            "    AND u.EMAIL_PEMILIK_USAHA IS NOT NULL \n"+
            "    AND rownum <= 200", nativeQuery = true)
    UserLoginInfo findByEmail(String email);

    @Modifying
    @Transactional
    @Query(value = "UPDATE USER_OOB_TABLE_AUTH_DTL a SET IS_REMINDER = 1 WHERE a.ID_USER_OOB = ?1", nativeQuery = true)
    void updateIsReminder(Long id);

    @Query(value = "SELECT \n" +
            "    u.ID, \n" +
            "    u.NAMA_PEMILIK_USAHA, \n" +
            "    LOWER(u.EMAIL_PEMILIK_USAHA) as EMAIL_PEMILIK_USAHA,\n" +
            "    u.NO_HANDPHONE,\n" +
            "    TRUNC(SYSDATE) - TRUNC(a.LAST_LOGIN) AS SELISIH\n" +
            "FROM \n" +
            "    USER_OOB_TABLE u \n" +
            "LEFT JOIN \n" +
            "    USER_OOB_TABLE_AUTH_DTL a ON u.ID = a.ID_USER_OOB \n" +
            "WHERE \n" +
            "    LOWER(u.EMAIL_PEMILIK_USAHA) = LOWER(?1) \n" +
            "    AND a.LAST_LOGIN IS NOT NULL \n" +
            "    AND u.EMAIL_PEMILIK_USAHA IS NOT NULL \n"+
            "    AND rownum = 1", nativeQuery = true)
    UserLoginInfo findOneByEmail(String email);
}
