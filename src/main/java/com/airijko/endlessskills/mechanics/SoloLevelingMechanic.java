package com.airijko.endlessskills.mechanics;

import com.airijko.endlesscore.permissions.Permissions;
import com.airijko.endlessskills.managers.PlayerDataManager;

public class SoloLevelingMechanic {
    private PlayerDataManager playerDataManager;
    private Permissions permissions;

    public SoloLevelingMechanic(PlayerDataManager playerDataManager, Permissions permissions) {
        this.playerDataManager = playerDataManager;
        this.permissions = permissions;
    }

//    public void handlePlayerDeath() {
//        if (!soloLeveler(player)) {
//
//        }
//    }

    public void soloLeveler(Player player) {
        if (permissions.hasPermission(player,"endlessskills.solo.leveling")) {
            return true;
        }
        return false;
    }
}
