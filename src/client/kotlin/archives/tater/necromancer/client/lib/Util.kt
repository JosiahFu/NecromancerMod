package archives.tater.necromancer.client.lib

import net.minecraft.client.model.ModelPartBuilder

fun ModelPartBuilder(init: ModelPartBuilder.() -> Unit): ModelPartBuilder = ModelPartBuilder.create().apply(init)
