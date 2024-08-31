package archives.tater.necromancer

import archives.tater.necromancer.render.entity.NecromancerEntityRenderer
import archives.tater.necromancer.render.entity.model.NecromancerEntityModel
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry

object NecromancerClient : ClientModInitializer {
	override fun onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
		NecromancerEntityModel.register()
		EntityRendererRegistry.register(Necromancer.NECROMANCER_ENTITY, ::NecromancerEntityRenderer)
	}
}
