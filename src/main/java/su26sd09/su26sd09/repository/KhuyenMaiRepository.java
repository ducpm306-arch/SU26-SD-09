package su26sd09.su26sd09.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import su26sd09.su26sd09.entity.KhuyenMai;

@Repository
public interface KhuyenMaiRepository extends JpaRepository<KhuyenMai, Integer> {
}
