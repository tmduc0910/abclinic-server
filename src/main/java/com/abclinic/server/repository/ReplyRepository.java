package com.abclinic.server.repository;

import com.abclinic.server.model.entity.payload.Reply;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReplyRepository extends JpaRepository<Reply, Long> {
}
