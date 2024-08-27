package archives.tater.necromancer

import net.minecraft.entity.Entity
import net.minecraft.entity.data.TrackedData
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

operator fun <T> TrackedData<T>.provideDelegate(thisRef: T, property: KProperty<*>) = object : ReadWriteProperty<Entity, T> {
    override fun getValue(thisRef: Entity, property: KProperty<*>): T = thisRef.dataTracker.get(this@provideDelegate)

    override fun setValue(thisRef: Entity, property: KProperty<*>, value: T) {
        thisRef.dataTracker.set(this@provideDelegate, value)
    }
}

/*
operator fun <T> TrackedData<T>.getValue(thisRef: Entity, property: KProperty<*>): T = thisRef.dataTracker.get(this)
operator fun <T> TrackedData<T>.setValue(thisRef: Entity, property: KProperty<*>, value: T) {
    thisRef.dataTracker.set(this, value)
}
*/
