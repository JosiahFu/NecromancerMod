package archives.tater.necromancer.client.particle

import archives.tater.necromancer.Necromancer
import archives.tater.necromancer.entity.NecromancerEntity
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
) : NoRenderParticle(clientWorld, x, y, z, 0.0, 0.0, 0.0) {
    init {
        velocityX = 0.0
        velocityY = 0.0
        velocityZ = 0.0
        maxAge = NecromancerEntity.CAST_TIME
    }

    override fun tick() {
        if (age == 0) {
            world.addParticle(Necromancer.NECROMANCER_SUMMON_PARTICLE, x, y, z, 0.0, 0.0, 0.0)
        }

//        for (i in 0..<1) {
            val theta = random.nextFloat() * TAU
            val r = random.nextFloat() * RADIUS
            world.addParticle(Necromancer.NECROMANCER_PARTICLE, x + r * cos(theta), y, z + r * sin(theta), 0.0, 0.2 * random.nextDouble() + 0.1, 0.0)
//        }

        super.tick()
    }

    @Environment(EnvType.CLIENT)
    companion object {
        const val RADIUS = 0.8f
    }

    @Environment(EnvType.CLIENT)
    class Factory: ParticleFactory<DefaultParticleType> {
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
