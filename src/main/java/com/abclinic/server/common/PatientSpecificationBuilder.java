//package com.abclinic.server.common;
//
//import com.abclinic.server.model.entity.user.Patient;
//import org.springframework.data.jpa.domain.Specification;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.stream.Collectors;
//
///**
// * @author tmduc
// * @package com.abclinic.server.common
// * @created 4/22/2020 10:39 AM
// */
//public class PatientSpecificationBuilder {
//    private final List<SearchCriteria> params;
//
//    public PatientSpecificationBuilder() {
//        params = new ArrayList<>();
//    }
//
//    public PatientSpecificationBuilder with(String key, String operation, Object value) {
//        params.add(new SearchCriteria(key, operation, value));
//        return this;
//    }
//
//    public PatientSpecificationBuilder with(String key, Object value) {
//        return with(key, "=", value);
//    }
//    public Specification<Patient> build() {
//        if (params.size() == 0)
//            return null;
//
//        List<Specification> specs = params.stream()
//                .map(PatientSpecification::new)
//                .collect(Collectors.toList());
//        Specification result = specs.get(0);
//
//        params.stream().skip(1).forEach(p -> {
//            result = p
//        });
//    }
//}
