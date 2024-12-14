package com.example.mod;

import com.example.item.ModItems;
import net.minecraft.advancements.critereon.ItemUsedOnBlockTrigger;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.item.ItemEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.network.chat.Component;
import java.util.Random;


@Mod.EventBusSubscriber(modid = "unknownapplemod", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class RaidManager {

    private static ServerBossEvent bossBar = new ServerBossEvent(
            new TextComponent("Zombie Raid Progress"), BossEvent.BossBarColor.RED, BossEvent.BossBarOverlay.PROGRESS);

    private static int raidStage = 0;
    private static int zombieCount = 0;

    public static void startRaid(ServerPlayer player) {
        bossBar.addPlayer(player);
        bossBar.setProgress(0);
        raidStage = 1;
        spawnRaidZombies(player.getLevel(), player.blockPosition());
    }

    @SubscribeEvent
    public static void onWorldTick(TickEvent.WorldTickEvent event) {
        if (raidStage == 0) return;

        if (zombieCount <= 0) {
            raidStage++;
            bossBar.setProgress(raidStage / 3.0f);
            if (raidStage > 3) {
                bossBar.removeAllPlayers();
                raidStage = 0;
                return;
            }
            spawnRaidZombies(event.world, event.world.getNearestPlayer((Entity) event.world.players(), 50).blockPosition());
        }
    }

    private static void spawnRaidZombies(Level world, BlockPos pos) {
        Random random = new Random();
        for (int i = 0; i < raidStage * 5; i++) {
            Zombie zombie = new Zombie(EntityType.ZOMBIE, world);
            zombie.setPos(pos.getX() + random.nextInt(10) - 5, pos.getY(), pos.getZ() + random.nextInt(10) - 5);
            world.addFreshEntity(zombie);
            zombieCount++;
        }
    }

    @SubscribeEvent
    public static void onZombieDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof Zombie && raidStage > 0) {
            zombieCount--;

            // 랜덤 아이템 드롭
            Random rand = new Random();
            if (rand.nextInt(100) < 30) {  // 30% 확률로 블록 드롭
                dropBlockItems(event.getEntity().level, event.getEntity().blockPosition());
            } else {  // 70% 확률로 몹 생성
                spawnMobs(event.getEntity().level, event.getEntity().blockPosition());
            }
        }
    }

    private static void dropBlockItems(Level world, BlockPos pos) {
        Random random = new Random();
        int dropType = random.nextInt(3);
        switch (dropType) {
            case 0:
                world.addFreshEntity(new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(Items.DIAMOND_BLOCK)));
                break;
            case 1:
                world.addFreshEntity(new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(Items.GOLD_BLOCK)));
                break;
            case 2:
                world.addFreshEntity(new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(Items.IRON_BLOCK)));
                break;
        }
    }

    private static void spawnMobs(Level world, BlockPos pos) {
        Random random = new Random();
        int mobCount = random.nextInt(3) + 1;  // 1~3명의 몹 생성
        for (int i = 0; i < mobCount; i++) {
            Zombie zombie = new Zombie(EntityType.ZOMBIE, world);
            zombie.setPos(pos.getX() + random.nextInt(5) - 2, pos.getY(), pos.getZ() + random.nextInt(5) - 2);
            world.addFreshEntity(zombie);
        }
    }

    @SubscribeEvent
    public static void onAppleUse(PlayerEvent event) {
        if (event.getPlayer().getUseItem().getItem() == ModItems.UNKNOWN_APPLE) {
            startRaid((ServerPlayer) event.getPlayer());
            event.setCanceled(true); // 아이템 사용 이벤트를 취소하여 추가적인 행동을 방지
        }
    }
}
