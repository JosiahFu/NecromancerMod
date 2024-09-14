package archives.tater.necromancer.mixin;

import archives.tater.necromancer.cca.NecromancedComponent;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.TrackTargetGoal;
import net.minecraft.entity.mob.MobEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.UUID;

@Mixin(TrackTargetGoal.class)
public class TrackTargetGoalMixin {
    @Shadow @Final protected MobEntity mob;

    @ModifyReturnValue(
            method = "canTrack",
            at = @At(value = "RETURN", ordinal = 4)
    )
    private boolean checkOwner(boolean original, @Local(argsOnly = true) @Nullable LivingEntity target) {
        if (!original) return false;
        if (!(target instanceof MobEntity otherMob)) return true;
        @Nullable UUID otherOwner = NecromancedComponent.getOwnerUUID(otherMob);
        if (otherOwner == null) return true;
        @Nullable UUID selfOwner = NecromancedComponent.getOwnerUUID(mob);
        if (selfOwner == null) return true;
        return mob.getUuid() != otherOwner && otherMob.getUuid() != selfOwner && otherOwner != selfOwner;
    }
}
