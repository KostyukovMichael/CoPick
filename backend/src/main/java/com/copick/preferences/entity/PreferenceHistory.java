package com.copick.preferences.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "preference_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PreferenceHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "history_id")
    private Integer historyId;

    @Column(name = "profile_id", nullable = false)
    private Integer profileId;

    @Column(name = "genre_id", nullable = false)
    private Integer genreId;

    @Column(name = "old_weight", precision = 3, scale = 2)
    private BigDecimal oldWeight;

    @Column(name = "new_weight", precision = 3, scale = 2)
    private BigDecimal newWeight;

    @CreationTimestamp
    @Column(name = "changed_at", updatable = false)
    private OffsetDateTime changedAt;
}