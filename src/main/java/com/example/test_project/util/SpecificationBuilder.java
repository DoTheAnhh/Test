package com.example.test_project.util;

import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class SpecificationBuilder {

    public <T> Specification<T> build(Map<String, Object> filters) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            filters.forEach((key, value) -> {
                if (value != null) {
                    Path<Object> path = root.get(key);
                    if (value instanceof String s) {
                        predicates.add(cb.like(path.as(String.class), "%" + s + "%"));
                    } else {
                        predicates.add(cb.equal(path, value));
                    }
                }
            });

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
