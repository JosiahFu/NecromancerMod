package archives.tater.necromancer.mixin;

import archives.tater.necromancer.entity.ai.goal.AttackWithNecromancerGoal;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ZombieEntity.class)
public abstract class ZombieEntityMixin extends HostileEntity {
	protected ZombieEntityMixin(EntityType<? extends HostileEntity> entityType, World world) {
		super(entityType, world);
	}

	@Inject(at = @At("TAIL"), method = "initGoals")
	private void init(CallbackInfo info) {
		targetSelector.add(1, new AttackWithNecromancerGoal(this));
	}
}
