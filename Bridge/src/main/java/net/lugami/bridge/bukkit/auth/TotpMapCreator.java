package net.lugami.bridge.bukkit.auth;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapView;
import net.lugami.bridge.bukkit.Bridge;

import java.awt.image.BufferedImage;

public class TotpMapCreator {

    public ItemStack createMap(Player player, BufferedImage bufferedImage) {
        MapView mapView = Bridge.getInstance().getServer().createMap(player.getWorld());
        mapView.getRenderers().forEach(mapView::removeRenderer);
        mapView.addRenderer(new TotpMapRenderer(player.getUniqueId(), bufferedImage));
        player.sendMap(mapView);
        return new ItemStack(Material.MAP, 0, mapView.getId());
    }

}
