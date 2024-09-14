package archives.tater.necromancer.mixin;

import archives.tater.necromancer.cca.NecromancedComponent;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MobEntity.class)
public abstract class MobEntityMixin extends LivingEntity {
    protected MobEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @ModifyReturnValue(
            method = "canMoveVoluntarily",
            at = @At("RETURN")
    )
    private boolean checkEmerge(boolean original) {
        //noinspection ConstantValue
        return original && NecromancedComponent.getEmergeTicks((MobEntity) (LivingEntity) this) == 0;
    }

    @Inject(
            method = "baseTick",
            at = @At("TAIL")
    )
    private void decrementEmerge(CallbackInfo ci) {
        NecromancedComponent.tickEmerge((MobEntity) (LivingEntity) this);
    }
}
