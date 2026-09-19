# StationAPI Fix

Unofficial fix for the **StationAPI 2.0 startup crash on Android launchers** (Zalith Launcher 2).

# Warning

use https://modrinth.com/mod/stationapis-no-startup-screen by FarnGitHub

![Minecraft](https://img.shields.io/badge/Minecraft-b1.7.3-62B47A)




![Loader](https://img.shields.io/badge/Loader-Babric%2FFabric-DBB18D)




![Requires](https://img.shields.io/badge/Requires-StationAPI-blue)




![Side](https://img.shields.io/badge/Side-client-lightgrey)
## The problem

On some Android launchers, Minecraft b1.7.3 with StationAPI 2.0 crashes right after the mods finish loading, before the main menu appears. Without StationAPI the game starts fine.

The JVM crash report (`hs_err_pid*.log`) shows a native crash with this stack:

```
C  libpojavexec.so  gl_swap_buffers+0x24
C  libpojavexec.so  pojavSwapBuffers+0x94
j  org.lwjgl.glfw.GLFW.glfwSwapBuffers
j  org.lwjgl.opengl.Display.update()
j  net.modificationstation.stationapi.api.client.resource.ReloadScreenManager.onStartup
```

## Cause

StationAPI's early reload (loading) screen runs on its own thread and calls `Display.update()` from there. In the launcher's native GL bridge that thread has no window surface bound, so swapping buffers dereferences a null pointer (`SIGSEGV`, `si_addr 0x0`) and the whole process dies.

## What this mod does

One Mixin redirects the `Display.update()` call inside `ReloadScreenManager.onStartup` to a tiny 10 ms sleep. Resource loading continues as normal; only the buffer swap from that thread is skipped. Nothing else in StationAPI is touched.

## Side effects

- The animated StationAPI loading screen is **not shown** while resources load.

## Requirements

- Minecraft b1.7.3 with Babric / Fabric Loader
- [StationAPI](https://modrinth.com/mod/stationapi) 2.0 (required)
- Client side only

## Installation

Put the jar into the `mods` folder next to StationAPI.

## Compatibility

- **Tested:** Zalith Launcher 2 (2.3.3), Android 14, Krypton Wrapper renderer, StationAPI 2.0.0-alpha.6.4, Fabric Loader 0.19.5.
- Other Android launchers (e.g. PojavLauncher) may hit the same problem, but this is untested.
- Not needed on desktop launchers. If installed there, you would only lose the loading animation.
- If a future StationAPI version changes this code, the mod does nothing (it is set not to fail) and will not crash the game. If the crash comes back after a StationAPI update, please open an issue.

## Related

- [ZalithLauncher2 issue #1441](https://github.com/ZalithLauncher/ZalithLauncher2/issues/1441): StationAPI 2.0 on Babric b1.7.3 crashes on startup.

## License and credits

Licensed under **LGPL-3.0-only**.

StationAPI is made by the Modification Station team (MIT license). This mod is unofficial and not affiliated with them; the icon uses the StationAPI logo.

[![Claude](https://img.shields.io/badge/Claude-D97757?logo=claude&logoColor=fff)](#)
