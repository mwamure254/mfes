package com.mfano.mfes.repositories;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.mfano.mfes.dtos.LoanRepaymentSummary;
import com.mfano.mfes.models.Loan;
import com.mfano.mfes.models.Repayments;

import jakarta.persistence.LockModeType;

public interface RepaymentRepository extends JpaRepository<Repayments, Long> {
  @Query("""
      SELECT i FROM Repayments i
      WHERE i.dueDate < :today
        AND i.totalAmount > i.amountPaid
      ORDER BY i.dueDate ASC
      """)
  List<Repayments> findOverdue(
      @Param("today") LocalDate today);

  @Query("""
      SELECT i FROM Repayments i
      WHERE i.dueDate BETWEEN :start AND :end
        AND i.totalAmount > i.amountPaid
      ORDER BY i.dueDate ASC
      """)
  List<Repayments> findUnpaidBetween(
      @Param("start") LocalDate start,
      @Param("end") LocalDate end);

  @Query("""
      SELECT i FROM Repayments i
      WHERE i.dueDate <= :end
        AND i.totalAmount > i.amountPaid
      ORDER BY i.dueDate ASC
      """)
  List<Repayments> findUnpaidUpto(@Param("end") LocalDate end);

  @Query("""
      SELECT i FROM Repayments i
      WHERE i.totalAmount > i.amountPaid
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

  @Query("""
      SELECT new com.mfano.mfes.dtos.LoanRepaymentSummary(
          r.loan.id,
          COUNT(r.id),
          MAX(r.installmentNo),
          COALESCE(SUM(r.principalAmount), 0),
          COALESCE(SUM(r.interestAmount), 0),
          COALESCE(SUM(r.totalAmount), 0),
          COALESCE(SUM(r.amountPaid), 0),
          COALESCE(SUM(r.penalty), 0),
          COALESCE(SUM(r.totalAmount), 0)
              - COALESCE(SUM(r.amountPaid), 0)
      )
      FROM Repayments r
        where r.dueDate <= :end
      GROUP BY r.loan.id
      ORDER BY r.loan.id
      """)
  List<LoanRepaymentSummary> getLoanSummaries(@Param("end") LocalDate end);

  @Query("""
      SELECT new com.mfano.mfes.dtos.LoanRepaymentSummary(
          r.loan.id,
          COUNT(r.id),
          MAX(r.installmentNo),
          COALESCE(SUM(r.principalAmount), 0),
          COALESCE(SUM(r.interestAmount), 0),
          COALESCE(SUM(r.totalAmount), 0),
          COALESCE(SUM(r.amountPaid), 0),
          COALESCE(SUM(r.penalty), 0),
          COALESCE(SUM(r.totalAmount), 0)
              - COALESCE(SUM(r.amountPaid), 0)
      )
      FROM Repayments r
      WHERE r.loan = :l AND r.dueDate <= :end
      GROUP BY r.loan.id
      ORDER BY r.loan.id
      """)
  LoanRepaymentSummary getLoanRepaymentSummaries(Loan l, LocalDate end);

  // Sum amount paid
  @Query("""
      SELECT COALESCE(SUM(r.amountPaid), 0)
      FROM Repayments r
      WHERE r.loan = :loan
      """)
  BigDecimal sumAmountPaidByLoan(@Param("loan") Loan loan);

  // Sum total scheduled repayments
  @Query("""
      SELECT COALESCE(SUM(r.totalAmount), 0)
      FROM Repayments r
      WHERE r.loan = :loan
      """)
  BigDecimal sumTotalAmountByLoan(@Param("loan") Loan loan);

  Long countByLoan(Loan loan);
}
