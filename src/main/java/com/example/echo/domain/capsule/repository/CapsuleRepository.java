package com.example.echo.domain.capsule.repository;

import com.example.echo.domain.capsule.entity.Capsule;
import com.example.echo.domain.security.entity.AuthUser;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface CapsuleRepository extends JpaRepository<Capsule,Long> {
    Optional<Capsule> findByIdAndDeletedAtIsNull(Long capsuleId);
    List<Capsule> findAllByDeletedAtIsNotNull();
    List<Capsule> findByUserIdAndDeletedAtIsNullOrderByDeadLineAsc(Long userId);
    Slice<Capsule> findByDeadLineAndIsOpenedFalse(LocalDate deadLine, Pageable pageable);

//    @Query(value = "SELECT c.*, " +
//            "CONCAT(LPAD(DATE_FORMAT(c.dead_line, '%Y%M%D'), 10, '0'), LPAD(c.id, 10, '0')) as lpad_id " +
//            "FROM capsule c " +
//            "WHERE c.user_id = :userId AND c.deleted_at IS NULL " +
//            "AND CONCAT(LPAD(DATE_FORMAT(c.dead_line, '%Y%M%D'), 10, '0'), LPAD(c.id, 10, '0')) < " +
//            "(SELECT CONCAT(LPAD(DATE_FORMAT(c3.dead_line, '%Y%M%D'), 10, '0'), LPAD(c3.id, 10, '0')) " +
//            "FROM capsule c3 WHERE c3.id = :capsuleId) " +
//            "ORDER BY lpad_id ASC",
//            nativeQuery = true)
//    @Query(value = "SELECT c.* FROM capsule c  " +
//            "WHERE c.user_id = :userId AND c.deleted_at IS NULL " +
//            "AND cursorValue > (SELECT CONCAT(LPAD(DATE_FORMAT(:deadLine, '%Y%M%D'), 10, '0'), LPAD(c3.id, 10, '0')) as cursorValue FROM capsule c3 WHERE c3.id = :capsuleId) " +
//            "ORDER BY cursorTable.cursorValue ASC",
//            nativeQuery = true)

@Query(value = """
        SELECT c.* FROM capsule c
        WHERE c.user_id = :userId AND c.deleted_at IS NULL
        AND CONCAT(LPAD(DATE_FORMAT(c.dead_line, '%Y%m%d'), 10, '0'), LPAD(c.id, 10, '0')) >
            CONCAT(LPAD(DATE_FORMAT(:deadline, '%Y%m%d'), 10, '0'), LPAD(:capsuleId, 10, '0'))
        ORDER BY c.dead_line ASC, c.id ASC 
        """, nativeQuery = true)
    Slice<Capsule> findByUserIdAndDeletedAtIsNullAndCursorUsingLPAD(Long userId, @Param("capsuleId")Long cursor, LocalDate deadline, Pageable pageable);
    Slice<Capsule> findByUserIdAndDeletedAtIsNullOrderByDeadLineAsc(Long userId,Pageable pageable);
}
