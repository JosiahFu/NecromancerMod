package archives.tater.necromancer.mixin.client;

import archives.tater.necromancer.client.render.entity.features.NecromancedEyesFeatureRenderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.BipedEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.ZombieVillagerEntityRenderer;
import net.minecraft.client.render.entity.model.ZombieVillagerEntityModel;
import net.minecraft.entity.mob.ZombieVillagerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(ZombieVillagerEntityRenderer.class)
public abstract class ZombieVillagerEntityRendererMixin extends BipedEntityRenderer<ZombieVillagerEntity, ZombieVillagerEntityModel<ZombieVillagerEntity>>  {
	public ZombieVillagerEntityRendererMixin(EntityRendererFactory.Context ctx, ZombieVillagerEntityModel<ZombieVillagerEntity> model, float shadowRadius) {
		super(ctx, model, shadowRadius);
	}

	@Inject(at = @At("TAIL"), method = "<init>")
	private void addEyes(CallbackInfo info) {
		addFeature(new NecromancedEyesFeatureRenderer<>(this, NecromancedEyesFeatureRenderer.ZOMBIE_VILLAGER_EYES));
	}
}
