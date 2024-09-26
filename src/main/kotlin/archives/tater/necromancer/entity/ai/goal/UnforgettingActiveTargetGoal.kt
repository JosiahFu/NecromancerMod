package archives.tater.necromancer.entity.ai.goal

import net.minecraft.entity.LivingEntity
import net.minecraft.entity.ai.goal.ActiveTargetGoal
import net.minecraft.entity.mob.MobEntity
import net.minecraft.entity.player.PlayerEntity
import java.util.function.Predicate

class UnforgettingActiveTargetGoal<T: LivingEntity>(
    mob: MobEntity,
    targetClass: Class<T>,
    checkVisibility: Boolean,
    checkCanNavigate: Boolean = false,
    targetPredicate: Predicate<LivingEntity>? = null,
) : ActiveTargetGoal<T>(mob, targetClass, 10, checkVisibility, checkCanNavigate, targetPredicate) {
    override fun findClosestTarget() {
        val previousTarget = targetEntity
        super.findClosestTarget()
        if (targetEntity == null && (previousTarget != null && previousTarget.isAlive && !(previousTarget is PlayerEntity && (previousTarget.isCreative || previousTarget.isSpectator)))) targetEntity = previousTarget
    }
}
