package com.copick.groups.repository;

import com.copick.groups.entity.GroupMember;
import com.copick.groups.entity.GroupMemberId;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupMemberRepository extends JpaRepository<GroupMember, GroupMemberId> {

    // Проверяет, состоит ли пользователь в группе
    boolean existsByGroupIdAndProfileId(Integer groupId, Integer profileId);

    // Возвращает список ID пользователей, состоящих в группе
    List<Integer> findProfileIdByGroupId(Integer groupId);

    // Возвращает список ID групп, в которых состоит пользователь
    List<Integer> findGroupIdByProfileId(Integer profileId);
}