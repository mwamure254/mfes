package com.mfano.mfes.repositories;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.mfano.mfes.models.Repayments;

import jakarta.persistence.LockModeType;

public interface RepaymentRepository extends JpaRepository<Repayments, Long> {
        @Query("""
                        SELECT i FROM Repayments i
                        WHERE i.dueDate < :today
                          AND i.amountDue > i.amountPaid
                        ORDER BY i.dueDate ASC
                        """)
        List<Repayments> findOverdue(
                        @Param("today") LocalDate today);

        @Query("""
                        SELECT i FROM Repayments i
                        WHERE i.dueDate BETWEEN :start AND :end
                          AND i.amountDue > i.amountPaid
                        ORDER BY i.dueDate ASC
                        """)
        List<Repayments> findUnpaidBetween(
                        @Param("start") LocalDate start,
                        @Param("end") LocalDate end);

        @Query("""
                        SELECT i FROM Repayments i
                        WHERE i.amountDue > i.amountPaid
                        ORDER BY i.dueDate ASC
                        """)
        List<Repayments> findAllUnpaid();

        @Lock(LockModeType.PESSIMISTIC_WRITE)
        @Query("""
                        SELECT i
                        FROM Repayments i
                        WHERE i.id = :id
                        """)
        Optional<Repayments> findById(
                        @Param("id") Long id);

        @Query("""
                            SELECT i.id
                            FROM Repayments i
                            WHERE i.dueDate <= :today
                              AND i.status = :status
                            ORDER BY i.dueDate ASC, i.id ASC
                        """)
        List<Long> findDueInstallmentIds(
                        @Param("today") LocalDate today,
                        @Param("status") String status);

        @Query("""
                            select i.id
                            from Repayments i
                            where i.dueDate <= :today
                              and i.status = :status
                            order by i.dueDate asc, i.id asc
                        """)
        List<Long> findDueIds(
                        @Param("today") LocalDate today,
                        @Param("status") String status);

        boolean existsById(Long id);
}
