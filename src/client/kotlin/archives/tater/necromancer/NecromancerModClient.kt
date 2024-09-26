package archives.tater.necromancer

import archives.tater.necromancer.client.particle.NecromancerEmitterParticle
import archives.tater.necromancer.client.particle.NecromancerParticle
import archives.tater.necromancer.client.particle.NecromancerSummonParticle
import archives.tater.necromancer.client.render.entity.NecromancerEntityRenderer
import archives.tater.necromancer.client.render.entity.model.NecromancerEntityModel
import archives.tater.necromancer.entity.NecromancerModEntities
import archives.tater.necromancer.particle.NecromancerModParticles
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry


object NecromancerModClient : ClientModInitializer {
	override fun onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
		NecromancerEntityModel.register()
		EntityRendererRegistry.register(NecromancerModEntities.NECROMANCER, ::NecromancerEntityRenderer)

		with (ParticleFactoryRegistry.getInstance()) {
			with (NecromancerModParticles) {
				register(NECROMANCER_SUMMON_PARTICLE, NecromancerSummonParticle::Factory)
				register(NECROMANCER_PARTICLE, NecromancerParticle::Factory)
				register(NECROMANCER_SUMMON_PARTICLE_EMITTER, NecromancerEmitterParticle.SummonFactory())
				register(NECROMANCER_TELEPORT_PARTICLE_EMITTER, NecromancerEmitterParticle.TeleportFactory())
			}
		}
	}
}
