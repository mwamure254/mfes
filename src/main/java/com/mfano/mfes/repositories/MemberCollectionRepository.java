package com.mfano.mfes.repositories;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.mfano.mfes.models.MemberCollection;

public interface MemberCollectionRepository extends JpaRepository<MemberCollection, Long>{

    Optional<MemberCollection> findByMemberId(Long id);
    @Query("""
        SELECT COALESCE(SUM(m.savingsAmount), 0)
        FROM MemberCollection m
        WHERE m.memberId = :memberId
    """)
    BigDecimal getSavingsTotal(@Param("memberId") Long memberId);

    @Query("""
        SELECT COALESCE(SUM(m.loanAmount), 0)
        FROM MemberCollection m
        WHERE m.memberId = :memberId
    """)
    BigDecimal getLoanTotal(@Param("memberId") Long memberId);
    
     @Query("""
        SELECT COALESCE(SUM(m.savingsAmount), 0)
        FROM MemberCollection m
    """)
    BigDecimal getSavings();

    @Query("""
        SELECT COALESCE(SUM(m.loanAmount), 0)
        FROM MemberCollection m
    """)
    BigDecimal getLoans();
}
