package com.example.echo.domain.reply.repository;

import com.example.echo.domain.reply.entity.Reply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReplyRepository extends JpaRepository<Reply, Long> {

    // 특정 그룹(ref)에서 가장 큰 refOrder 조회
    @Query("SELECT COALESCE(MAX(r.refOrder), 0) FROM Reply r WHERE r.ref = :ref")
    Long findMaxRefOrderByRef(Long ref);

    // 부모 refOrder 이후의 댓글들 refOrder + 1로 밀기
    @Modifying
    @Query("UPDATE Reply r SET r.refOrder = r.refOrder + 1 " +
            "WHERE r.ref = :ref AND r.refOrder > :parentRefOrder")
    void incrementRefOrderAfter(Long ref, Long parentRefOrder);

    // 특정 게시글의 댓글 목록 조회 (REF ASC, REFORDER ASC)
    List<Reply> findByCapsule_IdOrderByRefAscRefOrderAsc(Long capsuleId);
}
