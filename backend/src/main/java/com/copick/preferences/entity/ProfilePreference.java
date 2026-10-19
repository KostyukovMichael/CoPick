package com.copick.preferences.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "profile_preferences")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@IdClass(ProfilePreferenceId.class)
public class ProfilePreference {
    @Id
    @Column(name = "profile_id")
    private Integer profileId;

    @Id
    @Column(name = "genre_id")
    private Integer genreId;

    @Column(name = "weight", nullable = false, precision = 3, scale = 2)
    private BigDecimal weight;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;
}