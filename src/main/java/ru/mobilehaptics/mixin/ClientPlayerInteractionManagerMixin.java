package ru.mobilehaptics.mixin;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.item.BlockItem;
import net.minecraft.util.Hand;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.mobilehaptics.MobileHapticsClient;

@Mixin(ClientPlayerInteractionManager.class)
public abstract class ClientPlayerInteractionManagerMixin {
    @Inject(method = "breakBlock", at = @At("RETURN"))
    private void mobilehaptics$afterBreak(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValueZ()) {
            MobileHapticsClient.vibrateBreak();
        }
    }

    @Inject(method = "interactBlock", at = @At("RETURN"))
    private void mobilehaptics$afterPlace(ClientPlayerEntity player, Hand hand, BlockHitResult hit,
                                           CallbackInfoReturnable<ActionResult> cir) {
        if (!cir.getReturnValue().isAccepted()) return;
        if (player.getStackInHand(hand).getItem() instanceof BlockItem) {
            MobileHapticsClient.vibratePlace();
        }
    }
}