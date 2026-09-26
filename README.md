# 2006Scape Singleplayer Client

This is the desktop game client for the 2006Scape single-player project. It is a
Java client for the matching `2006sp-Server`, with the game cache, interfaces,
audio, world map, and client configuration included in this repository.

The default server entry is `127.0.0.1`, so the client connects to a server
running on the same computer. The normal game port is `43594`.

The revision 443 port is in progress. `run.bat` uses the 443 JS5 CRC fetch and
RSA login path for testing against the server's 443 branch. The
client also has a JS5 cache reader for 443 reference tables, named lookups,
multi-file groups, and XTEA-encrypted map locations when a key is supplied. It
can read those files either over JS5 or directly from the local
`runtime/cache` directory. To check the active cache after
running `build.bat --check`, use:

```bat
java -cp "build/classes;lib/client-runtime.jar;lib/lwjgl-2.9.3.jar;lib/theme.jar" client.CacheProbe "runtime\cache"
```

The 443 region loader reads terrain and locations only from JS5 archive 5.
Missing location groups leave the corresponding squares without static objects;
invalid or undecodable groups report an error.

In 443 mode, startup opens JS5 and loads 443 definitions, textures, sounds, and
fonts before the first region packet. It skips the 377 config, texture, and sound
archives. The 443 path now initializes the model cache from JS5 archive 7 and
skips the 377 version-list archive and `OnDemandFetcher`. It uses the 443
Huffman wordpack and leaves chat filtering to the server, without loading the
377 `wordenc` archive. The title and gameframe sprites load from JS5 archive 8,
the title image from archive 10, and native interfaces from archive 3. Old
decorative media groups without named 443 equivalents use empty placeholders.
Music tracks and jingles load as MIDI from archives 6 and 11. The local interface
audit (`client.InterfaceAudit`) checks every interface group. The 443
game packet path loads terrain and XTEA-decrypted locations from JS5 archive 5,
then builds the scene with 443 object definitions, models, sequences, and frames.
The matching server's cache probe decodes all 791 audited map squares without a
377 location fallback. The client cache probe validates the spawn region's 443
locations and all 207 meshes used there. Opcode 29 is dispatched
for local movement/teleports, nearby-player movement/add/remove records, and the
server-used 443 player masks (appearance, forced movement/text, graphics, facing,
animation, public chat, and hits). The post-login bridge also consumes the 443
sidebar, skill, inventory/equipment, player-option, chat-mode, run-energy,
interface-text, message, and walkable/main-interface packets needed by the
server's normal initialization. Other game packets remain incomplete, so this
mode is not yet a fully playable 443 client.

## Requirements

