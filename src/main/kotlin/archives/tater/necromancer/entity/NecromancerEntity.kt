package archives.tater.necromancer.entity

import archives.tater.necromancer.*
import archives.tater.necromancer.cca.NecromancedComponent.Companion.hasNecromancedOwner
import archives.tater.necromancer.cca.NecromancedComponent.Companion.necromancedOwner
import archives.tater.necromancer.cca.NecromancedComponent.Companion.startEmerge
import archives.tater.necromancer.tag.NecromancerTags
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.entity.SpawnReason
import net.minecraft.entity.ai.goal.FleeEntityGoal
import net.minecraft.entity.ai.goal.Goal
import net.minecraft.entity.attribute.DefaultAttributeContainer
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.entity.damage.DamageSource
import net.minecraft.entity.data.TrackedData
import net.minecraft.entity.data.TrackedDataHandlerRegistry
import net.minecraft.entity.mob.AbstractSkeletonEntity
import net.minecraft.entity.mob.MobEntity
import net.minecraft.entity.passive.IronGolemEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.fluid.Fluids
import net.minecraft.nbt.NbtCompound
import net.minecraft.registry.tag.StructureTags
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.MathHelper.DEGREES_PER_RADIAN
import net.minecraft.util.math.MathHelper.atan2
import net.minecraft.util.math.random.Random
import net.minecraft.world.LocalDifficulty
import net.minecraft.world.World
import kotlin.math.pow

