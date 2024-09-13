package archives.tater.necromancer

import net.minecraft.entity.Entity
import net.minecraft.entity.data.DataTracker
import net.minecraft.entity.data.TrackedData
import net.minecraft.entity.data.TrackedDataHandler
import net.minecraft.registry.entry.RegistryEntry
import net.minecraft.registry.tag.TagKey
import net.minecraft.util.math.BlockPos
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
