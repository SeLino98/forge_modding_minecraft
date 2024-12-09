package com.example.item;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemUtils;
import net.minecraftforge.event.entity.item.ItemEvent;

public class ModItems {
    public static final Item UNKNOWN_APPLE = new Item(new Item.Properties()
            .tab(CreativeModeTab.TAB_MISC)
            .stacksTo(1)
            .food(new FoodProperties.Builder()
                    .nutrition(4)
                    .saturationMod(0.3f)
                    .build()));


    // Register item in the mod setup (e.g., DeferredRegister)
}
