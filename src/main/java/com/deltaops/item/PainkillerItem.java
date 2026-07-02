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
 * 止痛藥 - 使用後 60 秒內免疫部位黑傷帶來的緩速與疲勞
 * 使用原版糖的材質
 */
public class PainkillerItem extends Item {
    private static final int DURATION_TICKS = 60 * 20; // 60 秒

    public PainkillerItem() {
        super(new Item.Properties().stacksTo(3));
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide) return InteractionResultHolder.success(stack);

        BodyPartHealth health = BodyPartHealth.get(player);
        health.applyPainkiller(DURATION_TICKS);

        player.sendSystemMessage(
                Component.translatable("message." + DeltaOpsMod.MOD_ID + ".painkiller_applied")
        );

        stack.shrink(1);
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.translatable("tooltip." + DeltaOpsMod.MOD_ID + ".painkiller"));
    }
}