package com.copick.groups.entity;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "group_members")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@IdClass(GroupMemberId.class)
public class GroupMember {
    @Id
    @Column(name = "group_id")
    private Integer groupId;

    @Id
    @Column(name = "profile_id")
    private Integer profileId;

    @Convert(converter = GroupRoleConverter.class)
    @Column(name = "role", nullable = false, length = 16)
    private GroupRole role;

    @CreationTimestamp
    @Column(name = "joined_at", updatable = false)
    private OffsetDateTime joinedAt;
}