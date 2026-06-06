package su26sd09.su26sd09.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import su26sd09.su26sd09.entity.HoaDon;
import su26sd09.su26sd09.repository.HoaDonRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

@Service
public class HoaDonService {

    @Autowired
    private HoaDonRepository hoaDonRepository;

    public List<HoaDon> search(String keyword) {
        List<HoaDon> hoaDons = hoaDonRepository.findAll();
        if (keyword == null || keyword.isBlank()) {
            return hoaDons;
        }

        String q = keyword.toLowerCase(Locale.ROOT);
        return hoaDons.stream()
                .filter(hd -> contains(String.valueOf(hd.getId()), q)
                        || (hd.getD() != null && contains(String.valueOf(hd.getD().getId()), q))
                        || (hd.getD() != null && hd.getD().getN() != null && contains(hd.getD().getN().getHoTen(), q))
                        || (hd.getK() != null && contains(hd.getK().getPromoCode(), q))
                        || (hd.getN() != null && contains(hd.getN().getHoTen(), q))
                        || contains(hd.getGhiChu(), q))
                .toList();
    }

    public HoaDon findById(int id) {
        return hoaDonRepository.findById(id).orElse(null);
    }

    public void save(HoaDon hoaDon) {
        LocalDateTime now = LocalDateTime.now();
        hoaDon.setTienPhong(defaultMoney(hoaDon.getTienPhong()));
        hoaDon.setTienDichVu(defaultMoney(hoaDon.getTienDichVu()));
        hoaDon.setTienGiam(defaultMoney(hoaDon.getTienGiam()));
        hoaDon.setTienVat(defaultMoney(hoaDon.getTienVat()));
        hoaDon.setTongTien(defaultMoney(hoaDon.getTongTien()));
        hoaDon.setDaThanhToan(defaultMoney(hoaDon.getDaThanhToan()));
        if (hoaDon.getNgayXuat() == null) {
            HoaDon oldHoaDon = findById(hoaDon.getId());
            hoaDon.setNgayXuat(oldHoaDon != null && oldHoaDon.getNgayXuat() != null ? oldHoaDon.getNgayXuat() : now);
        }
        hoaDon.setNgayCapNhat(now);
        hoaDonRepository.save(hoaDon);
    }

    public void delete(int id) {
        hoaDonRepository.deleteById(id);
    }

    private boolean contains(String value, String keyword) {
        return value != null && value.toLowerCase(Locale.ROOT).contains(keyword);
    }

    private BigDecimal defaultMoney(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }
}
