package com.copick.groups.service;

import com.copick.groups.entity.Group;
import com.copick.groups.entity.GroupMember;
import com.copick.groups.entity.GroupRole;
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

    /** Создает новую группу и делает создателя админом */
    @Transactional
    public Group createGroup(String name, Short ageRestriction, Integer creatorProfileId) {
        Group group = new Group();
        group.setName(name);
        group.setAgeRestriction(ageRestriction);
        group.setInviteCode(generateUniqueInviteCode());

        group = groupRepository.save(group);

        // Добавляем создателя как владельца
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
                .orElseThrow(() -> new RuntimeException("Группа с таким кодом не найдена"));

        // Проверка, не состоит ли уже пользователь в группе
        if (groupMemberRepository.existsByGroupIdAndProfileId(group.getGroupId(), profileId)) {
            throw new RuntimeException("Вы уже состоите в этой группе");
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

        throw new IllegalStateException(
            "Не удалось сгенерировать уникальный invite-код за "
            + MAX_INVITE_CODE_ATTEMPTS
            + " попыток");
    }
}
