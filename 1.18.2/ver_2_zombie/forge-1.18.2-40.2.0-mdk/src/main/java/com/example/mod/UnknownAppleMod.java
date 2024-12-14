package com.example.mod;

import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Mod("unknownapplemod")
public class UnknownAppleMod {

    // 아이템 등록
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, "unknownapplemod");

    // 알 수 없는 사과 아이템 정의
    public static final RegistryObject<Item> UNKNOWN_APPLE = ITEMS.register("unknown_apple",
            () -> new Item(new Item.Properties().food((new FoodProperties.Builder())
                    .nutrition(4)
                    .saturationMod(0.3f)
                    .effect(() -> new MobEffectInstance(MobEffects.LUCK, 200, 1), 0.2f)
                    .build())));

    // 랜덤 생성기
    private static final Random random = new Random();

    @SubscribeEvent
    public void onZombieDeath(LivingDeathEvent event) {
        // 좀비가 죽었는지 확인
        if (event.getEntity() instanceof Zombie &&
                event.getSource().getEntity() != null) {

            // 10% 확률로 아이템 드랍
            if (random.nextDouble() <= 0.99) {
                Zombie zombie = (Zombie) event.getEntity();
                zombie.spawnAtLocation(UNKNOWN_APPLE.get());
            }
        }
    }

    @Mod.EventBusSubscriber(modid = "unknownapplemod", bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ModEvents {
        @SubscribeEvent
        public static void onItemConsumed(LivingEntityUseItemEvent.Finish event) {
            // 알 수 없는 사과를 먹었을 때
            if (event.getItem().getItem() == UNKNOWN_APPLE.get()) {
                // 20% 확률로 다이아몬드 획득
                if (random.nextDouble() <= 0.2) {
                    ItemStack diamondStack = new ItemStack(Items.DIAMOND);
                    event.getEntity().spawnAtLocation(diamondStack);
                }
            }
        }
    }
    @Mod.EventBusSubscriber(modid = "unknownapplemod", bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class RaidEvent {
        @SubscribeEvent
        public static void onUnknownAppleConsumed(LivingEntityUseItemEvent.Finish event) {
            // 플레이어가 사과를 먹었을 때
            if (event.getItem().getItem() == UNKNOWN_APPLE.get() &&
                    event.getEntity() instanceof Player) {

                // 40% 확률로 레이드 트리거
                if (random.nextDouble() <= 0.4) {
                    Player player = (Player) event.getEntity();
                    startRaid(player);
                }
            }
        }

        private static void startRaid(Player player) {
            Level world = player.level;
            BlockPos playerPos = player.blockPosition();

            // 4단계 레이드 시작
            for (int stage = 1; stage <= 4; stage++) {
                spawnMobsForStage(world, playerPos, stage);
            }
        }

        private static void spawnMobsForStage(Level world, BlockPos playerPos, int stage) {
            List<EntityType<?>> stageEnemies = getMobsForStage(stage);

            // 각 단계별 몬스터 수 증가
            int mobCount = stage * 2;

            for (int i = 0; i < mobCount; i++) {
                // 랜덤 몬스터 선택
                EntityType<?> enemyType = stageEnemies.get(random.nextInt(stageEnemies.size()));

                // 플레이어 주변 랜덤 위치에 몬스터 소환
                double offsetX = (random.nextDouble() - 0.5) * 10;
                double offsetZ = (random.nextDouble() - 0.5) * 10;

                BlockPos spawnPos = playerPos.offset(offsetX, 0, offsetZ);

                // 몬스터 생성 및 target 설정
                if (enemyType == EntityType.ZOMBIE) {
                    Zombie zombie = new Zombie(world);
                    zombie.setPos(spawnPos.getX(), spawnPos.getY(), spawnPos.getZ());
                    zombie.setTarget(world.getNearestPlayer(zombie, 50));
                    world.addFreshEntity(zombie);
                } else if (enemyType == EntityType.SKELETON) {
                    Skeleton skeleton = new Skeleton(EntityType.SKELETON,world);
                    skeleton.setPos(spawnPos.getX(), spawnPos.getY(), spawnPos.getZ());
                    skeleton.setTarget(world.getNearestPlayer(skeleton, 50));
                    world.addFreshEntity(skeleton);
                }
            }
        }

        private static List<EntityType<?>> getMobsForStage(int stage) {
            List<EntityType<?>> enemies = new ArrayList<>();

            // 스테이지에 따라 다른 몬스터 구성
            switch(stage) {
                case 1:
                    enemies.add(EntityType.ZOMBIE);
                    break;
                case 2:
                    enemies.add(EntityType.ZOMBIE);
                    enemies.add(EntityType.SKELETON);
                    break;
                case 3:
                    enemies.add(EntityType.ZOMBIE);
                    enemies.add(EntityType.SKELETON);
                    break;
                case 4:
                    enemies.add(EntityType.ZOMBIE);
                    enemies.add(EntityType.SKELETON);
                    break;
            }

            return enemies;
        }
    }
}
