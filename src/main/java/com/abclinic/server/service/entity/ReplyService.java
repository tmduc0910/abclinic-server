package com.abclinic.server.service.entity;

import com.abclinic.server.exception.NotFoundException;
import com.abclinic.server.model.entity.payload.Reply;
import com.abclinic.server.repository.ReplyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

/**
 * @author tmduc
 * @package com.abclinic.server.service.entity
 * @created 5/4/2020 9:57 AM
 */
@Service
public class ReplyService implements IDataMapperService<Reply> {
    private ReplyRepository replyRepository;

    @Autowired
    public ReplyService(ReplyRepository replyRepository) {
        this.replyRepository = replyRepository;
    }

    @Override
    @Transactional
    public Reply getById(long id) throws NotFoundException {
        return replyRepository.findById(id).orElseThrow(NotFoundException::new);
    }

    @Override
    public Reply save(Reply obj) {
        return replyRepository.save(obj);
    }

    @Transactional
    public Page<Reply> getList(long inquiryId, Pageable pageable) {
        return replyRepository.findByInquiryId(inquiryId, pageable).orElseThrow(NotFoundException::new);
    }
}
