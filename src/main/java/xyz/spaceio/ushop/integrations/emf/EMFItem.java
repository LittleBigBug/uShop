package xyz.spaceio.ushop.integrations.emf;

import org.bukkit.inventory.ItemStack;
import xyz.spaceio.ushop.Main;
import xyz.spaceio.ushop.item.SellableItem;

public class EMFItem extends SellableItem {

    public EMFItem(Main plugin, ItemStack is) {
        super(plugin, is);
    }

    @Override
    public double getPrice() {
        var emf = this.plugin.getEmf();
        if (emf == null) return -1;
        return emf.getFishPrice(this.item);
    }

}