- Windows (the included build and run scripts are batch files)
- JDK 1.8.0_101 (Java SE Development Kit 8u101) - [Oracle Java SE 8 Archive Downloads](https://www.oracle.com/java/technologies/javase/javase8-archive-downloads.html)
- `java`, `javac`, and `jar` available on `PATH`
- The matching `2006sp-Server` project if you want to play locally

The normal client/runtime files are kept in the repository. The first GPU-enabled
build also downloads LWJGL 2.9.3 and its Windows native library bundle directly
from Maven Central; later builds reuse the downloaded copies.

- `lib/theme.jar` - compile-time theme library
- `lib/client-runtime.jar` - launcher and packaging base
- `lib/lwjgl-2.9.3.jar` - downloaded OpenGL binding used by the GPU rasterizer
- `runtime/natives/` - extracted LWJGL Windows native libraries
- `runtime/cache/` - game cache and runtime assets

JDK 8 is recommended because the source uses the legacy `sun.audio.AudioPlayer`
API. A Java Runtime Environment (JRE) alone can run an existing build, but it
cannot compile the source.

## Build

From File Explorer, double-click `build.bat`. From Command Prompt, run:

```bat
cd /d "location of client"
build.bat
```

The script compiles every Java file under `src/main/java` and creates:

```text
build/Client.jar
```

Compiler diagnostics are written under `build/` if a source file fails to
compile.

For a command-line source check without packaging or a `pause`, run:

```bat
build.bat --check
```

It returns a nonzero exit code if any source fails to compile. To run the
server-free smoke checks as well, run `build.bat --smoke`. These checks cover multi-sector
cache reads and writes, chat encoding, 443 JS5 framing and grouped-file decoding,
and the encrypted 443 login packet layout.
A full `build.bat` build also returns a
nonzero exit code on compiler errors and does not package an incomplete JAR.

## Run

1. Build and start the matching revision 443 server.
2. In the server control panel, click **Start Server**.
3. Run the client:

```bat
cd /d "location of client"
run.bat
```

`run.bat` starts the client from the `runtime` directory in revision 443 mode
and reads the promoted cache from `runtime/cache`.

The server's current `port/443` branch defaults to a 443 handshake. The client
console prints the first 20 incoming 443 game packet opcodes and lengths after login.

If the JAR is missing, run `build.bat` first. If the client cannot connect,
confirm that the server is online and listening on port `43594`.

## Renderer modes

The client now defaults to **GPU Rendering**. The `Renderer` menu can switch at
runtime between the OpenGL triangle rasterizer and the original software
`Rasterizer3D` implementation. The same preference is available as
`[GPU_RENDERING];1` (GPU) or `[GPU_RENDERING];0` (software) in `userConfig.cfg`.
If OpenGL or its native library cannot initialize, triangle calls automatically
fall back to the software rasterizer for compatibility.

The GPU path keeps the existing scene/model callers and moves flat, Gouraud,
textured, and depth triangle coverage/interpolation into an off-screen OpenGL
buffer. The main scene stays on the GPU for the full `renderScene` pass and is
read back once before fog/overlays; standalone triangle calls outside that pass
still use a small bounding-box readback. If a legacy-only triangle is encountered
mid-frame, accumulated GPU output is copied back once and the rest of that frame
continues in software.

GPU staging memory deliberately uses resize hysteresis. Small window-size changes
keep existing direct buffers to avoid allocation churn, but a buffer or fallback
Pbuffer is downsized once its capacity is at least four times the current
requirement. Temporary transition-frame storage is released after the handoff,
and renderer disable/context loss drops reusable CPU staging buffers. As a
result, native/heap usage can remain somewhat above the exact current frame size
during normal resizing, but a temporary 4K/fullscreen resize is not retained as
the permanent staging-memory high-water mark.

## Configuration

Client preferences are stored in `runtime/userConfig.cfg`. This includes display
options, renderer selection, loot highlighting, XP drops, key bindings, audio,
and optional saved login details. Close the client before changing settings
marked that way in the file.

## Project layout

The repository also has a short [working guide](AGENTS.md) for automated contributors.

### Source map

All paths below are under `src/main/java/`:

| Area | Start here |
| --- | --- |
| Startup, input and login | `client/Client.java`, `client/ClientWindow.java`, `client/GameShell.java`, `bootstrap/DefaultClientBootstrap.java` |
| 443 networking and cache | `client/Js5Client.java`, `client/LocalCache.java`, `client/Cache.java`, `client/PacketFramer.java`, `client/IncomingPacketLengths.java` |
| World scene and models | `client/RegionBuilder.java`, `client/SceneGraph.java`, `client/Model.java`, `client/SceneObjects.java` |
| Rendering and UI | `client/Rasterizer2D.java`, `client/Rasterizer3D.java`, `client/Widget.java`, `client/Sprite.java` |
| Audio | `client/PcmPlayer.java`, `client/MidiController.java`, `client/SoundEffect.java` |
| World map | `worldmap/WorldMapViewer.java`, `worldmap/WorldMapTileExporter.java` |

`client/Client.java` also contains packet dispatch, region loading, menus, chat,
camera control, and the game loop. Search for the relevant method there before
reading the entire file.

```text
src/main/java/       Client, renderer, networking, audio, UI, and world-map source
lib/                 Bundled dependencies plus downloaded LWJGL jar
runtime/cache/       Required game cache and assets
runtime/userConfig.cfg
build.bat            Compiles and packages the client
run.bat              Launches the built client
build.bat --smoke    Compiles source and runs smoke checks
build/Client.jar     Generated executable JAR
```
