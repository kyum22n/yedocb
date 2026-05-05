import org.apache.ibatis.annotations.Mapper;
import java.util.List;
import com.example.demo.entity.Admin;

/**
 * 파일명: AdminDao.java
 * 설명: 관리자 정보 관련 dao
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-05-02 | 규민 | 메소드 수정
 */

@Mapper
public interface AdminDao {

    // 모든 관리자 목록 조회
	public List<Admin> selectAllAdmins();
	// 관리자 1명 조회
	public Admin selectAdminByAId(int aId);
	// 관리자 등록
	public int insertAdmin(Admin admin);
    // 관리자 정보 수정
    public int updateAdmin(Admin admin);
	// 관리자 삭제
	public int deleteAdmin(int aId);

    // 로그인 ID로 관리자 조회
    public Admin selectAdminByLoginId(String aLoginId);
	// 관리자 이메일로 관리자 아이디 찾기
	public String selectAdminIdByEmail(String aEmail);
	// 관리자 비밀번호 변경
	public int updatePassword(Admin admin);
}
