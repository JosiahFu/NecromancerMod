package archives.tater.necromancer.entity

import archives.tater.necromancer.*
import archives.tater.necromancer.cca.NecromancedComponent.Companion.hasNecromancedOwner
import archives.tater.necromancer.entity.ai.goal.NecromancerSummonGoal
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.entity.ai.goal.*
import net.minecraft.entity.attribute.DefaultAttributeContainer
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.entity.damage.DamageSource
import net.minecraft.entity.data.TrackedData
import net.minecraft.entity.data.TrackedDataHandlerRegistry
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.entity.mob.AbstractSkeletonEntity
import net.minecraft.entity.mob.MobEntity
import net.minecraft.entity.mob.WitherSkeletonEntity
import net.minecraft.entity.mob.ZombieEntity
import net.minecraft.entity.passive.IronGolemEntity
import net.minecraft.entity.passive.WolfEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.nbt.NbtCompound
import net.minecraft.registry.tag.DamageTypeTags
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.util.math.random.Random
import net.minecraft.world.LocalDifficulty
import net.minecraft.world.World
import java.util.*

class NecromancerEntity(entityType: EntityType<out NecromancerEntity>, world: World) :
    AbstractSkeletonEntity(entityType, world) {

    var casting by CASTING
    var spellCooldown = 0
    val summons = mutableListOf<UUID>()

    val isCasting
        get() = isAlive && casting > 0

    override fun initEquipment(random: Random?, localDifficulty: LocalDifficulty?) {}

    override fun initDataTracker() {
        super.initDataTracker()
        dataTracker.startTracking(CASTING, 0)
    }

    override fun initGoals() {
        goalSelector.add(2, AvoidSunlightGoal(this))
        goalSelector.add(2, FleeEntityGoal(this, PlayerEntity::class.java, 8.0f, 1.4, 1.4))
        goalSelector.add(2, FleeEntityGoal(this, IronGolemEntity::class.java, 8.0f, 1.4, 1.4))
        goalSelector.add(2, FleeEntityGoal(this, WolfEntity::class.java, 8.0f, 1.4, 1.4))
        goalSelector.add(2, LookAtTargetGoal())
        goalSelector.add(3, NecromancerSummonGoal(this))
        goalSelector.add(3, EscapeSunlightGoal(this, 1.0))
        goalSelector.add(5, WanderAroundFarGoal(this, 1.0))
        goalSelector.add(6, LookAtEntityGoal(this, PlayerEntity::class.java, 8.0f))
        goalSelector.add(6, LookAroundGoal(this))
        targetSelector.add(1, ActiveTargetGoal(this, PlayerEntity::class.java, false))
        targetSelector.add(2, ActiveTargetGoal(this, IronGolemEntity::class.java, false))
        targetSelector.add(3, ActiveTargetGoal(this, WolfEntity::class.java, false))
        targetSelector.add(4, RevengeGoal(this))
    }

    override fun writeCustomDataToNbt(nbt: NbtCompound) {
        super.writeCustomDataToNbt(nbt)
        nbt.putInt(NBT_CASTING, casting)
        nbt.putInt(NBT_COOLDOWN, spellCooldown)
        nbt.putUuidList(NBT_SUMMONS, summons)
    }

    override fun readCustomDataFromNbt(nbt: NbtCompound) {
        super.readCustomDataFromNbt(nbt)
        casting = nbt.getInt(NBT_CASTING)
        spellCooldown = nbt.getInt(NBT_COOLDOWN)
        summons.clear()
        summons.addAll(nbt.getUuidList(NBT_SUMMONS))
    }

    override fun updateAttackType() {}

    override fun isTeammate(other: Entity): Boolean = super.isTeammate(other) || (other as? MobEntity)?.hasNecromancedOwner == true

    override fun damage(source: DamageSource, amount: Float): Boolean {
        if (world.isClient || isDead || isInvulnerableTo(source) || source.attacker == null || (source.isIn(
                DamageTypeTags.IS_FIRE
            ) && this.hasStatusEffect(StatusEffects.FIRE_RESISTANCE))) return super.damage(source, amount)

        val world = world as ServerWorld

        val swapTarget = summons.shuffled().mapNotNull { uuid -> world.getEntity(uuid)?.takeIf { it.isAlive && it.isOnGround } }.let { entities ->
            entities
                .filter { it is ZombieEntity || it is WitherSkeletonEntity }
                .ifEmpty { entities }
                .maxByOrNull { source.attacker!!.squaredDistanceTo(it) }
        } ?: return super.damage(source, amount)

        val superResult = super.damage(source, amount / 2)

        if (isDead) return superResult

        val selfPos = pos
        val selfYaw = yaw
        val selfPitch = pitch

        this.updatePositionAndAngles(swapTarget.x, swapTarget.y, swapTarget.z, swapTarget.yaw, swapTarget.pitch)
        swapTarget.updatePositionAndAngles(selfPos.x, selfPos.y, selfPos.z, selfYaw, selfPitch)

        this.playSound(SoundEvents.ENTITY_ENDERMAN_TELEPORT, 1f, 1f)
        swapTarget.playSound(SoundEvents.ENTITY_ENDERMAN_TELEPORT, 1f, 1f)

        this.navigation.stop()

        world.spawnParticles(Necromancer.NECROMANCER_TELEPORT_PARTICLE_EMITTER, x, y, z, 1)
        world.spawnParticles(Necromancer.NECROMANCER_TELEPORT_PARTICLE_EMITTER, swapTarget.x, swapTarget.y, swapTarget.z, 1)

        swapTarget.damage(source, amount / 2)

        return superResult
    }

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
        const val NBT_SUMMONS = "Summons"

        const val CAST_TIME = 40
        const val COOLDOWN_TIME = 300

        val necromancerAttributes: DefaultAttributeContainer.Builder
            get() = createAbstractSkeletonAttributes().apply {
                add(EntityAttributes.GENERIC_FOLLOW_RANGE, 40.0)
                add(EntityAttributes.GENERIC_MAX_HEALTH, 40.0)
                add(EntityAttributes.GENERIC_ARMOR, 10.0)
            }
    }

    inner class LookAtTargetGoal : Goal() {
        init {
            controls = EnumSet.of(Control.LOOK, Control.MOVE)
        }

        override fun canStart(): Boolean {
            return this@NecromancerEntity.isCasting
        }

        override fun start() {
            navigation.stop()
        }

        override fun tick() {
            if (target != null)
                lookControl.lookAt(target, maxHeadRotation.toFloat(), maxLookPitchChange.toFloat())
        }
    }
}
