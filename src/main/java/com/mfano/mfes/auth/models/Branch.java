package com.mfano.mfes.auth.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "branches")
@Setter
@Getter
@NoArgsConstructor
public class Branch extends CommonObject {
    private String location;
    private String manager;
    private String contact;  
    private String email;
    private String address;
    private String createdBy;
}
