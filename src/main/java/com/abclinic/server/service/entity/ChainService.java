package com.abclinic.server.service.entity;

import com.abclinic.server.common.constant.Constant;
import com.abclinic.server.common.constant.FilterConstant;
import com.abclinic.server.common.criteria.EntityPredicateBuilder;
import com.abclinic.server.common.criteria.InquiryChainPredicateBuilder;
import com.abclinic.server.exception.NotFoundException;
import com.abclinic.server.model.entity.payload.Chain;
import com.abclinic.server.model.entity.payload.Inquiry;
import com.abclinic.server.model.entity.user.User;
import com.abclinic.server.repository.ChainRepository;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author tmduc
 * @package com.abclinic.server.service.entity
 * @created 7/23/2020 2:40 PM
 */
@Service
public class ChainService implements IDataMapperService<Chain> {
    private ChainRepository chainRepository;

    @Autowired
    public ChainService(ChainRepository chainRepository) {
        this.chainRepository = chainRepository;
    }

    @Override
    public Chain getById(long id) throws NotFoundException {
        return chainRepository.findById(id)
                .orElseThrow(NotFoundException::new);
    }

    public List<Inquiry> getInquiries(long chainId) {
        return chainRepository.findByChainId(chainId)
                .orElseThrow(NotFoundException::new)
                .stream()
                .map(Chain::getInquiry)
                .collect(Collectors.toList());
    }

    @Override
    public Page<Chain> getList(User user, String search, EntityPredicateBuilder builder, Pageable pageable) {
        InquiryChainPredicateBuilder predBuilder = (InquiryChainPredicateBuilder) builder.init(search);
        switch (user.getRole()) {
            case PATIENT:
                predBuilder.with(FilterConstant.CHAIN_PATIENT_ID.getValue(), Constant.EQUAL_SBL, user.getId());
                break;
            case PRACTITIONER:
                predBuilder.with(FilterConstant.CHAIN_PRACTITIONER_ID.getValue(), Constant.EQUAL_SBL, user.getId());
                break;
            case DIETITIAN:
            case SPECIALIST:
                predBuilder.with(FilterConstant.CHAIN_SUBDOCTOR.getValue(), Constant.CONTAIN_SBL, user);
                break;
        }
        BooleanExpression expression = predBuilder.build();
        if (expression != null)
            return chainRepository.findAll(expression, pageable);
        return chainRepository.findAll(pageable);
    }

    public Chain create(Inquiry inquiry, long chainId) {
        Chain chain;
        if (chainId == 0) {
            chain = new Chain(inquiry);
            chainRepository.findTopByPatientOrderByChainIdDesc(inquiry.getPatient())
                    .ifPresent(c -> chain.setChainId(c.getChainId() + 1));
        } else {
            Chain c = chainRepository.findTopByPatientOrderByChainIdDesc(inquiry.getPatient())
                    .orElseThrow(NotFoundException::new);
            chain = c.clone();
            c.setNextInquiry(inquiry);
            save(c);

            chain.setId(0);
            chain.setPrevInquiry(c.getInquiry());
            chain.setInquiry(inquiry);
        }
        return save(chain);
    }

    @Override
    public Chain save(Chain obj) {
        return chainRepository.save(obj);
    }
}
