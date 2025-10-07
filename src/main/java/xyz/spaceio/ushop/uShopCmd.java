package xyz.spaceio.ushop;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import org.jetbrains.annotations.NotNull;
import xyz.spaceio.ushop.item.CustomItem;
import xyz.spaceio.ushop.item.Flags;
import xyz.spaceio.ushop.item.SellableItem;

public class uShopCmd implements CommandExecutor {

	private final Main plugin;

	public uShopCmd(Main plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender cs, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
		if (!cs.hasPermission("ushop.admin")) {
			cs.sendMessage("§cYou dont have permissions to use this command!");
			return true;
		}

        if (args.length == 0) {
            showHelp(cs);
            return true;
        }

        String subCmd = args[0].toLowerCase();

        switch (subCmd) {
            case "add":
                if (args.length == 1) {
                    showHelp(cs);
                    break;
                }

                if (!(cs instanceof Player p)) {
                    cs.sendMessage("You need to be a player!");
                    return true;
                }

                if (p.getInventory().getItemInMainHand().getType() == Material.AIR) {
                    cs.sendMessage("§cYou need to hold an item in your hand!");
                    return true;
                }

                ItemStack inHand = p.getInventory().getItemInMainHand();
                double price = Double.parseDouble(args[1]);

                CustomItem newCItem = new CustomItem(this.plugin, p.getInventory().getItemInMainHand(), price);

                if (args.length > 2) {
                    // handling flags
                    for (int i = 2; i < args.length; i++) {
                        String flagName = args[i].toUpperCase();
                        try {
                            Flags flag = Flags.valueOf(flagName);
                            newCItem.addFlag(flag);
                        } catch (Exception e) {
                            cs.sendMessage("§cFlag " + flagName + " not found. Valid flags are:");
                            List<String> flags = Arrays.stream(Flags.values()).map(flag -> flag.name().toLowerCase()).collect(Collectors.toList());
                            cs.sendMessage("§a" + String.join(", ", flags));
                            return true;
                        }
                    }
                }

                Optional<SellableItem> result = plugin.findSellableItem(inHand);
                if (result.isPresent()) {
                    if (!(result.get() instanceof CustomItem customItem)) {
                        p.sendMessage("&cThis item is not able to be manually set");
                        return true;
                    }

                    plugin.getCustomItems().remove(customItem);
                    p.sendMessage("§aSuccessfully updated item:");
                } else
                    p.sendMessage("§aSuccessfully added item:");

                plugin.addCustomItem(newCItem);
                plugin.saveMainConfig();

                p.sendMessage(plugin.getCustomItemDescription(newCItem, 1).toArray(String[]::new));
                return true;

            case "reload":
                cs.sendMessage("§aConfig.yml reloaded!");
                plugin.reloadItems();
                return true;

            case "open":
                if (args.length == 1) {
                    cs.sendMessage("§cYou need to specify a username!");
                    return true;
                }

                String playername = args[1];
                Player p = Bukkit.getPlayer(playername);

                if (p == null) {
                    cs.sendMessage("§cPlayer is not online or doesnt exist.");
                    return true;
                }

                plugin.openShop(p);
                cs.sendMessage("§cShop opened.");
                return true;

            case "convert":
                EssentialsWorthConverter.convert(plugin, cs);
                return true;
        }

        showHelp(cs);
		return true;
	}

	private void showHelp(CommandSender cs) {
		cs.sendMessage("§c -- uShop v" + plugin.getDescription().getVersion() + " help: --");
		cs.sendMessage("§e/ushop §areload §r- reloads the config");
		cs.sendMessage("§e/ushop §aadd <price> [flags ...] §r- sets a custom price for an item with custom lore, displayname, durability and enchants");
		cs.sendMessage("§e/ushop §aopen <player> §r- opens the shop for other players");
		cs.sendMessage("§e/ushop §aconvert §r- will convert your essentials worth list to the ushop one");
		cs.sendMessage("§cCurrently configured custom items (with NBT Data): §a" + plugin.getCustomItemCount());
	}

}
