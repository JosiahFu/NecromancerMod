package archives.tater.necromancer.client.particle

import archives.tater.necromancer.entity.NecromancerEntity
import archives.tater.necromancer.particle.NecromancerModParticles
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.particle.NoRenderParticle
import net.minecraft.client.particle.ParticleFactory
import net.minecraft.client.world.ClientWorld
import net.minecraft.particle.DefaultParticleType
import net.minecraft.util.math.MathHelper.*

@Environment(EnvType.CLIENT)
class NecromancerEmitterParticle(
    clientWorld: ClientWorld,
    x: Double,
    y: Double,
    z: Double,
    private val isSummon: Boolean = false,
) : NoRenderParticle(clientWorld, x, y, z, 0.0, 0.0, 0.0) {
    init {
        velocityX = 0.0
        velocityY = 0.0
        velocityZ = 0.0
        maxAge = if (isSummon) NecromancerEntity.CAST_TIME else 5
    }

    override fun tick() {
        if (isSummon && age == 0) {
            world.addParticle(NecromancerModParticles.NECROMANCER_SUMMON_PARTICLE, x, y, z, 0.0, 0.0, 0.0)
        }

        repeat(2) {
            val theta = random.nextFloat() * TAU
            val r = random.nextFloat() * RADIUS
            world.addParticle(NecromancerModParticles.NECROMANCER_PARTICLE, x + r * cos(theta), y, z + r * sin(theta), 0.0, 0.2 * random.nextDouble() + 0.1, 0.0)
        }

        super.tick()
    }

    @Environment(EnvType.CLIENT)
    companion object {
        const val RADIUS = 0.8f
    }

    @Environment(EnvType.CLIENT)
    class SummonFactory: ParticleFactory<DefaultParticleType> {
        override fun createParticle(
            parameters: DefaultParticleType?,
            world: ClientWorld,
            x: Double,
            y: Double,
            z: Double,
            velocityX: Double,
            velocityY: Double,
            velocityZ: Double
        ) = NecromancerEmitterParticle(world, x, y, z, true)
    }

    @Environment(EnvType.CLIENT)
    class TeleportFactory: ParticleFactory<DefaultParticleType> {
        override fun createParticle(
            parameters: DefaultParticleType?,
            world: ClientWorld,
            x: Double,
            y: Double,
            z: Double,
            velocityX: Double,
            velocityY: Double,
            velocityZ: Double
        ) = NecromancerEmitterParticle(world, x, y, z)
    }
}
