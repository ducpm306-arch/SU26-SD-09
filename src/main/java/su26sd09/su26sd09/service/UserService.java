package su26sd09.su26sd09.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import su26sd09.su26sd09.entity.NguoiDung;
import su26sd09.su26sd09.entity.VaiTro;
import su26sd09.su26sd09.repository.NguoiDungRepository;
import su26sd09.su26sd09.repository.VaiTroRepo;

import java.text.Normalizer;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

@Service
public class UserService {

    private static final Pattern DIACRITICS = Pattern.compile("\\p{M}+");

    @Autowired
    NguoiDungRepository repo;

    @Autowired
    VaiTroRepo vaiTroRepo;

    public List<NguoiDung> getAll(){
        return repo.findAll();
    }

    public NguoiDung Getbyid(int id){
        return repo.findById(id).orElse(null);
    }

    public NguoiDung findByEmail(String email) {
        if (email == null || email.isBlank()) {
            return null;
        }
        return repo.findByEmail(email);
    }

    public List<NguoiDung> search(String keyword) {
        List<NguoiDung> nguoiDungs = repo.findAll();
        if (keyword == null || keyword.isBlank()) {
            return nguoiDungs;
        }

        String q = keyword.toLowerCase(Locale.ROOT);
        return nguoiDungs.stream()
                .filter(nd -> contains(nd.getHoTen(), q)
                        || contains(nd.getEmail(), q)
                        || contains(nd.getSoDienThoai(), q)
                        || (nd.getVaiTro() != null && contains(nd.getVaiTro().getTen_VaiTro(), q)))
                .toList();
    }

    public List<NguoiDung> searchKhachHang(String keyword) {
        List<NguoiDung> khachHangs = repo.findAll()
                .stream()
                .filter(nd -> nd.getVaiTro() != null && isKhachHangRole(nd.getVaiTro().getId(), nd.getVaiTro().getTen_VaiTro()))
                .toList();

        if (keyword == null || keyword.isBlank()) {
            return khachHangs;
        }

        String q = keyword.toLowerCase(Locale.ROOT);
        return khachHangs.stream()
                .filter(nd -> contains(nd.getHoTen(), q)
                        || contains(nd.getEmail(), q)
                        || contains(nd.getSoDienThoai(), q))
                .toList();
    }

    public List<VaiTro> getAllVaiTro() {
        return vaiTroRepo.findAll();
    }

    public List<NguoiDung> getNhanVienUsers() {
        return repo.findAll()
                .stream()
                .filter(nd -> nd.getVaiTro() != null && isNhanVienRole(nd.getVaiTro().getTen_VaiTro()))
                .toList();
    }

    public void remove(NguoiDung nguoiDung){
        repo.delete(nguoiDung);
    }

    public void save(NguoiDung nguoiDung){
        repo.save(nguoiDung);
    }

    public void updateStatusAndRole(int userId, boolean trangThai, Integer vaiTroId) {
        NguoiDung nguoiDung = Getbyid(userId);
        if (nguoiDung == null) {
            return;
        }

        nguoiDung.setTrangThai(trangThai);
        if (vaiTroId != null) {
            vaiTroRepo.findById(vaiTroId).ifPresent(nguoiDung::setVaiTro);
        }
        repo.save(nguoiDung);
    }

    private boolean contains(String value, String keyword) {
        return value != null && value.toLowerCase(Locale.ROOT).contains(keyword);
    }

    private boolean isNhanVienRole(String roleName) {
        if (roleName == null) {
            return false;
        }

        String value = normalizeRoleName(roleName);
        return value.contains("nhan vien")
                || value.contains("nhanvien")
                || value.contains("staff")
                || value.contains("employee");
    }

    private boolean isKhachHangRole(Integer roleId, String roleName) {
        if (roleId != null && roleId == 3) {
            return true;
        }
        if (roleName == null) {
            return false;
        }

        String value = normalizeRoleName(roleName);
        return value.contains("khach")
                || value.contains("customer")
                || value.contains("client");
    }

    private String normalizeRoleName(String roleName) {
        String decomposed = Normalizer.normalize(roleName, Normalizer.Form.NFD);
        return DIACRITICS.matcher(decomposed).replaceAll("").toLowerCase(Locale.ROOT);
    }
}
