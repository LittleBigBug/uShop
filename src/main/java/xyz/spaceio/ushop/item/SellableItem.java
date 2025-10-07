package xyz.spaceio.ushop.item;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.spaceio.ushop.Main;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class SellableItem {

    protected final Main plugin;
    protected final ItemStack item;

    protected final Map<String, Integer> enchantments;
    protected String displayname;

    protected boolean hasMeta = false;
    protected boolean hasCustomModelData = false;

    protected short durability;
    protected List<String> lore;

    public SellableItem(Main plugin, ItemStack is) {
        this.plugin = plugin;
        this.item = is;

        this.durability = is.getDurability();

        ItemMeta im = is.getItemMeta();
        if (im != null) {
            this.hasMeta = true;
            this.hasCustomModelData = im.hasCustomModelDataComponent() || im.hasCustomModelData();
            if (im.hasDisplayName())
                this.displayname = im.getDisplayName();
            if (im.hasLore())
                this.lore = im.getLore();
        }

        this.enchantments = is.getEnchantments().entrySet().stream().collect(Collectors.toMap(x -> x.getKey().getKey().getKey(), x -> x.getValue()));
    }

    /**
     * Returns if this custom item has an item meta and enchantment on it
     */
    public boolean isSimpleItem() {
        if (hasMeta) return false;
        return this.enchantments == null || this.enchantments.isEmpty();
    }

    public abstract double getPrice();

    public Material getMaterial() { return this.item.getType(); }

    public String getMaterialStr() { return this.item.getType().toString(); }

    public Map<String, Integer> getEnchantments() { return enchantments; }

    public String getDisplayname() { return displayname; }

    public boolean hasMeta() { return hasMeta; }

    public boolean hasCustomModelData() { return hasCustomModelData; }

    public short getDurability() { return durability; }

    public List<String> getLore() { return lore; }

    public List<Flags> getFlags() { return new ArrayList<>(); }

}
