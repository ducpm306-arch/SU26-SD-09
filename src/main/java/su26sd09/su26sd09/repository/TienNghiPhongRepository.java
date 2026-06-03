package su26sd09.su26sd09.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import su26sd09.su26sd09.entity.TienNghiPhong;
import su26sd09.su26sd09.entity.TienNghiPhongId;

import java.util.List;

public interface TienNghiPhongRepository extends JpaRepository<TienNghiPhong, TienNghiPhongId> {
    List<TienNghiPhong> findByPhongMaPhong(int maPhong);
    void deleteByPhongMaPhong(int maPhong);
}
