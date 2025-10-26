package org.example.quizizz.service.Interface;

import org.example.quizizz.model.entity.PlayerProfile;

public interface IPlayerProfileService {
    void updateProfileAfterGame(Long userId, Long roomId);
    PlayerProfile getPlayerProfile(Long userId);
    void initializeProfile(Long userId, Integer age);
}
