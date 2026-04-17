package com.company.kassa.repository.specification;

import com.company.kassa.dto.product.ProductTransactionFilter;
import com.company.kassa.models.ProductTransaction;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;

import java.util.ArrayList;
import java.util.List;

public record ProductTransactionSpecification(
        ProductTransactionFilter filter) implements Specification<ProductTransaction> {

    @Nullable
    @Override
    public Predicate toPredicate(Root<ProductTransaction> root,
                                 @Nullable CriteriaQuery<?> query,
                                 CriteriaBuilder cb) {
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.isNull(root.get("deletedAt")));

        if (filter == null) {
            return cb.and(predicates.toArray(new Predicate[0]));
        }
        if (filter.getToUserId() != null) {
            predicates.add(cb.equal(root.get("toUser").get("id"), filter.getToUserId()));
        }

        if (filter.getFromUserId() != null) {
            predicates.add(cb.equal(root.get("fromUser").get("id"), filter.getToUserId()));
        }
        if (filter.getIsCompleted() != null) {
            predicates.add(cb.equal(root.get("isCompleted"), filter.getIsCompleted()));
        }

        if (filter.getFrom() != null && filter.getTo() != null) {
            predicates.add(cb.between(root.get("transactionDate"), filter.getFrom(), filter.getTo()));
        } else if (filter.getFrom() != null) {
            predicates.add(
                    cb.greaterThanOrEqualTo(
                            root.get("transactionDate"), filter.getFrom()
                    )
            );
        } else if (filter.getTo() != null) {
            predicates.add(
                    cb.lessThanOrEqualTo(
                            root.get("transactionDate"), filter.getTo()
                    )
            );
        }

        return cb.and(predicates.toArray(new Predicate[0]));
    }
}
