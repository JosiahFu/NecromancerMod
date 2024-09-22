package archives.tater.necromancer.lib

import net.minecraft.entity.Entity
import net.minecraft.entity.data.DataTracker
import net.minecraft.entity.data.TrackedData
import net.minecraft.entity.data.TrackedDataHandler
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtElement
import net.minecraft.nbt.NbtHelper
import net.minecraft.nbt.NbtList
import net.minecraft.particle.ParticleEffect
import net.minecraft.registry.entry.RegistryEntry
import net.minecraft.registry.tag.TagKey
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.util.math.random.Random
import java.util.*
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

fun Random.nextBetween(min: Double, max: Double) = (max - min) * nextDouble() + min

fun BlockPos.horizontalSquaredDistance(other: BlockPos): Int {
    val xDiff = this.x - other.x
    val zDiff = this.z - other.z
    return xDiff * xDiff + zDiff * zDiff
}

fun Iterable<NbtElement>.toNbtList() = NbtList().apply { this@toNbtList.forEach(::add) }

fun NbtCompound.putUuidList(key: String, uuids: Iterable<UUID>) {
    put(key, uuids.map(NbtHelper::fromUuid).toNbtList())
}
fun NbtCompound.getUuidList(key: String): List<UUID> = getList(key, NbtElement.INT_ARRAY_TYPE.toInt()).map(NbtHelper::toUuid)
