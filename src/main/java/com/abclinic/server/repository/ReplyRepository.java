package com.abclinic.server.repository;

import com.abclinic.server.model.entity.payload.Reply;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReplyRepository extends JpaRepository<Reply, Long> {
    Optional<Page<Reply>> findByInquiryId(long inquiryId, Pageable pageable);
}
