import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import com.example.demo.dao.AdminDao;
import com.example.demo.entity.Admin;

/**
 * 파일명: AdminService.java
 * 설명: 관리자 정보 관련 service
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-05-02 | 규민 | 로그인 메소드 분리
 */

@Service
public class AdminService {

    @Autowired
    private AdminDao adminDao;

    // 관리자 목록 조회
    public List<Admin> getAllAdmins() {
        return adminDao.selectAllAdmins();
    }

    // 관리자 1명 조회
    public Admin getAdminByAId(int aId) {
        return adminDao.selectAdminByAId(aId);
    }

    // 관리자 등록
    public int createAdmin(Admin admin) {
        return adminDao.insertAdmin(admin);
    }

    // 관리자 정보 수정
    public int modifyAdmin(Admin admin) {
        return adminDao.updateAdmin(admin);
    }

    // 관리자 삭제
    public int removeAdmin(int aId) {
        return adminDao.deleteAdmin(aId);
    }

}
