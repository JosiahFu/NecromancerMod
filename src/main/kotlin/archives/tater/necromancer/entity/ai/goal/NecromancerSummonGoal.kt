package archives.tater.necromancer.entity.ai.goal

import archives.tater.necromancer.NecromancerMod
import archives.tater.necromancer.cca.NecromancedComponent.Companion.necromancedOwner
import archives.tater.necromancer.cca.NecromancedComponent.Companion.startEmerge
import archives.tater.necromancer.entity.NecromancerEntity
import archives.tater.necromancer.lib.*
import archives.tater.necromancer.particle.NecromancerModParticles
import archives.tater.necromancer.tag.NecromancerModTags
import net.minecraft.entity.EntityType
import net.minecraft.entity.SpawnReason
import net.minecraft.entity.ai.goal.Goal
import net.minecraft.entity.attribute.EntityAttributeModifier
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.entity.mob.MobEntity
import net.minecraft.entity.mob.ZombieEntity
import net.minecraft.entity.mob.ZombifiedPiglinEntity
import net.minecraft.fluid.Fluids
import net.minecraft.registry.tag.FluidTags
import net.minecraft.registry.tag.StructureTags
import net.minecraft.registry.tag.TagKey
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundEvents
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.MathHelper
import net.minecraft.world.gen.structure.Structure
import java.util.*
import kotlin.math.pow

class NecromancerSummonGoal(private val owner: NecromancerEntity) : Goal() {

    override fun canStart(): Boolean = owner.target?.isAlive ?: false && !owner.isCasting && owner.spellCooldown == 0 && owner.castsLeft > 0

    override fun shouldContinue(): Boolean = owner.target?.isAlive ?: false && owner.spellCooldown == 0 && owner.castsLeft > 0

    override fun start() {
        owner.casting = NecromancerEntity.CAST_TIME
        owner.playSound(SoundEvents.ENTITY_EVOKER_PREPARE_SUMMON, 1.0f, 1.0f)
        owner.lookAtEntity(owner.target, 90f, 90f)
    }

    override fun tick() {
        if (!owner.isCasting) summonMobs()
    }

    override fun stop() {
        owner.casting = 0
    }

    private fun summonMobs() {
        val serverWorld = owner.world as ServerWorld

        val summons = mutableMapOf<BlockPos, EntityType<out MobEntity>>()
        var cost = 0

        fun structureWithin(structure: TagKey<Structure>, distance: Int) =
            NecromancerMod.logger.measure("locate ${structure.id}") {
                serverWorld.locateStructure(structure, owner.blockPos, 0, false)
            }?.let {
                it.horizontalSquaredDistance(owner.blockPos) <= distance.squared()
            } ?: false

        val nearVillage by lazy { structureWithin(StructureTags.VILLAGE, 64) }
        val nearFortress by lazy { structureWithin(NecromancerModTags.Structure.SPAWNS_WITHER_SKELETON, 48) }


        for (i in 0..<16) {
            attempts@ for (j in 0..<16) { // 16 attempts
                val x = owner.blockPos.x + owner.random.nextBetween(-2, 2)
                val z = owner.blockPos.z + owner.random.nextBetween(-2, 2)
                val y = owner.blockPos.y

                val distance = (owner.target!!.x - x).pow(2) + (owner.target!!.z - z).pow(2)

                val blockPos = BlockPos(x, y, z)
                val biome = owner.world.getBiome(blockPos)
                val skyAccess = owner.world.isSkyVisible(blockPos)

                val type: EntityType<out MobEntity> = when {
                    distance > 24.0.pow(2) && skyAccess -> EntityType.PHANTOM

                    distance > 8.0.pow(2) -> when {
                        biome in NecromancerModTags.Biome.SPAWNS_STRAY && skyAccess -> EntityType.STRAY
                        else -> EntityType.SKELETON
                    }

                    nearFortress -> EntityType.WITHER_SKELETON
                    biome in NecromancerModTags.Biome.SPAWNS_HUSK && skyAccess -> EntityType.HUSK
                    biome in NecromancerModTags.Biome.SPAWNS_DROWNED || owner.world.getFluidState(blockPos) in FluidTags.WATER -> EntityType.DROWNED
                    biome in NecromancerModTags.Biome.SPAWNS_ZOMBIE_PIGLIN -> EntityType.ZOMBIFIED_PIGLIN
                    nearVillage -> EntityType.ZOMBIE_VILLAGER
                    else -> EntityType.ZOMBIE
                }

                if (cost + SUMMON_COSTS[type]!! > MAX_SUMMON_COST) break@attempts

                for (offsetY in 2 downTo -2) {
                    val pos = BlockPos(x, y + offsetY, z)
                    val below = pos.down()
                    if ((type == EntityType.DROWNED && owner.world.getFluidState(pos) isOf Fluids.WATER)
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

        if (summons.isEmpty()) {
            return
        }

        for ((pos, type) in summons) {
            val posDiff = owner.target!!.pos - pos.toCenterPos()
            val yaw = MathHelper.atan2(posDiff.z, posDiff.x).toFloat() * MathHelper.DEGREES_PER_RADIAN - 90

            serverWorld.spawnEntityAndPassengers(type.create(owner.world)!!.apply {
                initialize(serverWorld, serverWorld.getLocalDifficulty(blockPos), SpawnReason.MOB_SUMMONED, null, null)
                refreshPositionAndAngles(pos, yaw, 0f)
                headYaw = yaw
                this.necromancedOwner = owner
                if (this is ZombieEntity && this !is ZombifiedPiglinEntity && !this.isBaby)
                    attributes.getCustomInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)?.addPersistentModifier(ZOMBIE_SPEED_BOOST)
                owner.summons.trimNonAlive()
                owner.summons.add(this)
                startEmerge()
                (world as ServerWorld).spawnParticles(NecromancerModParticles.NECROMANCER_SUMMON_PARTICLE_EMITTER, x, y, z, 1)
            })
        }

        owner.playSound(SoundEvents.ENTITY_EVOKER_CAST_SPELL, 1.0f, 1.0f)
        owner.castsLeft--
        owner.spellCooldown = NecromancerEntity.COOLDOWN_TIME
    }

    companion object {
        const val MAX_SUMMON_COST = 60
        val SUMMON_COSTS = mapOf(
            EntityType.PHANTOM to 40,
            EntityType.ZOMBIE to 12,
            EntityType.ZOMBIE_VILLAGER to 15,
            EntityType.HUSK to 15,
            EntityType.DROWNED to 15,
            EntityType.ZOMBIFIED_PIGLIN to 17,
            EntityType.SKELETON to 25,
            EntityType.STRAY to 30,
            EntityType.WITHER_SKELETON to 40,
        )

        val ZOMBIE_SPEED_BOOST = EntityAttributeModifier(UUID.fromString("79603b64-e46a-4244-8f98-2582e035c03e"), "attribute.necromancermod.zombie_speed_boost", 0.1, EntityAttributeModifier.Operation.MULTIPLY_TOTAL)
    }
}
