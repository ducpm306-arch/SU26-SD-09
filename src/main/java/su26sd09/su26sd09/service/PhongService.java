package su26sd09.su26sd09.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import su26sd09.su26sd09.entity.LoaiPhong;
import su26sd09.su26sd09.entity.Phong;
import su26sd09.su26sd09.entity.TienNghi;
import su26sd09.su26sd09.entity.TienNghiPhong;
import su26sd09.su26sd09.repository.LoaiPhongRepository;
import su26sd09.su26sd09.repository.PhongRepository;
import su26sd09.su26sd09.repository.TienNghiPhongRepository;
import su26sd09.su26sd09.repository.TienNghiRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class PhongService {

    @Autowired
    private PhongRepository phongRepository;

    @Autowired
    private LoaiPhongRepository loaiPhongRepository;

    @Autowired
    private TienNghiRepository tienNghiRepository;

    @Autowired
    private TienNghiPhongRepository tienNghiPhongRepository;

    public List<Phong> search(String keyword) {
        return phongRepository.search(keyword);
    }

    public Phong findById(int id) {
        return phongRepository.findById(id).orElse(null);
    }

    public List<LoaiPhong> findAllLoaiPhong() {
        return loaiPhongRepository.findAllByOrderByTenLoaiPhongAsc();
    }

    public List<LoaiPhong> searchLoaiPhong(String mucGia, Integer nguoiLon, Integer treEm) {
        BigDecimal minGia = null;
        BigDecimal maxGia = null;

        if ("duoi1tr".equals(mucGia)) {
            maxGia = new BigDecimal("1000000");
        } else if ("1tr-2tr".equals(mucGia)) {
            minGia = new BigDecimal("1000000");
            maxGia = new BigDecimal("2000000");
        } else if ("tren2tr".equals(mucGia)) {
            minGia = new BigDecimal("2000000");
        }

        Integer soKhach = null;
        if (nguoiLon != null || treEm != null) {
            soKhach = (nguoiLon == null ? 0 : nguoiLon) + (treEm == null ? 0 : treEm);
        }

        return loaiPhongRepository.searchLoaiPhong(minGia, maxGia, soKhach);
    }

    public LoaiPhong findLoaiPhongById(int id) {
        return loaiPhongRepository.findById(id).orElse(null);
    }

    public List<Phong> findPhongTheoLoai(int loaiPhongId) {
        return phongRepository.findByLoaiPhongIdAndHoatDongTrueOrderBySoPhongAsc(loaiPhongId);
    }

    public long countPhongTrongTheoLoai(int loaiPhongId) {
        return phongRepository.countByLoaiPhongIdAndHoatDongTrueAndTrangThai(loaiPhongId, "Trong");
    }

    public List<LoaiPhong> findLoaiPhongKhac(int id) {
        return loaiPhongRepository.findAllByOrderByTenLoaiPhongAsc()
                .stream()
                .filter(loaiPhong -> loaiPhong.getId() != id)
                .toList();
    }

    public List<TienNghi> findAllTienNghi() {
        return tienNghiRepository.findAllByOrderByTenTienNghiAsc();
    }

    public List<Integer> findTienNghiIdsByPhong(int maPhong) {
        return tienNghiPhongRepository.findByPhongMaPhong(maPhong)
                .stream()
                .map(tnp -> tnp.getTienNghi().getMaTienNghi())
                .toList();
    }

    public List<String> findTenTienNghiByPhong(int maPhong) {
        return tienNghiPhongRepository.findByPhongMaPhong(maPhong)
                .stream()
                .map(tnp -> tnp.getTienNghi().getTenTienNghi())
                .toList();
    }

    @Transactional
    public void save(Phong phong, int loaiPhongId, List<Integer> tienNghiIds) {
        LoaiPhong loaiPhong = loaiPhongRepository.findById(loaiPhongId).orElse(null);
        phong.setLoaiPhong(loaiPhong);
        if (loaiPhong == null) throw new RuntimeException("Loai phong khong ton tai");

        if (phong.getMaPhong() == 0) {
            phong.setNgayTao(LocalDateTime.now());
            phong.setNgayCapNhat(LocalDateTime.now());
            Phong savedPhong = phongRepository.save(phong);
            saveTienNghiPhong(savedPhong, tienNghiIds);
            return;
        }

        Phong oldPhong = findById(phong.getMaPhong());
        if (oldPhong == null) {
            return;
        }

        oldPhong.setLoaiPhong(phong.getLoaiPhong());
        oldPhong.setSoPhong(phong.getSoPhong());
        oldPhong.setSoTang(phong.getSoTang());
        oldPhong.setGiaMoiDem(phong.getGiaMoiDem());
        oldPhong.setTrangThai(phong.getTrangThai());
        oldPhong.setMoTa(phong.getMoTa());
        oldPhong.setHoatDong(phong.isHoatDong());
        oldPhong.setNgayCapNhat(LocalDateTime.now());

        Phong savedPhong = phongRepository.save(oldPhong);
        saveTienNghiPhong(savedPhong, tienNghiIds);
    }

    private void saveTienNghiPhong(Phong phong, List<Integer> tienNghiIds) {
        tienNghiPhongRepository.deleteByPhongMaPhong(phong.getMaPhong());

        if (tienNghiIds == null || tienNghiIds.isEmpty()) {
            return;
        }

        List<TienNghiPhong> tienNghiPhongs = new ArrayList<>();
        List<TienNghi> tienNghis = tienNghiRepository.findAllById(tienNghiIds);

        for (TienNghi tienNghi : tienNghis) {
            TienNghiPhong tienNghiPhong = new TienNghiPhong();
            tienNghiPhong.setPhong(phong);
            tienNghiPhong.setTienNghi(tienNghi);
            tienNghiPhongs.add(tienNghiPhong);
        }

        tienNghiPhongRepository.saveAll(tienNghiPhongs);
    }

    public void delete(int id) {
        Phong phong = findById(id);
        if (phong != null) {
            phong.setHoatDong(false);
            phong.setNgayCapNhat(LocalDateTime.now());
            phongRepository.save(phong);
        }
    }
}
