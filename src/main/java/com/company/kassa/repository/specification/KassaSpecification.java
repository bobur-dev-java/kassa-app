package com.company.kassa.repository.specification;

import com.company.kassa.dto.kassa.KassaFilter;
import com.company.kassa.models.Kassa;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;

import java.util.ArrayList;
import java.util.List;

public record KassaSpecification(KassaFilter filter) implements Specification<Kassa> {

    @Nullable
    @Override
    public Predicate toPredicate(Root<Kassa> root,
                                 @Nullable CriteriaQuery<?> query,
                                 CriteriaBuilder cb) {
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.isNull(root.get("deletedAt")));

        if (filter == null) {
            return cb.and(predicates.toArray(new Predicate[0]));
        }
        if (filter.getOwnerId() != null) {
            predicates.add(
                    cb.equal(root.get("owner").get("id"), filter.getOwnerId())
            );
        }
        if (filter.getIsCompleted() != null) {
            predicates.add(
                    cb.equal(root.get("isCompleted"), filter.getIsCompleted())
            );
        }

        if (filter.getFrom() != null && filter.getTo() != null) {
            predicates.add(cb.between(root.get("kassaDate"), filter.getFrom(), filter.getTo()));
        } else if (filter.getFrom() != null) {
            predicates.add(
                    cb.greaterThanOrEqualTo(
                            root.get("kassaDate"), filter.getFrom()
                    )
            );
        } else if (filter.getTo() != null) {
            predicates.add(
                    cb.lessThanOrEqualTo(
                            root.get("kassaDate"), filter.getTo()
                    )
            );
        }
        return cb.and(predicates.toArray(new Predicate[0]));
    }
}
