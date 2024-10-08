package archives.tater.necromancer.client.render.entity

import archives.tater.necromancer.NecromancerMod
import archives.tater.necromancer.client.render.entity.model.NecromancerEntityModel
import archives.tater.necromancer.entity.NecromancerEntity
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.entity.BipedEntityRenderer
import net.minecraft.client.render.entity.EntityRendererFactory.Context
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer
import net.minecraft.client.render.entity.feature.EyesFeatureRenderer
import net.minecraft.client.render.entity.model.EntityModelLayer
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Identifier

@Environment(EnvType.CLIENT)
class NecromancerEntityRenderer(
    ctx: Context,
    layer: EntityModelLayer,
    legArmorLayer: EntityModelLayer,
    bodyArmorLayer: EntityModelLayer,
) : BipedEntityRenderer<NecromancerEntity, NecromancerEntityModel>(ctx, NecromancerEntityModel(ctx.getPart(layer)), 0.5f) {
    init {
        addFeature(ArmorFeatureRenderer(
            this,
            NecromancerEntityModel(ctx.getPart(legArmorLayer)),
            NecromancerEntityModel(ctx.getPart(bodyArmorLayer)),
            ctx.modelManager
        ))
        addFeature(object : EyesFeatureRenderer<NecromancerEntity, NecromancerEntityModel>(this) {
            override fun getEyesTexture() = EYES_TEXTURE
        })
    }

    constructor(ctx: Context) : this(ctx, NecromancerEntityModel.LAYER, NecromancerEntityModel.INNER_ARMOR_LAYER, NecromancerEntityModel.OUTER_ARMOR_LAYER)

    override fun getTexture(entity: NecromancerEntity?): Identifier = TEXTURE

    override fun scale(entity: NecromancerEntity, matrixStack: MatrixStack, amount: Float) {
        matrixStack.scale(1.1f, 1.1f, 1.1f)
    }

    @Environment(EnvType.CLIENT)
    companion object {
        val TEXTURE = NecromancerMod.id("textures/entity/necromancer.png")
        val EYES_TEXTURE: RenderLayer = RenderLayer.getEyes(NecromancerMod.id("textures/entity/necromancer_eyes.png"))
    }
}
