package archives.tater.necromancer.client.render.entity.features

import archives.tater.necromancer.NecromancerMod
import archives.tater.necromancer.cca.NecromancedComponent.Companion.hasNecromancedOwner
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.entity.feature.EyesFeatureRenderer
import net.minecraft.client.render.entity.feature.FeatureRendererContext
import net.minecraft.client.render.entity.model.EntityModel
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.mob.MobEntity

@Environment(EnvType.CLIENT)
class NecromancedEyesFeatureRenderer<T: MobEntity, M: EntityModel<T>>(context: FeatureRendererContext<T, M>, private val eyesTexture: RenderLayer) :
    EyesFeatureRenderer<T, M>(context) {
    override fun getEyesTexture(): RenderLayer = eyesTexture

    override fun render(matrices: MatrixStack?, vertexConsumers: VertexConsumerProvider?, light: Int, entity: T, limbAngle: Float, limbDistance: Float, tickDelta: Float, animationProgress: Float, headYaw: Float, headPitch: Float) {
        if (!entity.hasNecromancedOwner) return
        super.render(matrices, vertexConsumers, light, entity, limbAngle, limbDistance, tickDelta, animationProgress, headYaw, headPitch)
    }

    @Environment(EnvType.CLIENT)
    companion object {
        @JvmField
        val ZOMBIE_EYES: RenderLayer = RenderLayer.getEyes(NecromancerMod.id("textures/entity/zombie_eyes.png"))

        @JvmField
        val SKELETON_EYES: RenderLayer = RenderLayer.getEyes(NecromancerMod.id("textures/entity/skeleton_eyes.png"))

        @JvmField
        val ZOMBIE_PIGLIN_EYES: RenderLayer = RenderLayer.getEyes(NecromancerMod.id("textures/entity/zombified_piglin_eyes.png"))

        @JvmField
        val ZOMBIE_VILLAGER_EYES: RenderLayer = RenderLayer.getEyes(NecromancerMod.id("textures/entity/zombie_villager_eyes.png"))
    }
}
