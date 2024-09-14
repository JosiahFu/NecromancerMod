package archives.tater.necromancer

import net.minecraft.entity.Entity
import net.minecraft.entity.data.DataTracker
import net.minecraft.entity.data.TrackedData
import net.minecraft.entity.data.TrackedDataHandler
import net.minecraft.particle.ParticleEffect
import net.minecraft.registry.entry.RegistryEntry
import net.minecraft.registry.tag.TagKey
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import kotlin.reflect.KProperty

operator fun <T> TrackedData<T>.getValue(thisRef: Entity, property: KProperty<*>): T = thisRef.dataTracker.get(this)
operator fun <T> TrackedData<T>.setValue(thisRef: Entity, property: KProperty<*>, value: T) {
    thisRef.dataTracker.set(this, value)
}

inline fun <reified E: Entity, T> trackedData(handler: TrackedDataHandler<T>): TrackedData<T> {
    return DataTracker.registerData(E::class.java, handler)
}

operator fun <T> TagKey<T>.contains(entry: RegistryEntry<T>) = entry.isIn(this)

fun BlockPos.copy() = BlockPos(x, y, z)

inline fun <T, C: Iterable<T>> C.toEach(action: T.() -> Unit): C = onEach(action)
inline fun <T> Array<out T>.toEach(action: T.() -> Unit): Array<out T> = onEach(action)

@Suppress("EXTENSION_SHADOWED_BY_MEMBER")
fun <T: ParticleEffect> ServerWorld.spawnParticles(particle: T, x: Double, y: Double, z: Double, count: Int, deltaX: Double = 0.0, deltaY: Double = 0.0, deltaZ: Double = 0.0, speed: Double = 0.0): Int =
    spawnParticles(particle, x, y, z, count, deltaX, deltaY, deltaZ, speed)

operator fun BlockPos.minus(other: Vec3d): Vec3d = toCenterPos() - other
operator fun Vec3d.minus(other: Vec3d): Vec3d = subtract(other)
