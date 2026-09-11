package io.github.demsum.maidsoulbrewery.network;

import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

public final class MaidSoulBreweryNetwork {
    private static final String PROTOCOL_VERSION = "1";

    private MaidSoulBreweryNetwork() {
    }

    public static void register(RegisterPayloadHandlersEvent event) {
        event.registrar(PROTOCOL_VERSION).playToServer(
                SetSteamerFilterPayload.TYPE,
                SetSteamerFilterPayload.STREAM_CODEC,
                SetSteamerFilterPayload::handle
        );
    }
}
