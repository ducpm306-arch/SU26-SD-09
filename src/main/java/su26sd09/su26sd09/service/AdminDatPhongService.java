package su26sd09.su26sd09.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import su26sd09.su26sd09.entity.ChiTietDatPhong;
import su26sd09.su26sd09.entity.DatPhong;
import su26sd09.su26sd09.entity.NguoiDung;
import su26sd09.su26sd09.entity.Phong;
import su26sd09.su26sd09.repository.ChiTietDatPhongRepo;
import su26sd09.su26sd09.repository.DatPhongRepo;
import su26sd09.su26sd09.repository.NguoiDungRepository;
import su26sd09.su26sd09.repository.PhongRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class AdminDatPhongService {

    @Autowired
    private DatPhongRepo datPhongRepo;

    @Autowired
    private ChiTietDatPhongRepo chiTietDatPhongRepo;

    @Autowired
    private NguoiDungRepository nguoiDungRepository;

    @Autowired
    private PhongRepository phongRepository;

    public List<DatPhong> search(String keyword) {
        List<DatPhong> datPhongs = datPhongRepo.findAll();
        if (keyword == null || keyword.isBlank()) {
            return datPhongs;
        }

        String q = keyword.toLowerCase(Locale.ROOT);
        return datPhongs.stream()
                .filter(dp -> contains(String.valueOf(dp.getId()), q)
                        || (dp.getN() != null && contains(dp.getN().getHoTen(), q))
                        || (dp.getN() != null && contains(dp.getN().getEmail(), q))
                        || (dp.getNv() != null && contains(dp.getNv().getHoTen(), q))
                        || contains(dp.getTrangThai(), q)
                        || contains(roomLabel(dp.getId()), q))
                .toList();
    }

    public DatPhong findById(int id) {
        return datPhongRepo.findById(id).orElse(null);
    }

    public Map<Integer, String> buildRoomLabelByDatPhong(List<DatPhong> datPhongs) {
        Map<Integer, String> result = new LinkedHashMap<>();
        for (DatPhong datPhong : datPhongs) {
            result.put(datPhong.getId(), roomLabel(datPhong.getId()));
        }
        return result;
    }

    public Integer findPhongIdByDatPhong(int datPhongId) {
        return chiTietDatPhongRepo.findFirstByD_Id(datPhongId)
                .map(item -> item.getP() != null ? item.getP().getMaPhong() : null)
                .orElse(null);
    }

    public List<ChiTietDatPhong> findChiTietByDatPhong(int datPhongId) {
        return chiTietDatPhongRepo.findByD_Id(datPhongId);
    }

    public DatPhong findLatestBookingByUserAndRoom(int userId, int roomId) {
        return chiTietDatPhongRepo.findLatestByUserAndRoom(userId, roomId)
                .map(ChiTietDatPhong::getD)
                .orElse(null);
    }

    @Transactional
    public void save(
            DatPhong form,
            Integer maKhach,
            Integer maNhanVien,
            Integer maPhong,
            LocalDate ngayNhan,
            LocalDate ngayTra
    ) {
        DatPhong datPhong = form.getId() == 0 ? new DatPhong() : findById(form.getId());
        if (datPhong == null) {
            datPhong = new DatPhong();
        }

        NguoiDung khach = maKhach == null ? null : nguoiDungRepository.findById(maKhach).orElse(null);
        NguoiDung nhanVien = maNhanVien == null ? null : nguoiDungRepository.findById(maNhanVien).orElse(null);

        datPhong.setN(khach);
        datPhong.setNv(nhanVien);
        datPhong.setNgaydatPhong(ngayNhan == null ? null : ngayNhan.atStartOfDay());
        datPhong.setNgaytraPhong(ngayTra == null ? null : ngayTra.atStartOfDay());
        datPhong.setSonguoiLon(form.getSonguoiLon());
        datPhong.setSotreEm(form.getSotreEm());
        datPhong.setYeuCauThem(form.getYeuCauThem());
        datPhong.setTrangThai(form.getTrangThai() == null || form.getTrangThai().isBlank()
                ? "Cho xac nhan"
                : form.getTrangThai());

        LocalDateTime now = LocalDateTime.now();
        if (datPhong.getId() == 0) {
            datPhong.setNgayTao(now);
        }
        datPhong.setNgayCapNhat(now);

        DatPhong saved = datPhongRepo.save(datPhong);
        savePhong(saved, maPhong);
    }

    @Transactional
    public DatPhong createQuickBooking(
            NguoiDung khach,
            Phong phong,
            LocalDate ngayNhan,
            LocalDate ngayTra,
            int nguoiLon,
            int treEm,
            String yeuCauThem
    ) {
        DatPhong datPhong = new DatPhong();
        datPhong.setN(khach);
        datPhong.setNgaydatPhong(ngayNhan.atStartOfDay());
        datPhong.setNgaytraPhong(ngayTra.atStartOfDay());
        datPhong.setSonguoiLon(nguoiLon);
        datPhong.setSotreEm(treEm);
        datPhong.setYeuCauThem(yeuCauThem);
        datPhong.setTrangThai("Cho xac nhan");
        datPhong.setNgayTao(LocalDateTime.now());
        datPhong.setNgayCapNhat(LocalDateTime.now());

        DatPhong saved = datPhongRepo.save(datPhong);
        savePhong(saved, phong == null ? null : phong.getMaPhong());
        return saved;
    }

    @Transactional
    public DatPhong createBooking(
            NguoiDung khach,
            List<Phong> phongs,
            LocalDate ngayNhan,
            LocalDate ngayTra,
            int nguoiLon,
            int treEm,
            String yeuCauThem,
            String trangThai
    ) {
        DatPhong datPhong = new DatPhong();
        datPhong.setN(khach);
        datPhong.setNgaydatPhong(ngayNhan.atStartOfDay());
        datPhong.setNgaytraPhong(ngayTra.atStartOfDay());
        datPhong.setSonguoiLon(nguoiLon);
        datPhong.setSotreEm(treEm);
        datPhong.setYeuCauThem(yeuCauThem);
        datPhong.setTrangThai(trangThai == null || trangThai.isBlank() ? "Cho xac nhan" : trangThai);
        datPhong.setNgayTao(LocalDateTime.now());
        datPhong.setNgayCapNhat(LocalDateTime.now());

        DatPhong saved = datPhongRepo.save(datPhong);
        savePhongs(saved, phongs);
        return saved;
    }

    @Transactional
    public void delete(int id) {
        for (ChiTietDatPhong chiTiet : chiTietDatPhongRepo.findByD_Id(id)) {
            chiTietDatPhongRepo.delete(chiTiet);
        }
        datPhongRepo.deleteById(id);
    }

    private void savePhong(DatPhong datPhong, Integer maPhong) {
        if (maPhong == null) {
            savePhongs(datPhong, List.of());
            return;
        }

        Phong phong = phongRepository.findById(maPhong).orElse(null);
        savePhongs(datPhong, phong == null ? List.of() : List.of(phong));
    }

    private void savePhongs(DatPhong datPhong, List<Phong> phongs) {
        for (ChiTietDatPhong chiTiet : chiTietDatPhongRepo.findByD_Id(datPhong.getId())) {
            chiTietDatPhongRepo.delete(chiTiet);
        }

        if (phongs == null || phongs.isEmpty()) {
            return;
        }

        List<ChiTietDatPhong> chiTiets = new ArrayList<>();
        for (Phong phong : phongs) {
            if (phong == null) {
                continue;
            }

            ChiTietDatPhong chiTiet = new ChiTietDatPhong();
            chiTiet.setD(datPhong);
            chiTiet.setP(phong);
            chiTiet.setGiaMoiDem(phong.getGiaMoiDem());
            chiTiets.add(chiTiet);
        }
        chiTietDatPhongRepo.saveAll(chiTiets);
    }

    private String roomLabel(int datPhongId) {
        return chiTietDatPhongRepo.findFirstByD_Id(datPhongId)
                .map(item -> item.getP() == null ? "" : item.getP().getSoPhong())
                .orElse("");
    }

    private boolean contains(String value, String keyword) {
        return value != null && value.toLowerCase(Locale.ROOT).contains(keyword);
    }
}
