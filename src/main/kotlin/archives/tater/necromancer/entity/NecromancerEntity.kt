package archives.tater.necromancer.entity

import archives.tater.necromancer.*
import archives.tater.necromancer.cca.NecromancedComponent.Companion.hasNecromancedOwner
import archives.tater.necromancer.entity.ai.goal.NecromancerSummonGoal
import archives.tater.necromancer.lib.*
import archives.tater.necromancer.particle.NecromancerModParticles
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityData
import net.minecraft.entity.EntityType
import net.minecraft.entity.SpawnReason
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
import net.minecraft.item.RangedWeaponItem
import net.minecraft.nbt.NbtCompound
import net.minecraft.registry.tag.DamageTypeTags
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.util.math.Vec3d
import net.minecraft.util.math.random.Random
import net.minecraft.world.LocalDifficulty
import net.minecraft.world.ServerWorldAccess
import net.minecraft.world.World
import java.util.*

class NecromancerEntity(entityType: EntityType<out NecromancerEntity>, world: World) :
    AbstractSkeletonEntity(entityType, world) {

    var casting by CASTING
    var spellCooldown = 0
    val summons = mutableListOf<UUID>()
    var castsLeft = MAX_CASTS
    var castRecharge = CAST_RECHARGE_TIME

    private var swapPos: Vec3d? = null
    private var swapYaw: Float? = null
    private var swapPitch: Float? = null
    private var swapDeath = false

    val isCasting
        get() = isAlive && casting > 0

    override fun initDataTracker() {
        super.initDataTracker()
        dataTracker.startTracking(CASTING, 0)
    }

    override fun initGoals() {
        goalSelector.add(2, AvoidSunlightGoal(this))
        goalSelector.add(2, FleeEntityGoal(this, PlayerEntity::class.java, 8.0f, 1.4, 1.4))
        goalSelector.add(2, FleeEntityGoal(this, IronGolemEntity::class.java, 8.0f, 1.4, 1.4))
        goalSelector.add(2, FleeEntityGoal(this, WolfEntity::class.java, 8.0f, 1.4, 1.4))
        goalSelector.add(2, LookAtCastTargetGoal())
        goalSelector.add(3, NecromancerSummonGoal(this))
        goalSelector.add(3, EscapeSunlightGoal(this, 1.0))
        goalSelector.add(4, WatchTargetGoal())
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

    override fun initialize(
        world: ServerWorldAccess?,
        difficulty: LocalDifficulty?,
        spawnReason: SpawnReason?,
        entityData: EntityData?,
        entityNbt: NbtCompound?
    ): EntityData? {
        return super.initialize(world, difficulty, spawnReason, entityData, entityNbt).also {
            setCanPickUpLoot(false)
        }
    }

    override fun initEquipment(random: Random?, localDifficulty: LocalDifficulty?) {}

    override fun updateAttackType() {}

    override fun isTeammate(other: Entity): Boolean = super.isTeammate(other) || (other as? MobEntity)?.hasNecromancedOwner == true

    override fun canUseRangedWeapon(weapon: RangedWeaponItem?): Boolean = false

    override fun damage(source: DamageSource, amount: Float): Boolean {
        if (world.isClient || isDead || ignoresSwap(source))
            return super.damage(source, amount)

        if (!super.damage(source, amount / 2)) return false

        if (isDead && !swapDeath) return true

        findSwapTarget()?.let {
            it.damage(source, if (swapDeath) amount else amount / 2)
            swapWith(it)
        }

        swapDeath = false

        return true
    }

    override fun onDeath(damageSource: DamageSource) {
        val world = world as? ServerWorld ?: return super.onDeath(damageSource)
        if (!ignoresSwap(damageSource) && summons.any { uuid -> world.getEntity(uuid)?.let { it.isAlive && it.isOnGround } == true }) {
            health = 0.1f
            swapDeath = true
        } else
            super.onDeath(damageSource)
    }

    private fun ignoresSwap(source: DamageSource): Boolean =
        isInvulnerableTo(source) || source.attacker == null || source in DamageTypeTags.BYPASSES_INVULNERABILITY || (source in DamageTypeTags.IS_FIRE && this.hasStatusEffect(StatusEffects.FIRE_RESISTANCE))

    private fun findSwapTarget(): Entity? {
        val world = world as? ServerWorld ?: return null
        return summons.shuffled().mapNotNull { uuid -> world.getEntity(uuid)?.takeIf { it.isAlive && it.isOnGround } }.let { entities ->
            entities
                .filter { it is ZombieEntity || it is WitherSkeletonEntity }
                .ifEmpty { entities }
                .maxByOrNull { health }
        }
    }

    private fun swapWith(swapTarget: Entity) {
        val world = world as? ServerWorld ?: return

        dismountVehicle()

        swapPos = swapTarget.pos
        swapYaw = swapTarget.yaw
        swapPitch = swapTarget.pitch

        swapTarget.updatePositionAndAngles(this.x, this.y, this.z, this.yaw, this.pitch)

        swapTarget.playSound(SoundEvents.ENTITY_ENDERMAN_TELEPORT, 1f, 1f)
        this.playSound(SoundEvents.ENTITY_ENDERMAN_TELEPORT, 1f, 1f)

        this.navigation.stop()

        world.spawnParticles(NecromancerModParticles.NECROMANCER_TELEPORT_PARTICLE_EMITTER, x, y, z, 1)
        world.spawnParticles(NecromancerModParticles.NECROMANCER_TELEPORT_PARTICLE_EMITTER, swapPos!!.x, swapPos!!.y, swapPos!!.z, 1)

        swapTarget.setVelocity(0.0, 0.0, 0.0)
    }

    override fun tick() {
        super.tick()
        if (isCasting) {
            world.addParticle(
                NecromancerModParticles.NECROMANCER_PARTICLE,
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
        swapPos?.run {
            updatePositionAndAngles(x, y, z, swapYaw ?: yaw, swapPitch ?: pitch)
            swapPos = null
            swapYaw = null
            swapPitch = null
            setVelocity(0.0, 0.0, 0.0)
        }
        if (spellCooldown > 0) spellCooldown--
        if (casting > 0) casting--
        if (castsLeft < MAX_CASTS) {
            castRecharge--
            if (castRecharge <= 0) {
                castRecharge = CAST_RECHARGE_TIME
                castsLeft++
            }
        }
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
        const val COOLDOWN_TIME = 15 * 20
        const val MAX_CASTS = 4
        const val CAST_RECHARGE_TIME = COOLDOWN_TIME * (MAX_CASTS - 1)

        val necromancerAttributes: DefaultAttributeContainer.Builder
            get() = createAbstractSkeletonAttributes().apply {
                add(EntityAttributes.GENERIC_FOLLOW_RANGE, 40.0)
                add(EntityAttributes.GENERIC_MAX_HEALTH, 40.0)
                add(EntityAttributes.GENERIC_ARMOR, 10.0)
            }
    }

    inner class LookAtCastTargetGoal : Goal() {
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

    inner class WatchTargetGoal : Goal() {
        init {
            controls = EnumSet.of(Control.LOOK)
        }

        override fun canStart(): Boolean = this@NecromancerEntity.target != null

        override fun tick() {
            if (target != null)
                lookControl.lookAt(target, maxHeadRotation.toFloat(), maxLookPitchChange.toFloat())
        }
    }
}
