package archives.tater.necromancer.mixin.client;

import archives.tater.necromancer.client.render.entity.features.NecromancedEyesFeatureRenderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.BipedEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.PiglinEntityRenderer;
import net.minecraft.client.render.entity.model.PiglinEntityModel;
import net.minecraft.entity.mob.MobEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(PiglinEntityRenderer.class)
public abstract class PiglinEntityRendererMixin extends BipedEntityRenderer<MobEntity, PiglinEntityModel<MobEntity>> {
    public PiglinEntityRendererMixin(EntityRendererFactory.Context ctx, PiglinEntityModel<MobEntity> model, float shadowRadius) {
        super(ctx, model, shadowRadius);
    }

    @Inject(at = @At("TAIL"), method = "<init>")
    private void addEyes(CallbackInfo info) {
        addFeature(new NecromancedEyesFeatureRenderer<>(this, NecromancedEyesFeatureRenderer.ZOMBIE_PIGLIN_EYES));
    }
}
