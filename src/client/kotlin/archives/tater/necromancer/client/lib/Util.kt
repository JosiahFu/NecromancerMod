@file:Environment(EnvType.CLIENT)

package archives.tater.necromancer.client.lib

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.model.ModelPartBuilder

inline fun ModelPartBuilder(init: ModelPartBuilder.() -> Unit): ModelPartBuilder = ModelPartBuilder.create().apply(init)
