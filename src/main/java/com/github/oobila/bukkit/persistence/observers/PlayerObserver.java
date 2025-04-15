package com.github.oobila.bukkit.persistence.observers;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

@SuppressWarnings("unused")
public interface PlayerObserver {

    void onJoin(Player player);

    void onLeave(OfflinePlayer player);

}
