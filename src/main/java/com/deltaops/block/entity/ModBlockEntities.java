package com.deltaops.block.entity;

import com.deltaops.DeltaOpsMod;
import com.deltaops.block.ModBlocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, DeltaOpsMod.MOD_ID);

    public static final RegistryObject<BlockEntityType<LootCrateBlockEntity>> LOOT_CRATE =
            BLOCK_ENTITIES.register("loot_crate",
                    () -> BlockEntityType.Builder.of(LootCrateBlockEntity::new, ModBlocks.LOOT_CRATE.get()).build(null));
}