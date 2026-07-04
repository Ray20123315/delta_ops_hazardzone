package com.deltaops.block;

import com.deltaops.DeltaOpsMod;
import com.deltaops.container.TacticalContainerBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, DeltaOpsMod.MOD_ID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, DeltaOpsMod.MOD_ID);

    public static final RegistryObject<Block> LOOT_CRATE = BLOCKS.register("loot_crate",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.BARREL).strength(2.0f)));
    public static final RegistryObject<Block> EXTRACTION_POINT = BLOCKS.register("extraction_point",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.BEACON).strength(-1.0f, 3600000.0f).noOcclusion().lightLevel(s -> 15)));
    public static final RegistryObject<TacticalContainerBlock> TACTICAL_CONTAINER = BLOCKS.register("tactical_container",
            TacticalContainerBlock::new);

    public static final RegistryObject<BlockEntityType<com.deltaops.container.TacticalContainerBlockEntity>> TACTICAL_CONTAINER_BLOCK_ENTITY =
            BLOCK_ENTITY_TYPES.register("tactical_container_block_entity", () -> BlockEntityType.Builder.of(
                    com.deltaops.container.TacticalContainerBlockEntity::new,
                    TACTICAL_CONTAINER.get()
            ).build(null));
}