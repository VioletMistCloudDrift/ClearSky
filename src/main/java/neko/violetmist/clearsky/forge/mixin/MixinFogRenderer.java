package neko.violetmist.clearsky.forge.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(FogRenderer.class)
public abstract class MixinFogRenderer {
    @ModifyVariable(
            at = @At(
                    value = "INVOKE_ASSIGN",
                    target = "Lnet/minecraft/util/CubicSampler;gaussianSampleVec3(Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/util/CubicSampler$Vec3Fetcher;)Lnet/minecraft/world/phys/Vec3;"),
            method = "setupColor",
            ordinal = 2,
            require = 1,
            allow = 1)
    private static Vec3 onSampleColor(Vec3 val) {
        final Minecraft mc = Minecraft.getInstance();
        final ClientLevel world = mc.level;
        if (world == null) return val;
        if (world.dimensionType().hasSkyLight()) {
            return world.getSkyColor(mc.gameRenderer.getMainCamera().getPosition(), mc.getFrameTime());
        } else {
            return val;
        }
    }

    @ModifyVariable(at = @At(value = "INVOKE_ASSIGN", target = "Lorg/joml/Vector3f;dot(Lorg/joml/Vector3fc;)F", remap = false), method = "setupColor", ordinal = 7, require = 1, allow = 1)
    private static float afterPlaneDot(float f) {
        return 0;
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientLevel;getRainLevel(F)F"), method = "setupColor", require = 1, allow = 1)
    private static float onGetRainLevel(ClientLevel world, float tickDelta) {
        return 0;
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientLevel;getThunderLevel(F)F"), method = "setupColor", require = 1, allow = 1)
    private static float onGetThunderLevel(ClientLevel world, float tickDelta) {
        return 0;
    }
}
