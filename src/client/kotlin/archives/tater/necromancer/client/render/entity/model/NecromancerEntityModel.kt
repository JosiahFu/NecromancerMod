package archives.tater.necromancer.client.render.entity.model

import archives.tater.necromancer.NecromancerMod
import archives.tater.necromancer.client.lib.ModelPartBuilder
import archives.tater.necromancer.client.lib.cuboid
import archives.tater.necromancer.entity.NecromancerEntity
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry
import net.minecraft.client.model.Dilation
import net.minecraft.client.model.ModelPart
import net.minecraft.client.model.ModelTransform
import net.minecraft.client.model.TexturedModelData
import net.minecraft.client.render.entity.model.*
import net.minecraft.util.math.MathHelper.PI
import net.minecraft.util.math.MathHelper.cos

@Environment(EnvType.CLIENT)
class NecromancerEntityModel(modelPart: ModelPart) : SkeletonEntityModel<NecromancerEntity>(modelPart) {
    override fun setAngles(mobEntity: NecromancerEntity, limbAngle: Float, limbDistance: Float, animationProgress: Float, headYaw: Float, headPitch: Float) {
        super.setAngles(mobEntity, limbAngle, limbDistance, animationProgress, headYaw, headPitch)
        if (mobEntity.isCasting) {
            rightArm.yaw = PI / 2
            rightArm.pitch = 0f
            rightArm.roll = cos(animationProgress * 0.4f) * 0.5f + PI / 2 + 0.5f
            leftArm.yaw = -PI / 2
            leftArm.pitch = 0f
            leftArm.roll = -(cos(animationProgress * 0.4f) * 0.5f + PI / 2 + 0.5f)
        }
    }

    @Environment(EnvType.CLIENT)
    companion object {
        val LAYER = EntityModelLayer(NecromancerMod.id("necromancer"), "main")
        val INNER_ARMOR_LAYER = EntityModelLayer(NecromancerMod.id("necromancer"), "inner_armor")
        val OUTER_ARMOR_LAYER = EntityModelLayer(NecromancerMod.id("necromancer"), "outer_armor")

        val texturedModelData: TexturedModelData
            get() = BipedEntityModel.getModelData(Dilation.NONE, 0f).apply {
                root.apply {
                    addChild(
                        EntityModelPartNames.BODY,
                        ModelPartBuilder {
                            cuboid(
                                uvX = 16,
                                uvY = 16,

                                offsetX = -4.0f,
                                offsetY = 0.0f,
                                offsetZ = -2.0f,

                                sizeX = 8.0f,
                                sizeY = 12.0f,
                                sizeZ = 4.0f,
                            )

                            cuboid(
                                uvX = 16,
                                uvY = 32,

                                offsetX = -4.0F,
                                offsetY = 0.0F,
                                offsetZ = -2.0F,

                                sizeX = 8.0F,
                                sizeY = 24.0F,
                                sizeZ = 4.0F,
                                extra = 0.55f
                            )
                        },
                        ModelTransform.pivot(0.0F, 0.0F, 0.0F)
                    )
                    addChild(
                        EntityModelPartNames.RIGHT_ARM,
                        ModelPartBuilder {
                            cuboid(
                                uvX = 40,
                                uvY = 16,

                                offsetX = -1.0f,
                                offsetY = -1.0f,
                                offsetZ = -1.0f,

                                sizeX = 2.0f,
                                sizeY = 12.0f,
                                sizeZ = 2.0f,
                            )

                            cuboid(
                                uvX = 40,
                                uvY = 32,

                                offsetX = -1.0F,
                                offsetY = -1.0F,
                                offsetZ = -1.0F,

                                sizeX = 2.0F,
                                sizeY = 12.0F,
                                sizeZ = 2.0F,
                                extra = 0.5f
                            )
                        },
                        ModelTransform.pivot(-5.0f, 1.0f, 0.0f)
                    )
                    addChild(
                        EntityModelPartNames.LEFT_ARM,
                        ModelPartBuilder {
                            cuboid(
                                uvX = 40,
                                uvY = 16,
                                mirrored = true,

                                offsetX = -1.0f,
                                offsetY = -1.0f,
                                offsetZ = -1.0f,

                                sizeX = 2.0f,
                                sizeY = 12.0f,
                                sizeZ = 2.0f,
                            )

                            cuboid(
                                uvX = 0,
                                uvY = 32,
                                mirrored = true,

                                offsetX = -1.0F,
                                offsetY = -1.0F,
                                offsetZ = -1.0F,

                                sizeX = 2.0F,
                                sizeY = 12.0F,
                                sizeZ = 2.0F,
                                extra = 0.5f,
                            )
                        },
                        ModelTransform.pivot(5.0f, 1.0f, 0.0f)
                    )
                    addChild(
                        EntityModelPartNames.RIGHT_LEG,
                        ModelPartBuilder {
                            cuboid(
                                uvX = 0,
                                uvY = 16,

                                offsetX = -1.0f,
                                offsetY = 0.0f,
                                offsetZ = -1.0f,

                                sizeX = 2.0f,
                                sizeY = 12.0f,
                                sizeZ = 2.0f,
                            )
                        },
                        ModelTransform.pivot(-2.0f, 12.0f, 0.0f)
                    )
                    addChild(
                        EntityModelPartNames.LEFT_LEG,
                        ModelPartBuilder {
                            uv(0, 16)
                            mirrored()
                            cuboid(
                                uvX = 0,
                                uvY = 16,
                                mirrored = true,

                                offsetX = -1.0f,
                                offsetY = 0.0f,
                                offsetZ = -1.0f,

                                sizeX = 2.0f,
                                sizeY = 12.0f,
                                sizeZ = 2.0f
                            )
                        },
                        ModelTransform.pivot(2.0f, 12.0f, 0.0f)
                    )
                }
            }.let { TexturedModelData.of(it, 64, 64) }

        fun register() {
            EntityModelLayerRegistry.registerModelLayer(LAYER, ::texturedModelData)
            EntityModelLayerRegistry.registerModelLayer(INNER_ARMOR_LAYER) { TexturedModelData.of(ArmorEntityModel.getModelData(Dilation(0.5F)), 64, 32) }
            EntityModelLayerRegistry.registerModelLayer(OUTER_ARMOR_LAYER) { TexturedModelData.of(ArmorEntityModel.getModelData(Dilation(1.0F)), 64, 32) }
        }
    }
}
