package archives.tater.necromancer.cca

import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer
import net.minecraft.entity.mob.AbstractSkeletonEntity
import net.minecraft.entity.mob.PhantomEntity
import net.minecraft.entity.mob.ZombieEntity

object NecromancerModComponents : EntityComponentInitializer {
    override fun registerEntityComponentFactories(registry: EntityComponentFactoryRegistry) {
        for (entityClass in listOf(
            ZombieEntity::class,
            AbstractSkeletonEntity::class,
            PhantomEntity::class,
        )) {
            registry.registerFor(entityClass.java, NecromancedComponent.KEY, ::NecromancedComponent)
        }
    }
}
