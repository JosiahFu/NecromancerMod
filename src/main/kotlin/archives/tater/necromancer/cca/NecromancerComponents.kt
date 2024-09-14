package archives.tater.necromancer.cca

import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer
import net.minecraft.entity.mob.MobEntity

object NecromancerComponents : EntityComponentInitializer {
    override fun registerEntityComponentFactories(registry: EntityComponentFactoryRegistry) {
        registry.registerFor(MobEntity::class.java, NecromancedComponent.KEY, ::NecromancedComponent)
    }
}
