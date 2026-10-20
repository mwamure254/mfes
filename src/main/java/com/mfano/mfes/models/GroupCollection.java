package com.mfano.mfes.models;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.mfano.mfes.auth.models.BaseObject;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "group_collection")
public class GroupCollection extends BaseObject {
    @NotNull
    private Long groupId;

    @NotBlank
    private String paymentMethod = "CASH";

    @Size(max = 100)
    private String paymentReference;

    @NotNull
    private String postedBy;

    @NotNull
    private BigDecimal paymentAmount = BigDecimal.ZERO;

    @OneToMany(mappedBy = "collectionId", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MemberCollection> members = new ArrayList<>();
}
