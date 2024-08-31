package archives.tater.necromancer.render.entity

import archives.tater.necromancer.Necromancer
import archives.tater.necromancer.entity.NecromancerEntity
import archives.tater.necromancer.render.entity.model.NecromancerEntityModel
import net.minecraft.client.render.entity.BipedEntityRenderer
import net.minecraft.client.render.entity.EntityRendererFactory.Context
import net.minecraft.client.render.entity.model.EntityModelLayer
import net.minecraft.util.Identifier

class NecromancerEntityRenderer(
    ctx: Context,
    layer: EntityModelLayer,
) : BipedEntityRenderer<NecromancerEntity, NecromancerEntityModel>(ctx, NecromancerEntityModel(ctx.getPart(layer)), 0.5f) {

    constructor(ctx: Context) : this(ctx, NecromancerEntityModel.LAYER)

    override fun getTexture(entity: NecromancerEntity?): Identifier = TEXTURE

    companion object {
        val TEXTURE = Necromancer.id("textures/entity/necromancer.png")
    }
}
