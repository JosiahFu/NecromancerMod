package archives.tater.necromancer.client.particle

import archives.tater.necromancer.toEach
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.particle.*
import net.minecraft.client.render.Camera
import net.minecraft.client.render.LightmapTextureManager
import net.minecraft.client.render.VertexConsumer
import net.minecraft.client.world.ClientWorld
import net.minecraft.particle.DefaultParticleType
import net.minecraft.util.math.MathHelper.*
import org.joml.Quaternionf
import org.joml.Vector3f

@Environment(EnvType.CLIENT)
class NecromancerParticle(
    clientWorld: ClientWorld,
    x: Double,
    y: Double,
    z: Double,
    velocityY: Double,
) :
    SpriteBillboardParticle(clientWorld, x, y, z, 0.0, 0.0, 0.0) {
        init {
            maxAge = MAX_AGE
            velocityX = 0.0
            this.velocityY = velocityY
            velocityZ = 0.0
        }

    override fun getType(): ParticleTextureSheet {
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT
    }

    override fun getSize(tickDelta: Float): Float = 3 / 16f / 2

    override fun getBrightness(tint: Float): Int = LightmapTextureManager.MAX_BLOCK_LIGHT_COORDINATE

    override fun buildGeometry(vertexConsumer: VertexConsumer, camera: Camera, tickDelta: Float) {
        val renderX = (lerp(tickDelta.toDouble(), this.prevPosX, this.x) - camera.pos.x).toFloat()
        val renderY = (lerp(tickDelta.toDouble(), this.prevPosY, this.y) - camera.pos.y).toFloat()
        val renderZ = (lerp(tickDelta.toDouble(), this.prevPosZ, this.z) - camera.pos.z).toFloat()

//        val rotation = if (this.angle == 0.0f)
//            camera.rotation
//        else
//            Quaternionf(camera.rotation).apply {
//                rotateZ(lerp(tickDelta, prevAngle, angle))
//            }


        val rotation = Quaternionf().rotationYXZ(PI - RADIANS_PER_DEGREE * camera.yaw, TAU / 4, 0f)

        val size = getSize(tickDelta)

        val vector3fs = arrayOf(
            Vector3f(-1.0f, 0.0f, -1.0f),
            Vector3f(-1.0f, 0.0f, 1.0f),
            Vector3f(1.0f, 0.0f, 1.0f),
            Vector3f(1.0f, 0.0f, -1.0f)
        ).toEach {
            rotate(rotation)
            mul(size)
            add(renderX, renderY, renderZ)
        }

        val light = getBrightness(tickDelta)

        vector3fs.forEachIndexed { index, vec ->
            with (vertexConsumer.vertex(vec.x.toDouble(), vec.y.toDouble(), vec.z.toDouble())) {
                when (index) {
                    0 -> texture(maxU, maxV)
                    1 -> texture(maxU, minV)
                    2 -> texture(minU, minV)
                    3 -> texture(minU, maxV)
                }
                val aAlpha = when (val dAge = age + tickDelta) {
                    in 0f..5f -> dAge / 5
                    in 5f..maxAge-5f -> 1f
                    else -> (maxAge - dAge).coerceAtLeast(0f) / 5
                }
                color(red, green, blue, alpha * aAlpha)
                light(light)
                next()
            }
        }
    }

    override fun tick() {
//        velocityY = MAX_VELOCITY * (1 - age / MAX_AGE.toFloat())
        alpha = 0.5f * (1 - age / MAX_AGE.toFloat())
        super.tick()
    }

    @Environment(EnvType.CLIENT)
    companion object {
        const val MAX_AGE = 15
    }

    @Environment(EnvType.CLIENT)
    class Factory(private val spriteProvider: SpriteProvider) : ParticleFactory<DefaultParticleType> {
        override fun createParticle(
            parameters: DefaultParticleType,
            world: ClientWorld,
            x: Double,
            y: Double,
            z: Double,
            velocityX: Double,
            velocityY: Double,
            velocityZ: Double
        ): Particle = NecromancerParticle(world, x, y, z, velocityY).apply {
            setSprite(spriteProvider)
        }
    }
}
