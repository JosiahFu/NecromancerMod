package archives.tater.necromancer.cca

import archives.tater.necromancer.Necromancer
import dev.onyxstudios.cca.api.v3.component.Component
import dev.onyxstudios.cca.api.v3.component.ComponentKey
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.mob.MobEntity
import net.minecraft.nbt.NbtCompound
import net.minecraft.server.world.ServerWorld
import java.util.*

class NecromancedComponent(private val mob: MobEntity) : Component, AutoSyncedComponent {
    var ownerUUID: UUID? = null
        private set
    var emergeTicks: Int = 0
        private set

    override fun readFromNbt(tag: NbtCompound) {
        if (tag.contains(OWNER_UUID))
            ownerUUID = tag.getUuid(OWNER_UUID)
        emergeTicks = tag.getInt(EMERGE_TICKS)
    }

    override fun writeToNbt(tag: NbtCompound) {
        if (ownerUUID != null)
            tag.putUuid(OWNER_UUID, ownerUUID)
        tag.putInt(EMERGE_TICKS, emergeTicks)
    }

    companion object {
        private const val OWNER_UUID = "Owner"
        private const val EMERGE_TICKS = "EmergeTicks"

        const val MAX_EMERGE_TICKS = 40

        val KEY: ComponentKey<NecromancedComponent> = ComponentRegistry.getOrCreate(Necromancer.id("necromanced"), NecromancedComponent::class.java)

        @JvmStatic
        @get:JvmName("getOwnerUUID")
        val MobEntity.necromancedOwnerUUID: UUID?
            get() = KEY.getNullable(this)?.ownerUUID

        @JvmStatic
        @get:JvmName("getOwner")
        var MobEntity.necromancedOwner: LivingEntity?
            get() {
                val serverWorld = this.world as? ServerWorld ?: return null
                val uuid = KEY.getNullable(this)?.ownerUUID ?: return null
                return serverWorld.getEntity(uuid) as? LivingEntity
            }
            set(entity) {
                KEY.get(this).ownerUUID = entity?.uuid
                KEY.sync(this)
            }

        @JvmStatic
        @get:JvmName("hasNecromancedOwner")
        val MobEntity.hasNecromancedOwner: Boolean get() = KEY.getNullable(this)?.ownerUUID != null

        /**
         * value is 0 if not present
         */
        @JvmStatic
        val MobEntity.emergeTicks: Int
            get() = KEY.getNullable(this)?.emergeTicks ?: 0

        @JvmStatic
        fun MobEntity.startEmerge() {
            KEY.getNullable(this)?.emergeTicks = MAX_EMERGE_TICKS
        }

        @JvmStatic
        fun MobEntity.tickEmerge() {
            KEY.getNullable(this)?.run {
                if (emergeTicks <= 0) return
                emergeTicks--
                KEY.sync(this@tickEmerge)
            }
        }
    }
}
