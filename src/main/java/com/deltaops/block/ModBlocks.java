package com.deltaops.block;

import com.deltaops.DeltaOpsMod;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, DeltaOpsMod.MOD_ID);

    public static final RegistryObject<Block> LOOT_CRATE = BLOCKS.register("loot_crate",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.BARREL).strength(2.0f)));
    public static final RegistryObject<Block> EXTRACTION_POINT = BLOCKS.register("extraction_point",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.BEACON).strength(-1.0f, 3600000.0f).noOcclusion().lightLevel(s -> 15)));
}