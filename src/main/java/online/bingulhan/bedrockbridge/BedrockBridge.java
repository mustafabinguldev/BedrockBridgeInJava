package online.bingulhan.bedrockbridge;

import org.bukkit.plugin.java.JavaPlugin;


import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class BedrockBridge extends JavaPlugin implements Listener {
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, (Plugin)this);
    }

    @EventHandler
    public void event(PlayerInteractEvent e) {
        if (e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            int pitch = (int)e.getPlayer().getLocation().getPitch();
            Location down = e.getPlayer().getLocation().clone().add(0.0D, -1.0D, 0.0D);
            if (!down.getBlock().getType().isSolid())
                return;
            if (e.getPlayer().getInventory().getItemInHand() != null) {
                if (!e.getPlayer().getInventory().getItemInHand().getType().isBlock())
                    return;
                if (e.getPlayer().getInventory().getItemInHand().getType().equals(Material.AIR))
                    return;
                if (pitch < BridgeValue.MIN_PITCH && pitch > BridgeValue.MAX_PITCH) {
                    Location location = e.getPlayer().getLocation().clone().add(e.getPlayer().getLocation().getDirection().getX(), -1.0D, e.getPlayer().getLocation().getDirection().getZ());
                    if (location.getBlock().getType().equals(Material.AIR)) {
                        if (e.getClickedBlock() != null) {
                            Location cLoc = e.getClickedBlock().getLocation();
                            if (cLoc.getBlockX() != location.getBlockX() || cLoc.getBlockZ() != location.getBlockZ())
                                return;
                        }
                        Material material = e.getPlayer().getInventory().getItemInHand().getType();
                        ItemStack itemStack = e.getPlayer().getItemInHand();
                        BlockPlaceEvent event = new BlockPlaceEvent(location.getBlock(), location.getBlock().getState(), location.getBlock(), itemStack, e.getPlayer(), true);
                        getServer().getPluginManager().callEvent((Event)event);
                        if (!event.isCancelled()) {
                            location.getBlock().setType(material);
                            location.getBlock().getState().setType(itemStack.getData().getItemType());
                            location.getBlock().getState().setData(itemStack.getData());
                            location.getBlock().setData(itemStack.getData().getData());
                            if (e.getPlayer().getGameMode().equals(GameMode.SURVIVAL))
                                if (itemStack.getAmount() != 1) {
                                    e.getPlayer().getInventory().getItemInHand().setAmount(e.getPlayer().getInventory().getItemInHand().getAmount() - 1);
                                } else {
                                    e.getPlayer().getInventory().setItemInHand(new ItemStack(Material.AIR));
                                }
                        }
                    }
                }
            }
        }
    }
}

