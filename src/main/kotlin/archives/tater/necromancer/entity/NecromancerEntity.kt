package archives.tater.necromancer.entity

import archives.tater.necromancer.*
import archives.tater.necromancer.tag.NecromancerBiomeTags
import net.minecraft.entity.EntityType
import net.minecraft.entity.SpawnReason
import net.minecraft.entity.ai.goal.FleeEntityGoal
import net.minecraft.entity.ai.goal.Goal
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.entity.data.TrackedData
import net.minecraft.entity.data.TrackedDataHandlerRegistry
import net.minecraft.entity.mob.AbstractSkeletonEntity
import net.minecraft.entity.passive.IronGolemEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.random.Random
import net.minecraft.world.LocalDifficulty
import net.minecraft.world.World
import kotlin.math.pow

class NecromancerEntity(entityType: EntityType<out NecromancerEntity>, world: World) :
    AbstractSkeletonEntity(entityType, world) {

    var casting by CASTING
    var spellCooldown = 0

    val isCasting
        get() = casting > 0

    override fun getStepSound(): SoundEvent = SoundEvents.ENTITY_SKELETON_STEP

    override fun initEquipment(random: Random?, localDifficulty: LocalDifficulty?) {}

    override fun initDataTracker() {
        super.initDataTracker()
        dataTracker.startTracking(CASTING, 0)
    }

    override fun initGoals() {
        super.initGoals()
        goalSelector.add(2, FleeEntityGoal(this, PlayerEntity::class.java, 8.0f, 1.0, 1.4))
        goalSelector.add(2, FleeEntityGoal(this, IronGolemEntity::class.java, 8.0f, 1.0, 1.4))
        goalSelector.add(3, SummonGoal())
    }

    override fun updateAttackType() {}

    override fun mobTick() {
        super.mobTick()
        if (spellCooldown > 0) spellCooldown--
        if (casting > 0) casting--
    }

    companion object {
        val CASTING: TrackedData<Int> = trackedData<NecromancerEntity, _>(TrackedDataHandlerRegistry.INTEGER)

        const val CAST_TIME = 40
        const val COOLDOWN_TIME = 300

        val necromancerAttributes
            get() = createAbstractSkeletonAttributes().apply {
                add(EntityAttributes.GENERIC_FOLLOW_RANGE, 40.0)
            }
    }

    inner class SummonGoal : Goal() {
        private inline val owner get() = this@NecromancerEntity

        override fun canStart(): Boolean = owner.target?.isAlive ?: false && !owner.isCasting && spellCooldown == 0

        override fun shouldContinue(): Boolean = owner.target?.isAlive ?: false && spellCooldown == 0

        override fun start() {
            owner.casting = CAST_TIME
            owner.playSound(SoundEvents.ENTITY_EVOKER_PREPARE_SUMMON, 1.0f, 1.0f)
            owner.lookAtEntity(owner.target, 90f, 90f)
        }

        override fun tick() {
            if (owner.isCasting) return
            owner.lookAtEntity(owner.target, 180f, 90f)

            val serverWorld = world as ServerWorld
            val distance = owner.squaredDistanceTo(target)
            val (type, count) = when {
                distance < 8.0.pow(2) -> when (world.getBiome(blockPos)) {
                    in NecromancerBiomeTags.SPAWNS_HUSK -> EntityType.HUSK to 4
                    in NecromancerBiomeTags.SPAWNS_DROWNED -> EntityType.DROWNED to 4
                    in NecromancerBiomeTags.SPAWNS_ZOMBIE_PIGLIN -> EntityType.ZOMBIFIED_PIGLIN to 5
                    else -> EntityType.ZOMBIE to 6
                }
                distance < 16.0.pow(2) -> when (world.getBiome(blockPos)) {
                    in NecromancerBiomeTags.SPAWNS_STRAY -> EntityType.STRAY
                    else -> EntityType.SKELETON
                } to 2
                else -> EntityType.PHANTOM to 1
            }

            val positions = mutableListOf<BlockPos>()

            repeat(count) {
                attempts@ for(i in 0..<16) { // 16 attempts
                    val x = owner.blockPos.x + random.nextBetween(-2, 2)
                    val z = owner.blockPos.z + random.nextBetween(-2, 2)
                    val y = owner.blockPos.y
                    for (offsetY in 2 downTo -2) {
                        val pos = BlockPos(x, y + offsetY, z)
                        val below = pos.down()
                        if (world.getBlockState(pos).getCollisionShape(world, pos).isEmpty && (world.getBlockState(below).isSideSolidFullSquare(world, below, Direction.UP) || type == EntityType.PHANTOM) && pos !in positions) {
                            positions.add(pos.copy())
                            break@attempts
                        }
                    }
                }
            }

            for (pos in positions) {
                serverWorld.spawnEntityAndPassengers(type.create(world)!!.apply {
                    refreshPositionAndAngles(pos, owner.yaw, 0f)
                    setHeadYaw(owner.headYaw)
                    initialize(serverWorld, serverWorld.getLocalDifficulty(blockPos), SpawnReason.MOB_SUMMONED, null, null)
                    (world as ServerWorld).spawnParticles(Necromancer.NECROMANCER_PARTICLE_EMITTER, x, y, z, 1, 0.0, 0.0, 0.0, 0.0)
                })
            }

            owner.playSound(SoundEvents.ENTITY_EVOKER_CAST_SPELL, 1.0f, 1.0f)
            owner.spellCooldown = COOLDOWN_TIME
        }
    }
}
