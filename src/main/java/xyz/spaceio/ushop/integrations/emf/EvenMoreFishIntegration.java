package xyz.spaceio.ushop.integrations.emf;

import xyz.spaceio.ushop.Main;
import com.oheers.fish.FishUtils;
import com.oheers.fish.selling.WorthNBT;
import org.bukkit.inventory.ItemStack;

public class EvenMoreFishIntegration {

    private final Main plugin;

    public EvenMoreFishIntegration(Main plugin) {
        this.plugin = plugin;
    }

    public double getFishPrice(ItemStack item) {
        return WorthNBT.getValue(item);
    }

    public EMFItem getEmfItem(ItemStack item) {
        if (FishUtils.isFish(item) && this.getFishPrice(item) > 0) return new EMFItem(this.plugin, item);
        return null;
    }

}
