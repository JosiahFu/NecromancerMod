package archives.tater.necromancer.entity

import net.minecraft.entity.EntityType
import net.minecraft.entity.SpawnReason
import net.minecraft.entity.ai.goal.FleeEntityGoal
import net.minecraft.entity.ai.goal.Goal
import net.minecraft.entity.mob.AbstractSkeletonEntity
import net.minecraft.entity.mob.MobEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.util.math.random.Random
import net.minecraft.world.LocalDifficulty
import net.minecraft.world.World

class NecromancerEntity(entityType: EntityType<out NecromancerEntity>, world: World) :
    AbstractSkeletonEntity(entityType, world) {


    override fun getStepSound(): SoundEvent = SoundEvents.ENTITY_SKELETON_STEP

    override fun initEquipment(random: Random?, localDifficulty: LocalDifficulty?) {}

    override fun initGoals() {
        super.initGoals()
        goalSelector.add(2, FleeEntityGoal(this, PlayerEntity::class.java, 8.0f, 0.6, 1.0))
        goalSelector.add(3, SummonGoal(EntityType.SKELETON, 240, 2))
        goalSelector.add(3, SummonGoal(EntityType.ZOMBIE, 120, 4))
    }

    override fun updateAttackType() {

    }

    companion object {
    }

    abstract inner class SummonGoal(
        val maxCooldown: Int,
        val count: Int,
    ) : Goal() {
        protected var cooldown: Int = 0

        override fun canStart(): Boolean = target?.isAlive ?: false

        override fun shouldContinue(): Boolean = target?.isAlive ?: false

        override fun tick() {
            if (cooldown == 0) {
                val serverWorld = world as ServerWorld
                repeat(count) {
                    with(spawnMob()) {
                        refreshPositionAndAngles(this@NecromancerEntity.blockPos.add(random.nextBetween(-2, 2), 0, random.nextBetween(-2, 2)), yaw, 0f)
                        initialize(serverWorld, serverWorld.getLocalDifficulty(blockPos), SpawnReason.MOB_SUMMONED, null, null)
                        serverWorld.spawnEntityAndPassengers(this)
                    }
                }
                cooldown = maxCooldown
            } else
                cooldown--
        }

        protected abstract fun spawnMob(): MobEntity
    }

    private fun SummonGoal(type: EntityType<out MobEntity>, maxCooldown: Int, count: Int) = object : SummonGoal(maxCooldown, count) {
        override fun spawnMob(): MobEntity = type.create(world)!!
    }
}
