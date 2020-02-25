package com.abclinic.server.repository;

import com.abclinic.server.model.entity.payload.Reply;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReplyRepository extends JpaRepository<Reply, Long> {
    Optional<List<Reply>> findByInquiryId(long inquiryId);
}
