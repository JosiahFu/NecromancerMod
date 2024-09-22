package archives.tater.necromancer.lib

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents
import net.fabricmc.fabric.api.`object`.builder.v1.entity.FabricEntityTypeBuilder
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.entity.SpawnGroup
import net.minecraft.item.Item
import net.minecraft.item.ItemGroup
import net.minecraft.particle.DefaultParticleType
import net.minecraft.particle.ParticleType
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.util.Identifier

abstract class Registrar<T>(val namespace: String, private val registry: Registry<T>) {
    fun <U: T> register(id: Identifier, entry: U): U = Registry.register(registry, id, entry)
    fun <U: T> register(path: String, entry: U) = register(Identifier(namespace, path), entry)

    open fun register() {}
}

fun Registrar<ParticleType<*>>.register(path: String): DefaultParticleType = register(path, FabricParticleTypes.simple())

fun <U: Entity> Registrar<EntityType<*>>.register(
    path: String,
    constructor: EntityType.EntityFactory<U>,
    spawnGroup: SpawnGroup = SpawnGroup.MISC,
    init: FabricEntityTypeBuilder<U>.() -> Unit
): EntityType<U> =
    register(path, FabricEntityTypeBuilder.create(spawnGroup, constructor).apply(init).build())

fun Registrar<Item>.registerGroup(path: String, init: ItemGroup.Builder.() -> Unit): ItemGroup =
    Registry.register(Registries.ITEM_GROUP, Identifier(namespace, path), FabricItemGroup.builder().apply(init).build())

fun Registrar<Item>.modifyGroup(group: RegistryKey<ItemGroup>, block: FabricItemGroupEntries.() -> Unit) {
    ItemGroupEvents.modifyEntriesEvent(group).register(block)
}
