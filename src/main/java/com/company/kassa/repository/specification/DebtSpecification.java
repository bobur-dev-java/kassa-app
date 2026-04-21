package com.company.kassa.repository.specification;

import com.company.kassa.dto.debit.DebitFilter;
import com.company.kassa.models.Debt;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;

import java.util.ArrayList;
import java.util.List;

public record DebtSpecification(DebitFilter filter) implements Specification<Debt> {

    @Nullable
    @Override
    public Predicate toPredicate(Root<Debt> root,
                                 @Nullable CriteriaQuery<?> query,
                                 CriteriaBuilder cb) {
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.isNull(root.get("deletedAt")));

        if (filter == null) {
            return cb.and(predicates.toArray(new Predicate[0]));
        }
        if (filter.getFromUserId() != null) {
            predicates.add(cb.equal(root.get("fromUser").get("id"), filter.getFromUserId()));
        }
        return cb.and(predicates.toArray(new Predicate[0]));
    }
}
