package archives.tater.necromancer.client.render.entity

import archives.tater.necromancer.Necromancer
import archives.tater.necromancer.client.render.entity.model.NecromancerEntityModel
import archives.tater.necromancer.entity.NecromancerEntity
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.entity.BipedEntityRenderer
import net.minecraft.client.render.entity.EntityRendererFactory.Context
import net.minecraft.client.render.entity.feature.EyesFeatureRenderer
import net.minecraft.client.render.entity.model.EntityModelLayer
import net.minecraft.util.Identifier

@Environment(EnvType.CLIENT)
class NecromancerEntityRenderer(
    ctx: Context,
    layer: EntityModelLayer,
) : BipedEntityRenderer<NecromancerEntity, NecromancerEntityModel>(ctx, NecromancerEntityModel(ctx.getPart(layer)), 0.5f) {
    init {
        addFeature(object : EyesFeatureRenderer<NecromancerEntity, NecromancerEntityModel>(this) {
            override fun getEyesTexture() = EYES_TEXTURE
        })
    }

    constructor(ctx: Context) : this(ctx, NecromancerEntityModel.LAYER)

    override fun getTexture(entity: NecromancerEntity?): Identifier = TEXTURE

    @Environment(EnvType.CLIENT)
    companion object {
        val TEXTURE = Necromancer.id("textures/entity/necromancer.png")
        val EYES_TEXTURE: RenderLayer = RenderLayer.getEyes(Necromancer.id("textures/entity/necromancer_eyes.png"))
    }
}
