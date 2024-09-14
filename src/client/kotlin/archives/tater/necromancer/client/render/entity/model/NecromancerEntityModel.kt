package archives.tater.necromancer.client.render.entity.model

import archives.tater.necromancer.Necromancer
import archives.tater.necromancer.client.ModelPartBuilder
import archives.tater.necromancer.entity.NecromancerEntity
import com.google.common.collect.Iterables
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry
import net.minecraft.client.model.Dilation
import net.minecraft.client.model.ModelPart
import net.minecraft.client.model.ModelTransform
import net.minecraft.client.model.TexturedModelData
import net.minecraft.client.render.entity.model.BipedEntityModel
import net.minecraft.client.render.entity.model.EntityModelLayer
import net.minecraft.client.render.entity.model.EntityModelPartNames
import net.minecraft.client.render.entity.model.SkeletonEntityModel
import net.minecraft.util.math.MathHelper.PI
import net.minecraft.util.math.MathHelper.cos

@Environment(EnvType.CLIENT)
class NecromancerEntityModel(modelPart: ModelPart) : SkeletonEntityModel<NecromancerEntity>(modelPart) {
    val jacket = modelPart.getChild(EntityModelPartNames.JACKET)

    override fun getBodyParts(): MutableIterable<ModelPart> {
        return Iterables.concat(super.getBodyParts(), listOf(jacket))
    }

    override fun setVisible(visible: Boolean) {
        super.setVisible(visible)
        jacket.visible = visible
    }

    override fun setAngles(mobEntity: NecromancerEntity, limbAngle: Float, limbDistance: Float, animationProgress: Float, headYaw: Float, headPitch: Float) {
        super.setAngles(mobEntity, limbAngle, limbDistance, animationProgress, headYaw, headPitch)
        jacket.copyTransform(body)
        if (mobEntity.isCasting) {
            rightArm.yaw = - PI / 2
            rightArm.roll = 0f
            rightArm.pitch = cos(animationProgress * 0.5f) * 0.5f + PI / 2 + 0.5f
            leftArm.yaw = PI / 2
            leftArm.roll = 0f
            leftArm.pitch = cos(animationProgress * 0.5f) * 0.5f + PI / 2 + 0.5f
        }
    }

    @Environment(EnvType.CLIENT)
    companion object {
        val LAYER = EntityModelLayer(Necromancer.id("necromancer"), "main")

        val texturedModelData: TexturedModelData
            get() = BipedEntityModel.getModelData(Dilation.NONE, 0f).apply {
                root.apply {
                    addChild(
                        EntityModelPartNames.JACKET,
                        ModelPartBuilder {
                            uv(16, 32)
                            cuboid(-4.0F, 0.0F, -2.0F, 8.0F, 24.0F, 4.0F, Dilation(0.5F))
                        },
                        ModelTransform.pivot(0.0F, -12.0F, 0.0F)
                    )
                    addChild(
                        EntityModelPartNames.RIGHT_ARM,
                        ModelPartBuilder {
                            uv(40, 16)
                            cuboid(-1.0f, -2.0f, -1.0f, 2.0f, 12.0f, 2.0f)

                            uv(40, 32)
                            cuboid(-1.0F, -2.0F, -1.0F, 2.0F, 12.0F, 2.0F, Dilation(0.5F))
                        },
                        ModelTransform.pivot(-5.0f, 2.0f, 0.0f)
                    )
                    addChild(
                        EntityModelPartNames.LEFT_ARM,
                        ModelPartBuilder {
                            uv(40, 16)
                            mirrored()
                            cuboid(-1.0f, -2.0f, -1.0f, 2.0f, 12.0f, 2.0f)

                            uv(0, 32)
                            mirrored()
                            cuboid(-1.0F, -2.0F, -1.0F, 2.0F, 12.0F, 2.0F, Dilation(0.5F))
                        },
                        ModelTransform.pivot(5.0f, 2.0f, 0.0f)
                    )
                    addChild(
                        EntityModelPartNames.RIGHT_LEG,
                        ModelPartBuilder {
                            uv(0, 16)
                            cuboid(-1.0f, 0.0f, -1.0f, 2.0f, 12.0f, 2.0f)
                        },
                        ModelTransform.pivot(-2.0f, 12.0f, 0.0f)
                    )
                    addChild(
                        EntityModelPartNames.LEFT_LEG,
                        ModelPartBuilder {
                            uv(0, 16)
                            mirrored()
                            cuboid(-1.0f, 0.0f, -1.0f, 2.0f, 12.0f, 2.0f)
                        },
                        ModelTransform.pivot(2.0f, 12.0f, 0.0f)
                    )
                }
            }.let { TexturedModelData.of(it, 64, 64) }

        fun register() {
            EntityModelLayerRegistry.registerModelLayer(LAYER, Companion::texturedModelData)
        }
    }
}
