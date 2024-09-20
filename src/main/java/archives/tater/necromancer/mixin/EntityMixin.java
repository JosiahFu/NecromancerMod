package archives.tater.necromancer.mixin;

import archives.tater.necromancer.entity.NecromancerEntity;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.MobEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import static archives.tater.necromancer.cca.NecromancedComponent.hasNecromancedOwner;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @ModifyReturnValue(
            method = "isTeammate",
            at = @At("RETURN")
    )
    private boolean checkOwner(boolean original, @Local(index = 0, argsOnly = true) Entity other) {
        if (original) return true;
        Entity self = (Entity) (Object) this;
        return (
            self instanceof MobEntity selfMob && hasNecromancedOwner(selfMob) &&
            (other instanceof NecromancerEntity || other instanceof MobEntity otherMob && hasNecromancedOwner(otherMob))
        );
    }
}
