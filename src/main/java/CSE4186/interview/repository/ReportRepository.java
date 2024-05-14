package CSE4186.interview.repository;

import CSE4186.interview.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ReportRepository extends JpaRepository<Report, Long> {

    @Query("select count(r) from Report r join r.user u where u.id = :id")
    Long findReportCountByUser(Long id);
}
