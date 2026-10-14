package com.mfano.mfes.models;

import java.math.BigDecimal;

import com.mfano.mfes.auth.models.BaseObject;
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
import lombok.Setter;

@Entity
@Setter 
@Getter 
@Table(name="clients")
public class Client extends BaseObject{
    private String fin;
    private String lan;
    private String idNumber;
    private String phone;
    private String document;
    private boolean active = false;
    
    @Column(precision = 9, scale = 6)
    @DecimalMin("-90.0") @DecimalMax("90.0")
    private BigDecimal latitude;

    @Column(precision = 10, scale = 6)
    @DecimalMin("-180.0") @DecimalMax("180.0") 
    private BigDecimal longitude;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "group_id")
    private Group group;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "branch_id")
    private Branch branch;
}