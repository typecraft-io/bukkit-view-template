package my.sample.plugin;

import io.typecraft.bukkit.view.BukkitView;
import io.typecraft.bukkit.view.ChestView;
import io.typecraft.bukkit.view.ViewAction;
import io.typecraft.bukkit.view.ViewItem;
import io.typecraft.bukkit.view.page.PageContext;
import io.typecraft.bukkit.view.page.PageViewLayout;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MyPlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        BukkitView.register(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = sender instanceof Player ? ((Player) sender) : null;
        String cmdName = command.getName();
        if (cmdName.equals("myplugincommand")) {
            String head = args.length >= 1 ? args[0] : "";
            switch (head.toLowerCase()) {
                case "chest": {
                    if (player != null) {
                        ChestView chestView = createChestView();
                        BukkitView.openView(chestView, player, this);
                    }
                    break;
                }
                case "page": {
                    if (player != null) {
                        PageViewLayout layout = createMyViewLayout();
                        ChestView chestView = layout.toView(1);
                        BukkitView.openView(chestView, player, this);
                    }
                    break;
                }
                default: {
                    sender.sendMessage(String.format("§a/%s page: §fshow a demo pagination view.", label));
                    sender.sendMessage(String.format("§a/%s chest: §fshow a demo chest view.", label));
                    break;
                }
            }
        }
        return true;
    }

    private static ChestView createChestView() {
        Map<Integer, ViewItem> controls = new HashMap<>();
        // set wall 0~8
        ViewItem wall = ViewItem.just(new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
        for (int i = 0; i < 9; i++) {
            controls.put(i, wall);
        }
        // set show button at 7
        ItemStack tpViewItem = createItemStack(Material.PLAYER_HEAD, "§aShow players", Collections.emptyList());
        controls.put(7, ViewItem.of(tpViewItem, e -> new ViewAction.Open(createMyViewLayout().toView(1))));
        // set exit button at 8
        ItemStack barrierItem = createItemStack(Material.BARRIER, "§cEXIT", Collections.emptyList());
        controls.put(8, ViewItem.of(barrierItem, e -> ViewAction.CLOSE));
        // return
        return new ChestView("bukkit-view chest", 6, controls);
    }

    private static PageViewLayout createMyViewLayout() {
        List<Function<PageContext, ViewItem>> pagingContents = Bukkit.getOnlinePlayers().stream()
                .map(p -> (Function<PageContext, ViewItem>) ctx -> {
                    ItemStack headItem = new ItemStack(Material.PLAYER_HEAD);
                    SkullMeta meta = (SkullMeta) headItem.getItemMeta();
                    if (meta != null) {
                        meta.setOwningPlayer(p);
                        headItem.setItemMeta(meta);
                    }
                    return ViewItem.of(headItem, e -> {
                        Player clicker = e.getClicker();
                        if (!p.isOnline()) {
                            clicker.sendMessage(ChatColor.RED + "Player '" + p.getName() + "' not in online!");
                        } else if (clicker.isOp()) {
                            clicker.teleport(p.getLocation());
                            return ViewAction.CLOSE;
                        } else {
                            clicker.sendMessage(ChatColor.RED + "You are not a op!");
                        }
                        return ViewAction.NOTHING;
                    });
                })
                .collect(Collectors.toList());
        return PageViewLayout.ofDefault(
                "bukkit-view page",
                6,
                Material.STONE_BUTTON,
                pagingContents
        );
    }

    private static ItemStack createItemStack(Material material, String display, List<String> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            if (!display.isEmpty()) {
                meta.setDisplayName(display);
            }
            if (!lore.isEmpty()) {
                meta.setLore(lore);
            }
            item.setItemMeta(meta);
        }
        return item;
    }
}
