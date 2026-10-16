package com.mfano.mfes.models;

import org.jspecify.annotations.NonNull;

import com.mfano.mfes.auth.models.CommonObject;
import com.mfano.mfes.auth.models.User;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter 
@Getter 
@Entity
@NoArgsConstructor 
@Table(name = "portfolios")
public class Portfolio extends CommonObject{
    @NonNull 
    private int capacity = 0;
    private double sales = 0.00;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User user;

   
}
