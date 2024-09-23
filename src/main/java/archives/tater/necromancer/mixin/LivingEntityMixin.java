package archives.tater.necromancer.mixin;

import archives.tater.necromancer.duck.DeathAvoider;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @Shadow public abstract void setHealth(float health);

    @ModifyExpressionValue(
            method = "damage",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;tryUseTotem(Lnet/minecraft/entity/damage/DamageSource;)Z")
    )
    boolean checkDeathAvoider(boolean original, @Local(argsOnly = true) DamageSource source) {
        if (original) return true;

        if (!(this instanceof DeathAvoider deathAvoider && deathAvoider.shouldAvoidDeath(source))) return false;

        setHealth(1.0f);

        return true;
    }
}
