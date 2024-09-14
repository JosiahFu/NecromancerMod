package archives.tater.necromancer.mixin.client;

import archives.tater.necromancer.client.render.entity.features.NecromancedEyesFeatureRenderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.BipedEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.ZombieBaseEntityRenderer;
import net.minecraft.client.render.entity.model.ZombieEntityModel;
import net.minecraft.entity.mob.ZombieEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(ZombieBaseEntityRenderer.class)
public abstract class ZombieBaseEntityRendererMixin <T extends ZombieEntity, M extends ZombieEntityModel<T>> extends BipedEntityRenderer<T, M> {
	public ZombieBaseEntityRendererMixin(EntityRendererFactory.Context ctx, M model, float shadowRadius) {
		super(ctx, model, shadowRadius);
	}

	@Inject(at = @At("TAIL"), method = "<init>")
	private void addEyes(CallbackInfo info) {
		addFeature(new NecromancedEyesFeatureRenderer<>(this, NecromancedEyesFeatureRenderer.ZOMBIE_EYES));
	}
}
