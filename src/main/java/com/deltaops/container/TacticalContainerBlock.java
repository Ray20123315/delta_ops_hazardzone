/*
 * Copyright (c) 2026 ray20123315. All Rights Reserved.
 * This file is part of "Delta Ops: Hazard Zone".
 * Proprietary and confidential.
 */
package com.deltaops.container;

import com.deltaops.DeltaOpsMod;
import com.deltaops.item.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;

public class TacticalContainerBlock extends BaseEntityBlock {
    public static final EnumProperty<ContainerVariant> VARIANT = EnumProperty.create("variant", ContainerVariant.class);

    public TacticalContainerBlock() {
        super(BlockBehaviour.Properties.of().strength(2.0F).sound(SoundType.WOOD).noOcclusion());
        this.registerDefaultState(this.stateDefinition.any().setValue(VARIANT, ContainerVariant.LARGE_SAFE));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<net.minecraft.world.level.block.Block, BlockState> builder) {
        builder.add(VARIANT);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(VARIANT, ContainerVariant.LARGE_SAFE);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TacticalContainerBlockEntity(pos, state);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        if (player.getItemInHand(hand).is(ModItems.BRAIN_COMPUTER_INTERFACE.get()) && state.getValue(VARIANT).supportsHiddenLayer()) {
            if (level.getBlockEntity(pos) instanceof TacticalContainerBlockEntity container) {
                container.setHiddenLayerUnlocked(true);
                container.setChanged();
                level.playSound((Player) null, pos, SoundEvents.UI_BUTTON_CLICK.get(), SoundSource.BLOCKS, 0.8F, 1.3F);
                player.displayClientMessage(Component.literal("§a已解鎖隱藏夾層。"), true);
            }
            return InteractionResult.CONSUME;
        }

        if (level.getBlockEntity(pos) instanceof TacticalContainerBlockEntity container) {
            MenuProvider provider = new MenuProvider() {
                @Override
                public Component getDisplayName() {
                    return Component.literal(container.getVariant().getDisplayName()).withStyle(ChatFormatting.GOLD);
                }

                @Override
                public AbstractContainerMenu createMenu(int id, Inventory inventory, Player playerEntity) {
                    return new TacticalContainerMenu(
                            id,
                            inventory,
                            container.getVariant(),
                            container.getMainInventory(),
                            container.getHiddenInventory(),
                            container.isHiddenLayerUnlocked()
                    );
                }
            };

            NetworkHooks.openScreen((ServerPlayer) player, provider);
            return InteractionResult.CONSUME;
        }

        return InteractionResult.PASS;
    }
}
