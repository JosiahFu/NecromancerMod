package archives.tater.necromancer

import archives.tater.necromancer.client.particle.NecromancerEmitterParticle
import archives.tater.necromancer.client.particle.NecromancerParticle
import archives.tater.necromancer.client.particle.NecromancerSummonParticle
import archives.tater.necromancer.client.render.entity.NecromancerEntityRenderer
import archives.tater.necromancer.client.render.entity.model.NecromancerEntityModel
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry


object NecromancerClient : ClientModInitializer {
	override fun onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
		NecromancerEntityModel.register()
		EntityRendererRegistry.register(Necromancer.NECROMANCER_ENTITY, ::NecromancerEntityRenderer)

		ParticleFactoryRegistry.getInstance().register(Necromancer.NECROMANCER_SUMMON_PARTICLE, NecromancerSummonParticle::Factory)
		ParticleFactoryRegistry.getInstance().register(Necromancer.NECROMANCER_PARTICLE, NecromancerParticle::Factory)
		ParticleFactoryRegistry.getInstance().register(Necromancer.NECROMANCER_SUMMON_PARTICLE_EMITTER, NecromancerEmitterParticle.SummonFactory())
		ParticleFactoryRegistry.getInstance().register(Necromancer.NECROMANCER_TELEPORT_PARTICLE_EMITTER, NecromancerEmitterParticle.TeleportFactory())
	}
}
