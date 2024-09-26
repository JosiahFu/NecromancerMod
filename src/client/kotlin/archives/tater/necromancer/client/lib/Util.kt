@file:Environment(EnvType.CLIENT)

package archives.tater.necromancer.client.lib

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.model.Dilation
import net.minecraft.client.model.ModelPartBuilder

inline fun ModelPartBuilder(init: ModelPartBuilder.() -> Unit): ModelPartBuilder = ModelPartBuilder.create().apply(init)

fun ModelPartBuilder.cuboid(
    uvX: Int,
    uvY: Int,
    offsetX: Float,
    offsetY: Float,
    offsetZ: Float,
    sizeX: Float,
    sizeY: Float,
    sizeZ: Float,
    name: String? = null,
    mirrored: Boolean = false,
    extra: Dilation = Dilation.NONE,
) {
    mirrored(mirrored)
    uv(uvX, uvY)
    cuboid(name, offsetX, offsetY, offsetZ, sizeX, sizeY, sizeZ, extra)
}
fun ModelPartBuilder.cuboid(
    uvX: Int,
    uvY: Int,
    offsetX: Float,
    offsetY: Float,
    offsetZ: Float,
    sizeX: Float,
    sizeY: Float,
    sizeZ: Float,
    name: String? = null,
    mirrored: Boolean = false,
    extra: Float,
) {
    cuboid(uvX, uvY, offsetX, offsetY, offsetZ, sizeX, sizeY, sizeZ, name, mirrored, Dilation(extra))
}
