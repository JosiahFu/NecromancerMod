package archives.tater.necromancer.tag

import archives.tater.necromancer.Necromancer
import net.minecraft.registry.RegistryKeys
import net.minecraft.world.gen.structure.Structure as MCStructure


object NecromancerTags {
    object Biome : NSTagCollection<net.minecraft.world.biome.Biome>(Necromancer.MOD_ID, RegistryKeys.BIOME) {
        val SPAWNS_HUSK = tag("spawns_husk")
        val SPAWNS_STRAY = tag("spawns_stray")
        val SPAWNS_DROWNED = tag("spawns_drowned")
        val SPAWNS_ZOMBIE_PIGLIN = tag("spawns_zombified_piglin")
    }

    object Structure : NSTagCollection<MCStructure>(Necromancer.MOD_ID, RegistryKeys.STRUCTURE) {
        val SPAWNS_WITHER_SKELETON = tag("spawns_wither_skeleton")
    }
}
