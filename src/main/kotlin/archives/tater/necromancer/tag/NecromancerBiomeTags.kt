package archives.tater.necromancer.tag

import archives.tater.necromancer.Necromancer
import net.minecraft.registry.RegistryKeys
import net.minecraft.world.biome.Biome

object NecromancerBiomeTags : NSTagCollection<Biome>(Necromancer.MOD_ID, RegistryKeys.BIOME) {
    val SPAWNS_HUSK = tag("spawns_husk")
    val SPAWNS_STRAY = tag("spawns_stray")
    val SPAWNS_DROWNED = tag("spawns_drowned")
}

