package com.DXC.iotbackend.util;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SensorFilterSpecificationBuilder {

    public static <T> Specification<T> buildCommonFilters(
            String variable1,
            String variable2,
            LocalDateTime start,
            LocalDateTime end,
            String variable1Field,
            String variable2Field
    ) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (variable1 != null && !variable1.isBlank()) {
                predicates.add(cb.equal(cb.lower(root.get(variable1Field)), variable1.toLowerCase()));
            }

            if (variable2 != null && !variable2.isBlank()) {
                predicates.add(cb.equal(cb.lower(root.get(variable2Field)), variable2.toLowerCase()));
            }

            if (start != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("timestamp"), start));
            }

            if (end != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("timestamp"), end));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}