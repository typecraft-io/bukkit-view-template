package my.sample.plugin;

import io.typecraft.bukkit.view.BukkitView;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class MyPlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        BukkitView.register(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String cmdName = command.getName();
        if (cmdName.equals("myplugincommand")) {
            sender.sendMessage("Hi!");
        }
        return true;
    }
}
