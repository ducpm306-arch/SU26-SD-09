package su26sd09.su26sd09.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import su26sd09.su26sd09.entity.DatPhong;
import su26sd09.su26sd09.entity.HoaDon;
import su26sd09.su26sd09.entity.NguoiDung;
import su26sd09.su26sd09.entity.Phong;
import su26sd09.su26sd09.repository.PhongRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
public class CartCheckoutService {

    @Autowired
    private PhongRepository phongRepository;

    @Autowired
    private AdminDatPhongService adminDatPhongService;

    @Autowired
    private HoaDonService hoaDonService;

    @Transactional
    public HoaDon checkout(
            NguoiDung khach,
            List<Integer> roomIds,
            LocalDate ngayNhan,
            LocalDate ngayTra,
            int nguoiLon,
            int treEm
    ) {
        return checkout(khach, roomIds, ngayNhan, ngayTra, nguoiLon, treEm, null, "Dat tu gio hang");
    }

    @Transactional
    public HoaDon checkout(
            NguoiDung khach,
            List<Integer> roomIds,
            LocalDate ngayNhan,
            LocalDate ngayTra,
            int nguoiLon,
            int treEm,
            String yeuCauThem
    ) {
        return checkout(khach, roomIds, ngayNhan, ngayTra, nguoiLon, treEm, null, yeuCauThem);
    }

    @Transactional
    public HoaDon checkout(
            NguoiDung khach,
            List<Integer> roomIds,
            LocalDate ngayNhan,
            LocalDate ngayTra,
            int nguoiLon,
            int treEm,
            String maCccd,
            String yeuCauThem
    ) {
        Set<Integer> distinctRoomIds = new LinkedHashSet<>(roomIds);
        List<Phong> phongs = phongRepository.findAllById(distinctRoomIds)
                .stream()
                .filter(phong -> phong.isHoatDong() && "Trong".equals(phong.getTrangThai()))
                .toList();

        if (phongs.isEmpty()) {
            throw new IllegalArgumentException("Không có phòng khả dụng để thanh toán");
        }

        long soDem = Math.max(1, ChronoUnit.DAYS.between(ngayNhan, ngayTra));
        BigDecimal tienPhong = phongs.stream()
                .map(Phong::getGiaMoiDem)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .multiply(BigDecimal.valueOf(soDem));

        DatPhong datPhong = adminDatPhongService.createBooking(
                khach,
                phongs,
                ngayNhan,
                ngayTra,
                nguoiLon,
                treEm,
                maCccd,
                yeuCauThem,
                "Cho xac nhan"
        );

        HoaDon hoaDon = new HoaDon();
        hoaDon.setD(datPhong);
        hoaDon.setTienPhong(tienPhong);
        hoaDon.setTienDichVu(BigDecimal.ZERO);
        hoaDon.setTienGiam(BigDecimal.ZERO);
        hoaDon.setTienVat(BigDecimal.ZERO);
        hoaDon.setTongTien(tienPhong);
        hoaDon.setDaThanhToan(BigDecimal.ZERO);
        hoaDon.setGhiChu("Hoa don tam tinh tu gio hang");
        hoaDonService.save(hoaDon);
        return hoaDon;
    }
}
