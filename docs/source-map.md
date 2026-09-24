# Client source map

All paths below are relative to `src/main/java`. Search within `client/` or `worldmap/` before searching the full repository.

| Area | Start here | What it contains |
| --- | --- | --- |
| Startup and window | `client/Client.java` (`main`), `client/ClientWindow.java`, `client/GameShell.java`, `bootstrap/DefaultClientBootstrap.java` | Entry point, main loop, window and input setup |
| Login and networking | `client/Client.java` (`login`, `parsePacket`), `client/BufferedConnection.java`, `client/IncomingPacketLengths.java`, `client/LanServerDiscovery.java` | Server connection, packets, server discovery |
| Preferences | `client/ClientSettings.java`, `client/Client.java` (`userConfig`), `client/SignLink.java` | Config parsing, settings state, cache directory |
| Scene and models | `client/SceneGraph.java`, `client/RegionBuilder.java`, `client/Model.java`, `client/OnDemandFetcher.java` | World scene, region loading, assets |
| Rendering and display | `client/Rasterizer2D.java`, `client/Rasterizer3D.java`, `client/GpuRasterizer3D.java`, `client/GpuPresentationCanvas.java`, `client/ClientWindow.java` | Software and GPU drawing, screen sizing |
| UI and chat | `client/Widget.java`, `client/Sprite.java`, `client/ChatCodec.java`, `client/ChatFilter.java`, `client/Client.java` | Interfaces, images, chat text |
| Cache and archives | `client/CacheStore.java`, `client/Archive.java`, `client/SignLink.java`, `client/GzipDecompressor.java` | Disk cache and archive decoding |
| Audio | `client/PcmPlayer.java`, `client/JavaSoundPcmPlayer.java`, `client/MidiController.java`, `client/SoundEffect.java` | PCM and MIDI playback |
| XP drops | `client/ExperienceDrop.java`, `client/ExperienceDropRenderer.java`, `client/Client.java` (`addExperienceDrop`, `drawExperienceDrops`) | Drop data, drawing, and call sites |
| Castle Wars overlays | `client/CastleWarsOverlay.java`, `client/Client.java` (`drawCastleWarsWalkableOverlay`, `parsePacket`) | Score, status, waiting timer, and catapult aim overlays |
| World map | `worldmap/WorldMapViewer.java`, `worldmap/WorldMapTileExporter.java` | Viewer and control-panel tile export |

## Finding code in `client/Client.java`

`Client.java` contains the game loop and parts of many features. These are starting points, not a complete method list. Method names are more durable than line numbers as the source changes.

| Task | Methods to search for |
| --- | --- |
| Startup and shutdown | `main`, `init`, `startUp`, `processGameLoop`, `mainGameProcessor`, `cleanUpForQuit` |
| Window size and UI scaling | `setScreenMode`, `updateClientWindowSize`, `rebuildViewportBuffers`, `drawFrameBufferToWindow`, `translatePresentationInputCoordinates`, `translateUiInputCoordinates` |
| Configuration | `loadUserConfig`, `reloadUserConfig`, `loadDisplaySettings`, `saveDisplaySettings`, `applyVarpSetting` |
| Login and incoming packets | `login`, `parsePacket`, `parseRegionPackets`, `dropClient` |
| Region loading and world objects | `rebuildWorldRegion`, `processOnDemandQueue`, `updateWorldObject`, `processSpawnedObjects`, `doWalkTo` |
| Players and NPCs | `updatePlayers`, `updateNPCs`, `parsePlayerUpdateMasks`, `parseNpcUpdateMasks`, `updateActor` |
| Scene and camera | `drawGameScreen`, `draw3dScreen`, `addPlayerToScene`, `calculateCameraPosition`, `updateCameraFollow` |
| Interfaces and menus | `drawInterface`, `buildInterfaceMenu`, `processMenuActions`, `drawMenu`, `manageTextInputs` |
| Chat | `drawChatArea`, `pushMessage`, `buildChatAreaMenu`, `processChatModeClick` |
| Minimap | `refreshMinimap`, `calculateMinimapMasks`, `drawMinimap`, `drawMinimapHint` |
| Bank tabs and search | `selectBankTab`, `layoutBankTabs`, `updateBankTabs`, `updateBankSearch` |
| Audio and music | `processAudioQueue`, `requestMusicTrackImmediate`, `requestMusicTrackWithFade`, `playMidiTrack` |
| XP drop calls | `addExperienceDrop`, `drawExperienceDrops` |
| Castle Wars overlay calls | `drawCastleWarsWalkableOverlay`, `draw3dScreen`, `parsePacket` |
| Particles and fog | `updateParticles`, `drawParticles`, `updateFog`, `addParticle` |

For example, run `rg -n -F 'parsePacket(' src/main/java/client/Client.java` from the repository root, then inspect the declaration and nearby callers. Search only `src/main/java` for Java code; `runtime/cache/` contains required game data, and `build/` contains generated classes, JARs, and compiler logs.

Castle Wars overlay drawing and catapult aim state are in `client/CastleWarsOverlay.java`. Start with `drawCastleWarsGameOverlay`, `drawCastleWarsWaitingOverlay`, or `drawCastleWarsCatapultAimOverlay` there; `Client.java` supplies the viewport layout and receives the aim updates in `parsePacket`.
