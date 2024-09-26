package archives.tater.necromancer.entity.ai.goal

import archives.tater.necromancer.cca.NecromancedComponent.Companion.necromancedOwner
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.ai.TargetPredicate
import net.minecraft.entity.ai.goal.TrackTargetGoal
import net.minecraft.entity.mob.HostileEntity
import net.minecraft.entity.mob.MobEntity

class AttackWithNecromancerGoal(mob: MobEntity) : TrackTargetGoal(mob, false, false) {
    private var attacking: LivingEntity? = null

    override fun canStart(): Boolean {
        return (mob.necromancedOwner as? HostileEntity)?.target?.let {
            if (it.isAlive && this.canTrack(it, TargetPredicate.DEFAULT)) {
                attacking = it
                true
            } else false
        } ?: false
    }

    override fun start() {
        mob.target = attacking
        super.start()
    }
}
