package com.mrcrayfish.guns.mixin.common;

import com.mrcrayfish.guns.Config;
import com.mrcrayfish.guns.entity.DamageSourceProjectile;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

/**
 * Author: MrCrayfish
 */
@Mixin(LivingEntity.class)
public class LivingEntityMixin
{
    private DamageSource source;

    @Inject(method = "hurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;knockback(FDD)V"))
    private void capture(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir)
    {
        this.source = source;
    }

    @ModifyArgs(method = "hurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;knockback(FDD)V"))
    private void modifyApplyKnockbackArgs(Args args)
    {
        if(this.source instanceof DamageSourceProjectile)
        {
            if(!Config.COMMON.gameplay.enableKnockback.get())
            {
                args.set(0, 0F);
                return;
            }

            double strength = Config.COMMON.gameplay.knockbackStrength.get();
            if(strength > 0)
            {
                args.set(0, (float) strength);
            }
        }
    }
}
