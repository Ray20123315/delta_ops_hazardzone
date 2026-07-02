package com.deltaops.item;

import com.deltaops.DeltaOpsMod;
import com.deltaops.health.BodyPartHealth;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * 止血帶 - 解除流血狀態
 * 使用原版紙的材質
 */
public class TourniquetItem extends Item {
    public TourniquetItem() {
        super(new Item.Properties().stacksTo(5));
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide) return InteractionResultHolder.success(stack);

        BodyPartHealth health = BodyPartHealth.get(player);
        if (!health.isBleeding()) {
            player.sendSystemMessage(
                    Component.translatable("message." + DeltaOpsMod.MOD_ID + ".tourniquet_no_bleeding")
            );
            return InteractionResultHolder.fail(stack);
        }

        health.setBleeding(false);
        player.sendSystemMessage(
                Component.translatable("message." + DeltaOpsMod.MOD_ID + ".tourniquet_applied")
        );
        stack.shrink(1);
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.translatable("tooltip." + DeltaOpsMod.MOD_ID + ".tourniquet"));
    }
}