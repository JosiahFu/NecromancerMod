package archives.tater.necromancer.entity

import archives.tater.necromancer.NecromancerMod
import archives.tater.necromancer.lib.Registrar
import archives.tater.necromancer.lib.register
import net.minecraft.entity.EntityDimensions
import net.minecraft.entity.EntityType
import net.minecraft.entity.SpawnGroup
import net.minecraft.registry.Registries

object NecromancerModEntities : Registrar<EntityType<*>>(NecromancerMod.MOD_ID, Registries.ENTITY_TYPE) {
    val NECROMANCER: EntityType<NecromancerEntity> = register("necromancer", ::NecromancerEntity, SpawnGroup.MONSTER) {
        dimensions(EntityDimensions.fixed(0.65f, 2.2f))
    }
}
