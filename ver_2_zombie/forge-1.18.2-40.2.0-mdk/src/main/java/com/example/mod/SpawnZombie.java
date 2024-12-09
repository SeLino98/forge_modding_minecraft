package com.example.mod;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
public class SpawnZombie {

    public void spawnZombie(Level world, double x, double y, double z) {
        Zombie zombie = new Zombie(EntityType.ZOMBIE, world);
        zombie.setPos(x, y, z);
        world.addFreshEntity(zombie);
    }

}
