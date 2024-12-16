package com.example.echo.domain.reply.repository;

import com.example.echo.domain.reply.entity.Reply;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReplyRepository extends JpaRepository<Reply, Long> {
}
