package dev.local.noreloadswap.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Workaround: StationAPI's early reload screen calls Display.update() from its own
 * thread, which makes Zalith's libpojavexec crash (SIGSEGV in gl_swap_buffers).
 * Here that single call is replaced with a short sleep, so the reload itself still
 * runs, but the frame is never swapped from that thread.
 */
@Mixin(
        targets = "net.modificationstation.stationapi.api.client.resource.ReloadScreenManager",
        remap = false
)
public class ReloadScreenManagerMixin {

    @Redirect(
            method = "onStartup",
            at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/Display;update()V"),
            remap = false,
            require = 0 // if another StationAPI version has no such call, silently skip instead of crashing
    )
    private static void noreloadswap$skipSwap() {
        try {
            Thread.sleep(10L); // don't burn a CPU core in the loop
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