class NecromancerEntity(entityType: EntityType<out NecromancerEntity>, world: World) :
    AbstractSkeletonEntity(entityType, world) {

    var casting by CASTING
    var spellCooldown = 0

    val isCasting
        get() = isAlive && casting > 0

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

    override fun writeCustomDataToNbt(nbt: NbtCompound) {
        super.writeCustomDataToNbt(nbt)
        nbt.putInt(NBT_CASTING, casting)
        nbt.putInt(NBT_COOLDOWN, spellCooldown)
    }

    override fun readCustomDataFromNbt(nbt: NbtCompound) {
        super.readCustomDataFromNbt(nbt)
        casting = nbt.getInt(NBT_CASTING)
        spellCooldown = nbt.getInt(NBT_COOLDOWN)
    }

    override fun updateAttackType() {}

    override fun isTeammate(other: Entity): Boolean = super.isTeammate(other) || (other as? MobEntity)?.hasNecromancedOwner == true

    override fun tick() {
        super.tick()
        if (isCasting) {
            world.addParticle(
                Necromancer.NECROMANCER_PARTICLE,
                x + world.random.nextBetween(-0.7, 0.7),
                y + world.random.nextBetween(1.2, 1.6),
                z + world.random.nextBetween(-0.7, 0.7),
                0.0,
                0.2 * world.random.nextDouble() + 0.1,
                0.0
            )
        }
    }

    override fun mobTick() {
        super.mobTick()
        if (spellCooldown > 0) spellCooldown--
        if (casting > 0) casting--
    }

    override fun getAmbientSound(): SoundEvent = SoundEvents.ENTITY_SKELETON_AMBIENT
    override fun getHurtSound(source: DamageSource?): SoundEvent = SoundEvents.ENTITY_SKELETON_HURT
    override fun getDeathSound(): SoundEvent = SoundEvents.ENTITY_SKELETON_DEATH
    override fun getStepSound(): SoundEvent = SoundEvents.ENTITY_SKELETON_STEP

    companion object {
        val CASTING: TrackedData<Int> = trackedData<NecromancerEntity, _>(TrackedDataHandlerRegistry.INTEGER)

        const val NBT_CASTING = "Casting"
        const val NBT_COOLDOWN = "SpellCooldown"

        const val CAST_TIME = 40
        const val COOLDOWN_TIME = 300

        const val MAX_SUMMON_COST = 60
        val SUMMON_COSTS = mapOf(
            EntityType.PHANTOM to 40,
            EntityType.ZOMBIE to 10,
            EntityType.ZOMBIE_VILLAGER to 10,
            EntityType.HUSK to 12,
            EntityType.DROWNED to 15,
            EntityType.ZOMBIFIED_PIGLIN to 15,
            EntityType.SKELETON to 25,
            EntityType.STRAY to 30,
        )

        val necromancerAttributes: DefaultAttributeContainer.Builder
            get() = createAbstractSkeletonAttributes().apply {
                add(EntityAttributes.GENERIC_FOLLOW_RANGE, 40.0)
                add(EntityAttributes.GENERIC_MAX_HEALTH, 40.0)
                add(EntityAttributes.GENERIC_ARMOR, 10.0)
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
            summonMobs()
            owner.spellCooldown = COOLDOWN_TIME
        }

        private fun summonMobs() {
            val serverWorld = world as ServerWorld

            val summons = mutableMapOf<BlockPos, EntityType<out MobEntity>>()
            var cost = 0

            for (i in 0..<16) {
                attempts@ for (j in 0..<16) { // 16 attempts
                    val x = owner.blockPos.x + random.nextBetween(-2, 2)
                    val z = owner.blockPos.z + random.nextBetween(-2, 2)
                    val y = owner.blockPos.y

                    val distance = (owner.target!!.x - x).pow(2) + (owner.target!!.z - z).pow(2)

                    val blockPos = BlockPos(x, y, z)
                    val biome = world.getBiome(blockPos)

                    val type: EntityType<out MobEntity> = when {
                        distance < 8.0.pow(2) -> when {
                            biome in NecromancerTags.Biome.SPAWNS_HUSK -> EntityType.HUSK
                            biome in NecromancerTags.Biome.SPAWNS_DROWNED || world.getFluidState(blockPos).isOf(Fluids.WATER) -> EntityType.DROWNED
                            biome in NecromancerTags.Biome.SPAWNS_ZOMBIE_PIGLIN -> EntityType.ZOMBIFIED_PIGLIN
                            (world as ServerWorld).locateStructure(StructureTags.VILLAGE, blockPos, 1, false) != null -> EntityType.ZOMBIE_VILLAGER
                            else -> EntityType.ZOMBIE
                        }

                        distance < 24.0.pow(2) -> when {
                            (world as ServerWorld).locateStructure(NecromancerTags.Structure.SPAWNS_WITHER_SKELETON, blockPos, 1, false) != null -> EntityType.WITHER_SKELETON
                            biome in NecromancerTags.Biome.SPAWNS_STRAY -> EntityType.STRAY
                            else -> EntityType.SKELETON
                        }

                        else -> EntityType.PHANTOM
                    }

                    if (cost + SUMMON_COSTS[type]!! > MAX_SUMMON_COST) break@attempts

                    for (offsetY in 2 downTo -2) {
                        val pos = BlockPos(x, y + offsetY, z)
                        val below = pos.down()
                        if ((type == EntityType.DROWNED && world.getFluidState(pos).isOf(Fluids.WATER))
                            || world.getBlockState(pos)
                                .getCollisionShape(world, pos).isEmpty && (world.getBlockState(below)
                                .isSideSolidFullSquare(world, below, Direction.UP)
                            || type == EntityType.PHANTOM) && pos !in summons
                        ) {
                            summons[pos] = type
                            cost += SUMMON_COSTS[type]!!
                            break@attempts
                        }
                    }
                }

                if (cost > MAX_SUMMON_COST) break
            }

            for ((pos, type) in summons) {
                val posDiff = owner.target!!.pos - pos.toCenterPos()
                val yaw = atan2(posDiff.z, posDiff.x).toFloat() * DEGREES_PER_RADIAN - 90

                serverWorld.spawnEntityAndPassengers(type.create(world)!!.apply {
                    initialize(serverWorld, serverWorld.getLocalDifficulty(blockPos), SpawnReason.MOB_SUMMONED, null, null)
                    refreshPositionAndAngles(pos, yaw, 0f)
                    headYaw = yaw
                    this.necromancedOwner = owner
                    startEmerge()
                    (world as ServerWorld).spawnParticles(Necromancer.NECROMANCER_PARTICLE_EMITTER, x, y, z, 1)
                })
            }

            owner.playSound(SoundEvents.ENTITY_EVOKER_CAST_SPELL, 1.0f, 1.0f)
        }
    }
}
