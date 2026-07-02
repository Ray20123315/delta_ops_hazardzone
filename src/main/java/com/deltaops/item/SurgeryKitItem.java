package com.deltaops.item;

import com.deltaops.DeltaOpsMod;
import com.deltaops.health.BodyPartHealth;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * 手術包 - 需長按右鍵讀條 8 秒，唯一能修復黑傷部位的道具
 * 使用原版書本材質
 */
public class SurgeryKitItem extends Item {
    public SurgeryKitItem() {
        super(new Item.Properties().stacksTo(1));
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide) return InteractionResultHolder.success(stack);

        // 開始讀條（8 秒 = 160 ticks）
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public int getUseDuration(@NotNull ItemStack stack) {
        return 160; // 8 秒
    }

    @Override
    public @NotNull ItemStack finishUsingItem(@NotNull ItemStack stack, @NotNull Level level, @NotNull LivingEntity entity) {
        if (!level.isClientSide && entity instanceof Player player) {
            BodyPartHealth health = BodyPartHealth.get(player);
            boolean anyRepaired = false;

            // 自動修復第一個黑傷部位
            for (BodyPartHealth.Part part : BodyPartHealth.Part.values()) {
                if (health.isBlacked(part)) {
                    health.surgery(part);
                    anyRepaired = true;
                    player.sendSystemMessage(
                            Component.translatable("message." + DeltaOpsMod.MOD_ID + ".surgery_done",
                                    Component.translatable("bodypart." + DeltaOpsMod.MOD_ID + "." + part.id))
                    );
                    break;
                }
            }

            if (!anyRepaired) {
                player.sendSystemMessage(
                        Component.translatable("message." + DeltaOpsMod.MOD_ID + ".surgery_no_blacked")
                );
                return stack;
            }

            stack.shrink(1);
        }
        return super.finishUsingItem(stack, level, entity);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.translatable("tooltip." + DeltaOpsMod.MOD_ID + ".surgery_kit"));
    }
}