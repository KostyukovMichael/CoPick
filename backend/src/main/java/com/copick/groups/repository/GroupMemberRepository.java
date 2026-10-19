package com.copick.groups.repository;

import com.copick.groups.entity.GroupMember;
import com.copick.groups.entity.GroupMemberId;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupMemberRepository extends JpaRepository<GroupMember, GroupMemberId> {
    // Возвращает список ID пользователей, состоящих в группе.
    @Query("select gm.id.profileId from GroupMember gm where gm.id.groupId = :groupId")
    List<Integer> findProfileIdsByGroupId(@Param("groupId") Integer groupId);

    // Возвращает список ID групп, в которых состоит пользователь.
    @Query("select gm.id.groupId from GroupMember gm where gm.id.profileId = :profileId")
    List<Integer> findGroupIdsByProfileId(@Param("profileId") Integer profileId);
}