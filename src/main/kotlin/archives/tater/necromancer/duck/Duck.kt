package archives.tater.necromancer.duck

import net.minecraft.entity.damage.DamageSource

interface DeathAvoider {
    fun shouldAvoidDeath(source: DamageSource): Boolean
}
