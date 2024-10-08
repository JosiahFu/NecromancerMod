package archives.tater.necromancer.client.particle

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.particle.*
import net.minecraft.client.render.Camera
import net.minecraft.client.render.LightmapTextureManager
import net.minecraft.client.render.VertexConsumer
import net.minecraft.client.world.ClientWorld
import net.minecraft.particle.DefaultParticleType
import net.minecraft.util.math.MathHelper.lerp
import org.joml.Vector3f

@Environment(EnvType.CLIENT)
class NecromancerSummonParticle(
    clientWorld: ClientWorld,
    x: Double,
    y: Double,
    z: Double,
) : SpriteBillboardParticle(clientWorld, x, y, z, 0.0, 0.0, 0.0) {
    init {
        velocityX = 0.0
        velocityY = 0.0
        velocityZ = 0.0
        maxAge = 40
        alpha = 0.5f
    }

    override fun getType(): ParticleTextureSheet = ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT

    override fun getSize(tickDelta: Float) = 22 / 32f

    override fun buildGeometry(vertexConsumer: VertexConsumer, camera: Camera, tickDelta: Float) {
        val renderX = (lerp(tickDelta.toDouble(), this.prevPosX, this.x) - camera.pos.x).toFloat()
        val renderY = (lerp(tickDelta.toDouble(), this.prevPosY, this.y) - camera.pos.y).toFloat() + 0.05f
        val renderZ = (lerp(tickDelta.toDouble(), this.prevPosZ, this.z) - camera.pos.z).toFloat()

        val size = getSize(tickDelta)

        val vector3fs = arrayOf(
            Vector3f(-1.0f, 0.0f, -1.0f),
            Vector3f(-1.0f, 0.0f, 1.0f),
            Vector3f(1.0f, 0.0f, 1.0f),
            Vector3f(1.0f, 0.0f, -1.0f)
        ).onEach {
            it.mul(size)
            it.add(renderX, renderY, renderZ)
        }

//        val light = getBrightness(tickDelta)
        val light = LightmapTextureManager.MAX_BLOCK_LIGHT_COORDINATE

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

    @Environment(EnvType.CLIENT)
    class Factory(private val spriteProvider: SpriteProvider) : ParticleFactory<DefaultParticleType> {
        override fun createParticle(
            defaultParticleType: DefaultParticleType,
            clientWorld: ClientWorld,
            x: Double,
            y: Double,
            z: Double,
            velocityX: Double,
            velocityY: Double,
            velocityZ: Double
        ): Particle {
            return NecromancerSummonParticle(clientWorld, x, y, z).apply {
                setSprite(spriteProvider)
            }
        }
    }
}
