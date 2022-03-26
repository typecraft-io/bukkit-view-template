package my.sample.plugin;

import io.typecraft.bukkit.view.*;
import io.typecraft.bukkit.view.page.PageContext;
import io.typecraft.bukkit.view.page.PageViewLayout;
import my.sample.plugin.view.ItemListView;
import my.sample.plugin.view.PlayerChestView;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MyPlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        BukkitView.register(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player p = sender instanceof Player ? ((Player) sender) : null;
        String head = args.length >= 1 ? args[0] : "";
        switch (head) {
            case "page": {
                if (p != null && p.isOp()) {
                    ChestView view = ItemListView.create(
                            Arrays.stream(Material.values())
                                    .filter(mat -> mat.isItem() && !mat.isAir())
                    );
                    BukkitView.openView(view, p, this);
                }
                break;
            }
            case "chest": {
                if (p != null && p.isOp()) {
                    ChestView view = PlayerChestView.create();
                    BukkitView.openView(view, p, this);
                }
                break;
            }
            default: {
                sender.sendMessage(String.format("§a/%s page: §fshow a demo pagination view.", label));
                sender.sendMessage(String.format("§a/%s chest: §fshow a demo chest view.", label));
                break;
            }
        }
        return true;
    }
}
