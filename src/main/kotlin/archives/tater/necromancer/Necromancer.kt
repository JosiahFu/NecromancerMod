package archives.tater.necromancer

import archives.tater.necromancer.entity.NecromancerEntity
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.fabricmc.fabric.api.`object`.builder.v1.entity.FabricDefaultAttributeRegistry
import net.fabricmc.fabric.api.`object`.builder.v1.entity.FabricEntityTypeBuilder
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes
import net.minecraft.entity.EntityDimensions
import net.minecraft.entity.EntityType
import net.minecraft.entity.SpawnGroup
import net.minecraft.item.SpawnEggItem
import net.minecraft.particle.DefaultParticleType
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.util.Identifier
import org.slf4j.LoggerFactory

object Necromancer : ModInitializer {
	const val MOD_ID = "necromancer"
	@JvmField
    val logger = LoggerFactory.getLogger(MOD_ID)

	fun id(path: String) = Identifier(MOD_ID, path)

	val NECROMANCER_ENTITY: EntityType<NecromancerEntity> = Registry.register(Registries.ENTITY_TYPE, id("necromancer"), FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, ::NecromancerEntity).apply {
		dimensions(EntityDimensions.fixed(0.6f, 1.99f))
	}.build())

	val NECROMANCER_SPAWN_EGG: SpawnEggItem = Registry.register(Registries.ITEM, id("necromancer_spawn_egg"), SpawnEggItem(
		NECROMANCER_ENTITY, 0xCCCCCC, 0x00FF00, FabricItemSettings()))

	val NECROMANCER_PARTICLE: DefaultParticleType = Registry.register(Registries.PARTICLE_TYPE, id("necromancer"), FabricParticleTypes.simple())
	val NECROMANCER_SUMMON_PARTICLE: DefaultParticleType = Registry.register(Registries.PARTICLE_TYPE, id("necromancer_summon"), FabricParticleTypes.simple())
	val NECROMANCER_PARTICLE_EMITTER: DefaultParticleType = Registry.register(Registries.PARTICLE_TYPE, id("necromancer_emitter"), FabricParticleTypes.simple())

	override fun onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		FabricDefaultAttributeRegistry.register(NECROMANCER_ENTITY, NecromancerEntity.necromancerAttributes)
	}
}
