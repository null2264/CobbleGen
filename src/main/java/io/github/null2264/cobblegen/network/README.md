# Networking For Recipe Viewer Integration (Recipe Sync)

## Packet Handling

### Server-side

Custom payloads are intercepted in `mixin/network/ServerCommonPacketListenerMixin.java` to be handled by `CGServerPlayNetworkHandler`, if custom payload are not made by CobbleGen then we let Minecraft (or other mods) handle it.

### Client-side

Custom payloads are intercepted in `mixin/network/ClientCommonPacketListenerMixin.java` to be handled by `CGClientPlayNetworkHandler`, if custom payload are not made by CobbleGen then we let Minecraft (or other mods) handle it.

## Flow

```mermaid
graph TD
    A[Player joined]-->B[1.20.2 or newer - ServerConfigurationPacketListener.startConfiguration#40;#41;]
    A-->C[1.20.1 or older - ServerCommonPacketListener.#60;init#62;#40;#41;]
    B-->D[CGServerPlayNetworkHandler.trySync#40;#41; // Send 'ping']
    C-->D
    D-->F[CGClientPlayNetworkHandler.handlePacket#40;#41; // Receive 'ping'. Send 'ping received, shouldSync = hasRecipeViewer']
    F-->G[CGServerPlayNetworkHandler.handlePacket#40;#41; // Receive 'ping received, shouldSync'. Send 'data if shouldSync']
```
