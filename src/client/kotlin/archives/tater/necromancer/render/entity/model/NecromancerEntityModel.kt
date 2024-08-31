package archives.tater.necromancer.render.entity.model

import archives.tater.necromancer.Necromancer
import archives.tater.necromancer.entity.NecromancerEntity
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry
import net.minecraft.client.model.ModelPart
import net.minecraft.client.render.entity.model.EntityModelLayer
import net.minecraft.client.render.entity.model.SkeletonEntityModel
import net.minecraft.util.math.MathHelper.PI
import net.minecraft.util.math.MathHelper.cos

class NecromancerEntityModel(modelPart: ModelPart) : SkeletonEntityModel<NecromancerEntity>(modelPart) {

    override fun setAngles(mobEntity: NecromancerEntity, limbAngle: Float, limbDistance: Float, animationProgress: Float, headYaw: Float, headPitch: Float) {
        super.setAngles(mobEntity, limbAngle, limbDistance, animationProgress, headYaw, headPitch)
        if (mobEntity.isCasting) {
            rightArm.yaw = - PI / 2
            rightArm.roll = 0f
            rightArm.pitch = cos(animationProgress * 0.5f) * 0.5f + PI / 2 + 0.5f
            leftArm.yaw = PI / 2
            leftArm.roll = 0f
            leftArm.pitch = cos(animationProgress * 0.5f) * 0.5f + PI / 2 + 0.5f
        }
    }

    companion object {
        val LAYER = EntityModelLayer(Necromancer.id("necromancer"), "main")

        fun register() {
            EntityModelLayerRegistry.registerModelLayer(LAYER, ::getTexturedModelData)
        }
    }
}
