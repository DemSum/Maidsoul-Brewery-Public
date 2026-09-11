package io.github.demsum.maidsoulbrewery.compat.kaleidoscopecookery;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

final class SteamerWorkLocks {
    private static final long CLAIM_TICKS = 600;
    private static final Map<MinecraftServer, Map<Key, Claim>> CLAIMS = new WeakHashMap<>();

    private SteamerWorkLocks() {
    }

    static synchronized boolean tryClaim(ServerLevel level, BlockPos pos, EntityMaid maid) {
        long now = level.getGameTime();
        Map<Key, Claim> serverClaims = CLAIMS.computeIfAbsent(level.getServer(), server -> new HashMap<>());
        serverClaims.entrySet().removeIf(entry -> entry.getValue().expiresAt() <= now);

        Key key = new Key(level.dimension(), pos.immutable());
        Claim claim = serverClaims.get(key);
        if (claim != null && !claim.maidId().equals(maid.getUUID())) {
            return false;
        }
        serverClaims.put(key, new Claim(maid.getUUID(), now + CLAIM_TICKS));
        return true;
    }

    static synchronized void release(ServerLevel level, BlockPos pos, EntityMaid maid) {
        Map<Key, Claim> serverClaims = CLAIMS.get(level.getServer());
        if (serverClaims == null) {
            return;
        }
        Key key = new Key(level.dimension(), pos.immutable());
        Claim claim = serverClaims.get(key);
        if (claim != null && claim.maidId().equals(maid.getUUID())) {
            serverClaims.remove(key);
        }
    }

    private record Key(ResourceKey<Level> dimension, BlockPos pos) {
    }

    private record Claim(UUID maidId, long expiresAt) {
    }
}
