package su26sd09.su26sd09.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import su26sd09.su26sd09.entity.KhuyenMai;
import su26sd09.su26sd09.repository.KhuyenMaiRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

@Service
public class KhuyenMaiService {

    @Autowired
    private KhuyenMaiRepository khuyenMaiRepository;

    public List<KhuyenMai> findAll() {
        return khuyenMaiRepository.findAll();
    }

    public List<KhuyenMai> search(String keyword) {
        List<KhuyenMai> khuyenMais = khuyenMaiRepository.findAll();
        if (keyword == null || keyword.isBlank()) {
            return khuyenMais;
        }

        String q = keyword.toLowerCase(Locale.ROOT);
        return khuyenMais.stream()
                .filter(km -> contains(String.valueOf(km.getId()), q)
                        || contains(km.getPromoCode(), q)
                        || contains(km.getMoTa(), q)
                        || contains(km.getLoaiGiam(), q)
                        || (km.getN() != null && contains(km.getN().getHoTen(), q)))
                .toList();
    }

    public KhuyenMai findById(int id) {
        return khuyenMaiRepository.findById(id).orElse(null);
    }

    public void save(KhuyenMai khuyenMai) {
        if (khuyenMai.getLoaiGiam() == null || khuyenMai.getLoaiGiam().isBlank()) {
            khuyenMai.setLoaiGiam("Percent");
        }
        if (khuyenMai.getGiatriGiam() == null) {
            khuyenMai.setGiatriGiam(BigDecimal.ZERO);
        }
        if (khuyenMai.getNgayBatDau() == null) {
            khuyenMai.setNgayBatDau(LocalDate.now());
        }
        if (khuyenMai.getNgayKetThuc() == null) {
            khuyenMai.setNgayKetThuc(khuyenMai.getNgayBatDau().plusDays(30));
        }
        if (khuyenMai.getNgayTao() == null) {
            KhuyenMai oldKhuyenMai = findById(khuyenMai.getId());
            khuyenMai.setNgayTao(oldKhuyenMai != null && oldKhuyenMai.getNgayTao() != null
                    ? oldKhuyenMai.getNgayTao()
                    : LocalDateTime.now());
        }
        khuyenMaiRepository.save(khuyenMai);
    }

    public void delete(int id) {
        khuyenMaiRepository.deleteById(id);
    }

    private boolean contains(String value, String keyword) {
        return value != null && value.toLowerCase(Locale.ROOT).contains(keyword);
    }
}
