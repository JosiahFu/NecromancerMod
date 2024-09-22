package archives.tater.necromancer.item

import archives.tater.necromancer.NecromancerMod
import archives.tater.necromancer.entity.NecromancerModEntities
import archives.tater.necromancer.lib.Registrar
import archives.tater.necromancer.lib.modifyGroup
import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.minecraft.item.Item
import net.minecraft.item.ItemGroups
import net.minecraft.item.SpawnEggItem
import net.minecraft.registry.Registries

object NecromancerModItems : Registrar<Item>(NecromancerMod.MOD_ID, Registries.ITEM) {
    val NECROMANCER_SPAWN_EGG = register("necromancer_spawn_egg", SpawnEggItem(
        NecromancerModEntities.NECROMANCER, 0xCCCCCC, 0x00FF00, FabricItemSettings()
    ))

    override fun register() {
        modifyGroup(ItemGroups.SPAWN_EGGS) {
            add(NECROMANCER_SPAWN_EGG)
        }
    }
}
