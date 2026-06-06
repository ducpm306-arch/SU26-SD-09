package su26sd09.su26sd09.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import su26sd09.su26sd09.entity.Nhanvien;
import su26sd09.su26sd09.repository.NhanVienRepository;

import java.util.List;
import java.util.Locale;

@Service
public class NhanVienService {

    @Autowired
    private NhanVienRepository nhanVienRepository;

    public List<Nhanvien> search(String keyword) {
        List<Nhanvien> nhanViens = nhanVienRepository.findAll();
        if (keyword == null || keyword.isBlank()) {
            return nhanViens;
        }

        String q = keyword.toLowerCase(Locale.ROOT);
        return nhanViens.stream()
                .filter(nv -> contains(String.valueOf(nv.getId()), q)
                        || contains(nv.getBoPhan(), q)
                        || contains(nv.getCaLam(), q)
                        || (nv.getN() != null && contains(nv.getN().getHoTen(), q))
                        || (nv.getN() != null && contains(nv.getN().getEmail(), q))
                        || (nv.getN() != null && contains(nv.getN().getSoDienThoai(), q)))
                .toList();
    }

    public Nhanvien findById(int id) {
        return nhanVienRepository.findById(id).orElse(null);
    }

    public void save(Nhanvien nhanVien) {
        nhanVienRepository.save(nhanVien);
    }

    public void delete(int id) {
        nhanVienRepository.deleteById(id);
    }

    private boolean contains(String value, String keyword) {
        return value != null && value.toLowerCase(Locale.ROOT).contains(keyword);
    }
}
