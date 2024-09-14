package archives.tater.necromancer.mixin.client;

import archives.tater.necromancer.cca.NecromancedComponent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(LivingEntityRenderer.class)
public class LivingEntityRendererMixin<T extends LivingEntity> {
    @Inject(
            method = "setupTransforms",
            at = @At("TAIL")
    )
    void sink(T entity, MatrixStack matrices, float animationProgress, float bodyYaw, float tickDelta, CallbackInfo ci) {
        if (!(entity instanceof MobEntity mobEntity)) return;
        int emerge = NecromancedComponent.getEmergeTicks(mobEntity);
        if (emerge == 0) return;
        matrices.translate(0f, entity.getHeight() * -((emerge - (tickDelta - 1.0f)) / NecromancedComponent.MAX_EMERGE_TICKS), 0f);
    }
}
