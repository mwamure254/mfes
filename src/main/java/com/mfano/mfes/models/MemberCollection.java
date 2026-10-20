package com.mfano.mfes.models;

import java.math.BigDecimal;

import com.mfano.mfes.auth.models.BaseObject;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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
    
    @NotNull
    private Long collectionId;

    @DecimalMin("0.00")
    private BigDecimal savingsAmount = BigDecimal.ZERO;

    @DecimalMin("0.00")
    private BigDecimal loanAmount = BigDecimal.ZERO;
}
