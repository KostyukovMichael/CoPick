package com.copick.groups.service;

import com.copick.groups.entity.Group;
import com.copick.groups.entity.GroupMember;
import com.copick.groups.entity.GroupRole;
import com.copick.groups.exception.AlreadyMemberException;
import com.copick.groups.exception.GroupNotFoundException;
import com.copick.groups.exception.InviteCodeGenerationException;
import com.copick.groups.repository.GroupMemberRepository;
import com.copick.groups.repository.GroupRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GroupService {

    private static final int INVITE_CODE_LENGTH = 8;
    private static final int MAX_INVITE_CODE_ATTEMPTS = 5;

    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;

    /** Создает новую группу и делает создателя владельцем */
    @Transactional
    public Group createGroup(String name, Short ageRestriction, Integer creatorProfileId) {
        Group group = new Group();
        group.setName(name);
        group.setAgeRestriction(ageRestriction);
        group.setInviteCode(generateUniqueInviteCode());

        group = groupRepository.save(group);

        GroupMember member = new GroupMember();
        member.setGroupId(group.getGroupId());
        member.setProfileId(creatorProfileId);
        member.setRole(GroupRole.OWNER);
        groupMemberRepository.save(member);

        return group;
    }

    /** Вступление в группу по коду */
    @Transactional
    public Group joinGroup(String inviteCode, Integer profileId) {
        Group group =
            groupRepository
                .findByInviteCode(inviteCode)
                .orElseThrow(() -> new GroupNotFoundException(inviteCode));

        if (groupMemberRepository.existsByGroupIdAndProfileId(group.getGroupId(), profileId)) {
          throw new AlreadyMemberException(group.getGroupId(), profileId);
        }

        GroupMember member = new GroupMember();
        member.setGroupId(group.getGroupId());
        member.setProfileId(profileId);
        member.setRole(GroupRole.MEMBER);
        groupMemberRepository.save(member);

        return group;
    }

    /**
    * Генерирует invite-код с защитой от коллизий. Делает до MAX_INVITE_CODE_ATTEMPTS
    * попыток проверить уникальность через репозиторий.
    */
    private String generateUniqueInviteCode() {
        for (int attempt = 0; attempt < MAX_INVITE_CODE_ATTEMPTS; attempt++) {
            String code = UUID.randomUUID().toString().substring(0, INVITE_CODE_LENGTH).toUpperCase();
            if (!groupRepository.existsByInviteCode(code)) {
                return code;
            }
        }
        throw new InviteCodeGenerationException(MAX_INVITE_CODE_ATTEMPTS);
    }
}
