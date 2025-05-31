package com.DXC.iotbackend.util;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SensorFilterSpecificationBuilder {

    public static <T> Specification<T> buildCommonFilters(
            String location,
            String statusOrCongestion,
            LocalDateTime start,
            LocalDateTime end,
            String locationField,
            String statusField
    ) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (location != null && !location.isBlank()) {
                predicates.add(cb.equal(cb.lower(root.get(locationField)), location.toLowerCase()));
            }

            if (statusOrCongestion != null && !statusOrCongestion.isBlank()) {
                predicates.add(cb.equal(cb.lower(root.get(statusField)), statusOrCongestion.toLowerCase()));
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