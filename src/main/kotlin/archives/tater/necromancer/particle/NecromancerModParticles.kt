package archives.tater.necromancer.particle

import archives.tater.necromancer.NecromancerMod
import archives.tater.necromancer.lib.Registrar
import archives.tater.necromancer.lib.register
import net.minecraft.particle.DefaultParticleType
import net.minecraft.particle.ParticleType
import net.minecraft.registry.Registries

object NecromancerModParticles : Registrar<ParticleType<*>>(NecromancerMod.MOD_ID, Registries.PARTICLE_TYPE) {
    val NECROMANCER_PARTICLE: DefaultParticleType = register("necromancer")
    val NECROMANCER_SUMMON_PARTICLE: DefaultParticleType = register("necromancer_summon")
    val NECROMANCER_SUMMON_PARTICLE_EMITTER: DefaultParticleType = register("necromancer_summon_emitter")
    val NECROMANCER_TELEPORT_PARTICLE_EMITTER: DefaultParticleType = register("necromancer_teleport_emitter")
}
