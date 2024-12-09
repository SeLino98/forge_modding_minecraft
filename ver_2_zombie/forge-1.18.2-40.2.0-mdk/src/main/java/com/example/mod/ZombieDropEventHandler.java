package com.example.mod;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Random;

@Mod.EventBusSubscriber(modid = "zombieraidmod", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ZombieDropEventHandler {
    @SubscribeEvent
    public static void onEntityDeath(LivingDropsEvent event) {
        if (!(event.getEntity() instanceof Zombie)) return;

        Random random = new Random();
        int chance = random.nextInt(100); // 0 ~ 99

        if (chance < 30) {
            // 30% 확률로 아이템 드롭
            ItemStack dropItem;
            switch (random.nextInt(3)) {
                case 0 -> dropItem = new ItemStack(Items.DIAMOND_BLOCK);
                case 1 -> dropItem = new ItemStack(Items.GOLD_BLOCK);
                default -> dropItem = new ItemStack(Items.IRON_BLOCK);
            }
            event.getDrops().add(new ItemEntity(event.getEntity().level, event.getEntity().getX(),
                    event.getEntity().getY(), event.getEntity().getZ(), dropItem));
        } else {
            // 70% 확률로 몹 소환
            for (int i = 0; i < 5; i++) {
                Zombie zombie = new Zombie(EntityType.ZOMBIE, event.getEntity().level);
                zombie.setPos(event.getEntity().getX() + random.nextInt(5) - 2,
                        event.getEntity().getY(), event.getEntity().getZ() + random.nextInt(5) - 2);
                event.getEntity().level.addFreshEntity(zombie);
            }
        }
    }
}
