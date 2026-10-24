package com.mfano.mfes.repositories;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.mfano.mfes.models.Client;
import com.mfano.mfes.models.Loan;
import com.mfano.mfes.models.Loan.LoanStatus;

public interface LoanRepository extends JpaRepository<Loan, Long>{

    List<Loan> findAllByOrderByCreatedAtDesc();

    Loan findByReference(String ref);
    List<Loan> findByStatus(LoanStatus s);

    List<Loan> findByClient(Client c);
    
    @Query("""
        SELECT DISTINCT l
        FROM Loan l
        JOIN Repayments r ON r.loan = l
        WHERE r.dueDate <= :today
          AND COALESCE(r.amountPaid, 0) < COALESCE(r.totalAmount, 0)
          AND l.status = 'DISBURSED'
        ORDER BY l.id DESC
        """)
    List<Loan> findLoansWithOverdueInstallments(
            @Param("today") LocalDate today);

             @Query("""
                SELECT DISTINCT l
        FROM Loan l
        JOIN Repayments r ON r.loan = l
        WHERE r.dueDate BETWEEN :start AND :end
          AND COALESCE(r.amountPaid, 0) < COALESCE(r.totalAmount, 0)
          AND l.status = 'DISBURSED'
        ORDER BY l.id DESC
      """)
  List<Loan> findOverdueBetween(
      @Param("start") LocalDate start,
      @Param("end") LocalDate end);

      @Query("""
    SELECT COUNT(l)
    FROM Loan l
    WHERE l.status = :status
      AND l.createdAt >= :startOfDay
      AND l.createdAt < :startOfTomorrow
""")
long countLoansCreatedTodayByStatus(
        @Param("status") String status,
        @Param("startOfDay") LocalDateTime startOfDay,
        @Param("startOfTomorrow") LocalDateTime startOfTomorrow
);
}
