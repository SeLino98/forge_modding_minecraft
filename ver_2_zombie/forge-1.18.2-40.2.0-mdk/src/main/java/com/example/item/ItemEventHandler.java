package com.example.item;

import com.example.mod.RaidManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "yourmodid", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ItemEventHandler {

    @SubscribeEvent
    public static void onItemConsumed(LivingEntityUseItemEvent.Finish event) {
        // Check if the entity is a player and the item is the "알 수 없는 사과"
        if (event.getEntity() instanceof ServerPlayer player) {
            ItemStack consumedItem = event.getItem();
            if (consumedItem.getItem() == ModItems.UNKNOWN_APPLE) {
                // Trigger the raid
                RaidManager.startRaid(player);
            }
        }
    }
}
