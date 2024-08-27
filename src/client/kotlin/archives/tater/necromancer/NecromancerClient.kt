package archives.tater.necromancer

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry
import net.minecraft.client.render.entity.SkeletonEntityRenderer

object NecromancerClient : ClientModInitializer {
	override fun onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
		EntityRendererRegistry.register(Necromancer.NECROMANCER_ENTITY, ::SkeletonEntityRenderer)
	}
}
