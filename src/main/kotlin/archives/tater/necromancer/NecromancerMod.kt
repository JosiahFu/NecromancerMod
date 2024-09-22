package archives.tater.necromancer

import archives.tater.necromancer.entity.NecromancerEntity
import archives.tater.necromancer.entity.NecromancerModEntities
import archives.tater.necromancer.item.NecromancerModItems
import archives.tater.necromancer.particle.NecromancerModParticles
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.`object`.builder.v1.entity.FabricDefaultAttributeRegistry
import net.minecraft.util.Identifier
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object NecromancerMod : ModInitializer {
	const val MOD_ID = "necromancer"
	@JvmField
    val logger: Logger = LoggerFactory.getLogger(MOD_ID)

	fun id(path: String) = Identifier(MOD_ID, path)

	override fun onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		FabricDefaultAttributeRegistry.register(NecromancerModEntities.NECROMANCER, NecromancerEntity.necromancerAttributes)
		NecromancerModEntities.register()
		NecromancerModItems.register()
		NecromancerModParticles.register()
	}
}
