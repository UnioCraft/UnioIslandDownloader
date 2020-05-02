package me.UnioDex.IslandDownloader;

import com.wasteofplastic.askyblock.ASkyBlock;
import com.wasteofplastic.askyblock.ASkyBlockAPI;
import com.wasteofplastic.askyblock.Island;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

public class Main extends JavaPlugin implements Listener {

    private ASkyBlock aSkyBlock;

    public void onEnable() {
        if (Bukkit.getPluginManager().isPluginEnabled("ASkyBlock")) {
            aSkyBlock = (ASkyBlock) Bukkit.getPluginManager().getPlugin("ASkyBlock");
        }

        if (aSkyBlock == null) {
            System.out.println("ASkyBlock couldn't find.");
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }

    public boolean onCommand(final CommandSender sender, Command cmd, String commandLabel, final String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Player only command.");
            return false;
        }
        Player p = (Player) sender;

        if (!isOnIsland(p)) {
            sender.sendMessage(ChatColor.RED + "Bu komutu kullanabilmek için adanızda olmalısınız.");
            return true;
        }
        sender.sendMessage(ChatColor.GREEN + "Adanız indirilmeye hazırlanıyor. Lütfen bekleyin.");
        downloadIsland(p);
        return true;
    }

    private boolean isOnIsland(Player p) {
        if (aSkyBlock != null) {
            return ASkyBlockAPI.getInstance().playerIsOnIsland(p);
        }
        return false;
    }


    private void downloadIsland(Player player) {
        if (aSkyBlock != null) {
            downloadIslandAcidIsland(player);
        }
    }

    private void downloadIslandAcidIsland(Player player) {
        Island island = aSkyBlock.getGrid().getIsland(player.getUniqueId());
        int minX = island.getMinX();
        int maxX = island.getMinX() + island.getIslandDistance() - 1;
        int minZ = island.getMinZ();
        int maxZ = island.getMinZ() + island.getIslandDistance() - 1;
        saveSchematic(player, minX, maxX, minZ, maxZ);
    }

    private boolean checkSchematicExist(String fileName) {
        File file = new File(Bukkit.getWorldContainer().getPath() + "/plugins/WorldEdit/schematics/" + fileName);
        return file.exists();
    }

    private void sendBosMesaj(Player p) {
        p.sendMessage(" ");
        p.sendMessage(" ");
        p.sendMessage(" ");
        p.sendMessage(" ");
        p.sendMessage(" ");
        p.sendMessage(" ");
        p.sendMessage(" ");
        p.sendMessage(" ");
        p.sendMessage(" ");
        p.sendMessage(" ");
    }

    private void saveSchematic(Player p, int minX, int maxX, int minZ, int maxZ) {
        if (checkSchematicExist(minX + "," + minZ + "," + maxX + "," + maxZ + ".schematic")) {
            if (aSkyBlock != null) {
                sendBosMesaj(p);
                p.sendMessage(ChatColor.AQUA + "Adanız zaten sitemize yüklenmiş. İndirmek için tıklayın: " + ChatColor.BLUE + ChatColor.UNDERLINE + "https://www.uniocraft.com/download/skyblock/" + minX + "," + minZ + "," + maxX + "," + maxZ + ".schematic");
            }
            return;
        }

        p.performCommand("/pos1 " + minX + ",0," + minZ);
        Bukkit.getScheduler().runTaskLater(this, () -> {
            p.performCommand("/pos2 " + maxX + ",255," + maxZ);
        }, 2L);
        Bukkit.getScheduler().runTaskLater(this, () -> {
            p.performCommand("/copy");
        }, 4L);
        Bukkit.getScheduler().runTaskLater(this, () -> {
            p.performCommand("/schem save " + minX + "," + minZ + "," + maxX + "," + maxZ);
        }, 14L);
        Bukkit.getScheduler().runTaskLater(this, () -> {
            sendBosMesaj(p);
        }, 20L);
        Bukkit.getScheduler().runTaskLater(this, () -> {
            sendBosMesaj(p);
            sendToWebSite(p, minX + "," + minZ + "," + maxX + "," + maxZ + ".schematic");
        }, 40L);
    }

    private void sendToWebSite(Player p, String fileName) {
        if (fileName == null) return;
        String[] command = {"cp", "-rf", "/home/server/Skyblock/plugins/WorldEdit/schematics/" + fileName, "/var/www/uniocraft.com/download/skyblock"};
        runCommand(p, command, fileName);
    }

    private void runCommand(Player player, String[] cmd, String fileName) {
        try {
            System.out.println("Executing command: " + cmd.toString());
            Process p = Runtime.getRuntime().exec(cmd);
            int result = p.waitFor();

            System.out.println("Process exit code: " + result);
            System.out.println();
            System.out.println("Result:");
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

            String line = "";
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            if (aSkyBlock != null) {
                sendBosMesaj(player);
                player.sendMessage(ChatColor.AQUA + "Adanız başarıyla sitemize yüklendi. İndirmek için tıklayın: " + ChatColor.BLUE + ChatColor.UNDERLINE + "https://www.uniocraft.com/download/skyblock/" + fileName);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
