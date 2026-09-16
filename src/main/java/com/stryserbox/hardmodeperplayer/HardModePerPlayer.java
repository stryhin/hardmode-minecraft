package com.stryserbox.hardmodeperplayer;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class HardModePerPlayer extends JavaPlugin {

    public static final String HARD_PERMISSION = "hardmodeperplayer.hard";

    private double incomingDamageMultiplier = 1.0;
    private boolean hungerCanKill = true;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadSettings();
        getServer().getPluginManager().registerEvents(new HardModeListener(this), this);
        getLogger().info("HardModePerPlayer activo. Se activa por el permiso '" + HARD_PERMISSION + "' (asignalo con LuckPerms).");

        // Replica la inanicion de Hard vanilla (el hambre mata hasta dejar
        // al jugador en 1 corazon) solo para quien tenga el permiso hard.
        // Se comprueba cada 80 ticks (4s), igual que el motor vanilla.
        Bukkit.getScheduler().runTaskTimer(this, this::starveHardPlayers, 80L, 80L);
    }

    private void starveHardPlayers() {
        if (!hungerCanKill) {
            return;
        }
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!isHardPlayer(player)) {
                continue;
            }
            if (player.getFoodLevel() <= 0 && player.getHealth() > 2.0) {
                double newHealth = Math.max(2.0, player.getHealth() - 1.0);
                player.setHealth(newHealth);
            }
        }
    }

    private void loadSettings() {
        reloadConfig();
        incomingDamageMultiplier = getConfig().getDouble("hard-incoming-damage-multiplier", 1.0);
        hungerCanKill = getConfig().getBoolean("hunger-can-kill", true);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!command.getName().equalsIgnoreCase("hardmode")) {
            return false;
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            loadSettings();
            sender.sendMessage("§a[HardModePerPlayer] Configuracion recargada.");
            return true;
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("check")) {
            Player target = Bukkit.getPlayerExact(args[1]);
            if (target == null) {
                sender.sendMessage("§c[HardModePerPlayer] Jugador no conectado: " + args[1]);
                return true;
            }
            boolean hard = isHardPlayer(target);
            sender.sendMessage("§e[HardModePerPlayer] " + target.getName() + " esta en modo " + (hard ? "§cDIFICIL" : "§aNORMAL"));
            return true;
        }

        sender.sendMessage("§eUso: /hardmode reload  |  /hardmode check <jugador>");
        return true;
    }

    /**
     * Un jugador esta en modo "hard" si tiene el permiso hardmodeperplayer.hard
     * en true. Asignalo/quitalo con LuckPerms:
     *   /lp user <jugador> permission set hardmodeperplayer.hard true
     *   /lp user <jugador> permission set hardmodeperplayer.hard false
     */
    public boolean isHardPlayer(Player player) {
        return player.hasPermission(HARD_PERMISSION);
    }

    public double getIncomingDamageMultiplier() {
        return incomingDamageMultiplier;
    }

    public boolean isHungerCanKill() {
        return hungerCanKill;
    }
}
