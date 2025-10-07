package xyz.spaceio.ushop.item;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import xyz.spaceio.ushop.Main;

public class CustomItem extends SellableItem implements ConfigurationSerializable {

    private double price;

	private final List<Flags> flags = new ArrayList<>();

	public CustomItem(Main plugin, ItemStack is, double price) {
        super(plugin, is);

		this.price = price;
    }

	/**
	 * Checks whether a real item stack equals to this custom item setup
	 */
	public boolean matches(ItemStack is) {
        boolean hasMeta = false;
        boolean hasCustomModelData = false;
        if (
            !is.getType().equals(this.getMaterial()) ||
            (is.hasItemMeta() != hasMeta && !hasFlag(Flags.IGNORE_META)) ||
            (is.getDurability() != durability && !hasFlag(Flags.IGNORE_DURABILITY)) ||
            (is.hasItemMeta() && is.getItemMeta().hasCustomModelData() != hasCustomModelData && !hasFlag(Flags.IGNORE_CUSTOM_MODEL_DATA))
        ) {
			return false;
		}

        if (!hasMeta || hasFlag(Flags.IGNORE_META)) return true;

        if (!hasFlag(Flags.IGNORE_DISPLAYNAME)) {
            if (displayname == null && is.getItemMeta().hasDisplayName() || displayname != null && !is.getItemMeta().hasDisplayName())
                return false;

            if (displayname != null && is.getItemMeta().hasDisplayName() && !displayname.equals(is.getItemMeta().getDisplayName()))
                return false;
        }

        if (!hasFlag(Flags.IGNORE_ENCHANTMENTS)) {
            if (this.enchantments == null || is.getEnchantments().isEmpty())
                return false;

            boolean matchesEnchantments = is.getEnchantments().entrySet().stream().allMatch(entry -> {
                if(this.enchantments.containsKey(entry.getKey().getKey().getKey()))
                    return Objects.equals(entry.getValue(), this.enchantments.get(entry.getKey().getKey().getKey()));
                return false;
            });

            if(!matchesEnchantments) {
                return false;
            }
        }

        if (lore != null && is.getItemMeta().getLore().size() != 0 && !hasFlag(Flags.IGNORE_LORE)) {
            int[] matches = {0};
            lore.forEach((line) -> {
                if(is.getItemMeta().getLore().contains(line)) {
                    matches[0]++;
                }
            });
            return matches[0] == is.getItemMeta().getLore().size();
        }

        return true;
    }
	
	@NotNull
    @Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<>();

		for (Field field : this.getClass().getDeclaredFields()) {
			try {
				map.put(field.getName(), field.get(this));
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
        }

		return map;
	}

    @Override
    public double getPrice() { return price; }

	public void setPrice(double price) { this.price = price; }

	public void addFlag(Flags flag) { flags.add(flag); }

	public boolean hasFlag(Flags flag) { return flags.contains(flag); }

	public void removeFlag(Flags flag) { flags.remove(flag); }

}
