package com.stryserbox.hardmodeperplayer;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

/**
 * Aplica el comportamiento tipo Hard vanilla solo a los jugadores que tengan
 * el permiso hardmodeperplayer.hard (gestionado con LuckPerms).
 *
 * La muerte por hambre se gestiona aparte, con una tarea periodica en
 * HardModePerPlayer#starveHardPlayers, porque Bukkit no expone eso mediante
 * un evento simple.
 */
public class HardModeListener implements Listener {

    private final HardModePerPlayer plugin;

    public HardModeListener(HardModePerPlayer plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        if (!(event.getDamager() instanceof LivingEntity)) {
            return;
        }
        if (!plugin.isHardPlayer(player)) {
            return;
        }
        double multiplier = plugin.getIncomingDamageMultiplier();
        if (multiplier != 1.0) {
            event.setDamage(event.getDamage() * multiplier);
        }
    }
}
