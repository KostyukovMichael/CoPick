package com.copick.preferences.service;

import com.copick.preferences.dto.PreferenceUpdateDto;
import com.copick.preferences.entity.PreferenceHistory;
import com.copick.preferences.entity.ProfilePreference;
import com.copick.preferences.exception.InvalidWeightException;
import com.copick.preferences.repository.PreferenceHistoryRepository;
import com.copick.preferences.repository.ProfilePreferenceRepository;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PreferenceService {

    private static final BigDecimal WEIGHT_MIN = BigDecimal.ONE.negate(); // -1
    private static final BigDecimal WEIGHT_MAX = BigDecimal.ONE; //  1

    private final ProfilePreferenceRepository preferenceRepository;
    private final PreferenceHistoryRepository historyRepository;

    @Transactional
    public void updatePreferences(Integer profileId, List<PreferenceUpdateDto> updates) {
        for (PreferenceUpdateDto dto : updates) {
            validateWeight(dto.getWeight());
        }

        for (PreferenceUpdateDto dto : updates) {
            ProfilePreference pref = preferenceRepository
                    .findByProfileIdAndGenreId(profileId, dto.getGenreId())
                    .orElseGet(() -> {
                        ProfilePreference newPref = new ProfilePreference();
                        newPref.setProfileId(profileId);
                        newPref.setGenreId(dto.getGenreId());
                        return newPref;
                    });

            BigDecimal oldWeight = pref.getWeight();

            pref.setWeight(dto.getWeight());
            preferenceRepository.save(pref);

            PreferenceHistory history = new PreferenceHistory();
            history.setProfileId(profileId);
            history.setGenreId(dto.getGenreId());
            history.setOldWeight(oldWeight);
            history.setNewWeight(dto.getWeight());
            historyRepository.save(history);

            log.info("Обновлено предпочтение: profile={}, genre={}, old={}, new={}",
                    profileId, dto.getGenreId(), oldWeight, dto.getWeight());
        }
    }

    /** Получает все предпочтения пользователя */
    public List<ProfilePreference> getPreferences(Integer profileId) {
        return preferenceRepository.findByProfileId(profileId);
    }

    private void validateWeight(BigDecimal weight) {
        if (weight == null) {
            throw new InvalidWeightException();
        }
        if (weight.compareTo(WEIGHT_MIN) < 0 || weight.compareTo(WEIGHT_MAX) > 0) {
            throw new InvalidWeightException(weight);
        }
    }
}