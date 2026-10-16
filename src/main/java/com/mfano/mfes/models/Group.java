package com.mfano.mfes.models;

import java.math.BigDecimal;

import com.mfano.mfes.auth.models.Branch;
import com.mfano.mfes.auth.models.CommonObject;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Entity
@NoArgsConstructor
@Table(name = "vikundi")
public class Group extends CommonObject {
    @Column(precision = 9, scale = 6)
    @DecimalMin("-90.0")
    @DecimalMax("90.0")
    private BigDecimal latitude;

    @Column(precision = 10, scale = 6)
    @DecimalMin("-180.0")
    @DecimalMax("180.0")
    private BigDecimal longitude;

    private String phone;
    private String meetingDay;
    private String meetingTime;
    private String document;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "branch_id")
    private Branch branch;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "portfolio_id")
    private Portfolio folio;

}