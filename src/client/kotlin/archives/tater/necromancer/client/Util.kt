package archives.tater.necromancer.client

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.model.ModelPartBuilder

fun ModelPartBuilder(init: ModelPartBuilder.() -> Unit) = ModelPartBuilder.create().apply(init)
