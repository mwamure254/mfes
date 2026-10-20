package com.mfano.mfes.services;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.mfano.mfes.models.GroupCollection;
import com.mfano.mfes.models.MemberCollection;
import com.mfano.mfes.repositories.GroupCollectionRepository;

import jakarta.transaction.Transactional;

@Service
public class GroupCollectionService {
    private final GroupCollectionRepository collectionRepository;

    public GroupCollectionService(GroupCollectionRepository collectionRepository) {
        this.collectionRepository = collectionRepository;
    }

    @Transactional
    public void postCollection(
            GroupCollection request, String postedBy) {

        if (request.getMembers() == null
                || request.getMembers().isEmpty()) {
            throw new IllegalArgumentException(
                    "No member contributions were submitted.");
        }

        Set<Long> uniqueMembers = new HashSet<>();
        BigDecimal total = BigDecimal.ZERO;

        // Phase 1: Validate submitted amounts before posting.
        for (MemberCollection line : request.getMembers()) {

            if (line.getMemberId() == null
                    || !uniqueMembers.add(line.getMemberId())) {
                throw new IllegalArgumentException(
                        "Missing or duplicate member in collection.");
            }

            BigDecimal savings = amountOrZero(
                    line.getSavingsAmount());
            BigDecimal loan = amountOrZero(
                    line.getLoanAmount());

            if (savings.signum() < 0 || loan.signum() < 0) {
                throw new IllegalArgumentException(
                        "Collection amounts cannot be negative.");
            }

            if (savings.signum() == 0
                    && loan.signum() == 0) {
                continue;
            }

            total = total.add(savings).add(loan);

            /*
             * Resolve the member from the database.
             * Verify that the member belongs to request.getGroupId().
             * Resolve the member's actual savings account and active loan.
             * Check branch permissions, account status, outstanding balance,
             * and whether the payment reference was already posted.
             *
             * Do not use an account ID or balance supplied by the browser.
             */
        }

        if (total.signum() <= 0) {
            throw new IllegalArgumentException(
                    "The collection total must be greater than zero.");
        }

        /*
         * Phase 2: Once the project-specific validations are implemented:
         *
         * 1. Save one parent GroupCollection.
         * 2. Save one collection line for each contributing member.
         * 3. Credit savings using the existing savings posting service.
         * 4. Apply loan repayments using the existing loan repayment service.
         * 5. Record the receipt, payment method, date, collector and audit.
         *
         * All posting methods must participate in this transaction.
         * Throw an exception on any failure so all database changes roll back.
         */
    }

    private BigDecimal amountOrZero(BigDecimal amount) {
        return amount == null ? BigDecimal.ZERO : amount;
    }
}
