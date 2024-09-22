package archives.tater.necromancer.entity.ai.goal

import archives.tater.necromancer.*
import archives.tater.necromancer.cca.NecromancedComponent.Companion.necromancedOwner
import archives.tater.necromancer.cca.NecromancedComponent.Companion.startEmerge
import archives.tater.necromancer.entity.NecromancerEntity
import archives.tater.necromancer.tag.NecromancerTags
import net.minecraft.entity.EntityType
import net.minecraft.entity.SpawnReason
import net.minecraft.entity.ai.goal.Goal
import net.minecraft.entity.mob.MobEntity
import net.minecraft.fluid.Fluids
import net.minecraft.registry.tag.StructureTags
import net.minecraft.registry.tag.TagKey
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundEvents
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.MathHelper
import net.minecraft.world.gen.structure.Structure
import kotlin.math.pow

class NecromancerSummonGoal(private val owner: NecromancerEntity) : Goal() {

    override fun canStart(): Boolean = owner.target?.isAlive ?: false && !owner.isCasting && owner.spellCooldown == 0

    override fun shouldContinue(): Boolean = owner.target?.isAlive ?: false && owner.spellCooldown == 0

    override fun start() {
        owner.casting = NecromancerEntity.CAST_TIME
        owner.playSound(SoundEvents.ENTITY_EVOKER_PREPARE_SUMMON, 1.0f, 1.0f)
        owner.lookAtEntity(owner.target, 90f, 90f)
    }

    override fun tick() {
        if (owner.isCasting) return
        summonMobs()
        owner.spellCooldown = NecromancerEntity.COOLDOWN_TIME
    }

    private fun summonMobs() {
        val serverWorld = owner.world as ServerWorld

        val summons = mutableMapOf<BlockPos, EntityType<out MobEntity>>()
        var cost = 0

        for (i in 0..<16) {
            attempts@ for (j in 0..<16) { // 16 attempts
                val x = owner.blockPos.x + owner.random.nextBetween(-2, 2)
                val z = owner.blockPos.z + owner.random.nextBetween(-2, 2)
                val y = owner.blockPos.y

                val distance = (owner.target!!.x - x).pow(2) + (owner.target!!.z - z).pow(2)

                val blockPos = BlockPos(x, y, z)
                val biome = owner.world.getBiome(blockPos)
                val skyAccess = owner.world.isSkyVisible(blockPos)

                fun structureWithin(structure: TagKey<Structure>, distance: Int) =
                    serverWorld.locateStructure(structure, blockPos, 1, false)?.let {
                        it.horizontalSquaredDistance(blockPos) <= distance * distance
                    } ?: false

                val type: EntityType<out MobEntity> = when {
                    distance > 24.0.pow(2) && skyAccess -> EntityType.PHANTOM

                    distance > 8.0.pow(2) -> when {
                        biome in NecromancerTags.Biome.SPAWNS_STRAY && skyAccess -> EntityType.STRAY
                        else -> EntityType.SKELETON
                    }

                    structureWithin(NecromancerTags.Structure.SPAWNS_WITHER_SKELETON, 32) -> EntityType.WITHER_SKELETON
                    biome in NecromancerTags.Biome.SPAWNS_HUSK && skyAccess -> EntityType.HUSK
                    biome in NecromancerTags.Biome.SPAWNS_DROWNED || owner.world.getFluidState(blockPos).isOf(
                        Fluids.WATER
                    ) -> EntityType.DROWNED
                    biome in NecromancerTags.Biome.SPAWNS_ZOMBIE_PIGLIN -> EntityType.ZOMBIFIED_PIGLIN
                    structureWithin(StructureTags.VILLAGE, 32) -> EntityType.ZOMBIE_VILLAGER
                    else -> EntityType.ZOMBIE
                }

                if (cost + SUMMON_COSTS[type]!! > MAX_SUMMON_COST) break@attempts

                for (offsetY in 2 downTo -2) {
                    val pos = BlockPos(x, y + offsetY, z)
                    val below = pos.down()
                    if ((type == EntityType.DROWNED && owner.world.getFluidState(pos).isOf(
                            Fluids.WATER
                        ))
                        || owner.world.getBlockState(pos)
                            .getCollisionShape(owner.world, pos).isEmpty && (owner.world.getBlockState(below)
                            .isSideSolidFullSquare(owner.world, below, Direction.UP)
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
            val yaw = MathHelper.atan2(posDiff.z, posDiff.x).toFloat() * MathHelper.DEGREES_PER_RADIAN - 90

            serverWorld.spawnEntityAndPassengers(type.create(owner.world)!!.apply {
                initialize(serverWorld, serverWorld.getLocalDifficulty(blockPos), SpawnReason.MOB_SUMMONED, null, null)
                refreshPositionAndAngles(pos, yaw, 0f)
                headYaw = yaw
                this.necromancedOwner = owner
                owner.summons.add(this.uuid)
                startEmerge()
                (world as ServerWorld).spawnParticles(Necromancer.NECROMANCER_SUMMON_PARTICLE_EMITTER, x, y, z, 1)
            })
        }

        owner.playSound(SoundEvents.ENTITY_EVOKER_CAST_SPELL, 1.0f, 1.0f)
    }

    companion object {
        const val MAX_SUMMON_COST = 60
        val SUMMON_COSTS = mapOf(
            EntityType.PHANTOM to 40,
            EntityType.ZOMBIE to 10,
            EntityType.ZOMBIE_VILLAGER to 12,
            EntityType.HUSK to 12,
            EntityType.DROWNED to 15,
            EntityType.ZOMBIFIED_PIGLIN to 15,
            EntityType.SKELETON to 25,
            EntityType.STRAY to 30,
            EntityType.WITHER_SKELETON to 40,
        )

    }
}
