package com.mfano.mfes.models;

import java.math.BigDecimal;

import com.mfano.mfes.auth.models.BaseObject;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity 
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "member_collection")
public class MemberCollection extends BaseObject{
    @NotNull
    private Long memberId;

    @DecimalMin("0.00")
    private BigDecimal savingsAmount = BigDecimal.ZERO;

    @DecimalMin("0.00")
    private BigDecimal loanAmount = BigDecimal.ZERO;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "group_collection_id", nullable = false)
    private GroupCollection groupCollection;
}
