package io.github.demsum.maidsoulbrewery.network;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import io.github.demsum.maidsoulbrewery.MaidSoulBrewery;
import io.github.demsum.maidsoulbrewery.compat.kaleidoscopecookery.KaleidoscopeCookeryCompat;
import io.github.demsum.maidsoulbrewery.compat.touhoulittlemaid.RecipeFilterDataKeys;
import io.github.demsum.maidsoulbrewery.recipe.RecipeFilterData;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SetSteamerFilterPayload(int maidId, RecipeFilterData filter) implements CustomPacketPayload {
    private static final int MAX_RECIPE_IDS = 4096;

    public static final Type<SetSteamerFilterPayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(MaidSoulBrewery.MOD_ID, "set_steamer_filter")
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, SetSteamerFilterPayload> STREAM_CODEC =
            StreamCodec.ofMember(SetSteamerFilterPayload::encode, SetSteamerFilterPayload::decode);

    private static SetSteamerFilterPayload decode(RegistryFriendlyByteBuf buffer) {
        int maidId = buffer.readVarInt();
        RecipeFilterData.Mode mode = buffer.readEnum(RecipeFilterData.Mode.class);
        List<ResourceLocation> whitelist = readIds(buffer);
        List<ResourceLocation> blacklist = readIds(buffer);
        return new SetSteamerFilterPayload(maidId, new RecipeFilterData(mode, whitelist, blacklist));
    }

    private void encode(RegistryFriendlyByteBuf buffer) {
        buffer.writeVarInt(maidId);
        buffer.writeEnum(filter.mode());
        writeIds(buffer, filter.whitelist());
        writeIds(buffer, filter.blacklist());
    }

    private static List<ResourceLocation> readIds(RegistryFriendlyByteBuf buffer) {
        int size = buffer.readVarInt();
        if (size < 0 || size > MAX_RECIPE_IDS) {
            throw new IllegalArgumentException("Invalid steamer recipe filter size: " + size);
        }
        List<ResourceLocation> ids = new ArrayList<>(size);
        for (int index = 0; index < size; index++) {
            ids.add(ResourceLocation.STREAM_CODEC.decode(buffer));
        }
        return ids;
    }

    private static void writeIds(RegistryFriendlyByteBuf buffer, List<ResourceLocation> ids) {
        if (ids.size() > MAX_RECIPE_IDS) {
            throw new IllegalArgumentException("Steamer recipe filter is too large: " + ids.size());
        }
        buffer.writeVarInt(ids.size());
        for (ResourceLocation id : ids) {
            ResourceLocation.STREAM_CODEC.encode(buffer, id);
        }
    }

    public static void handle(SetSteamerFilterPayload payload, IPayloadContext context) {
        if (!context.flow().isServerbound()) {
            return;
        }
        context.enqueueWork(() -> apply(payload, context.player()));
    }

    private static void apply(SetSteamerFilterPayload payload, Player player) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }
        Entity entity = serverPlayer.level().getEntity(payload.maidId());
        if (!(entity instanceof EntityMaid maid)
                || !maid.isOwnedBy(serverPlayer)) {
            return;
        }

        Set<ResourceLocation> knownRecipes = new HashSet<>();
        KaleidoscopeCookeryCompat.getSteamerRecipeOptions(maid.level())
                .forEach(option -> knownRecipes.add(option.id()));
        RecipeFilterData sanitized = new RecipeFilterData(
                payload.filter().mode(),
                sanitize(payload.filter().whitelist(), knownRecipes),
                sanitize(payload.filter().blacklist(), knownRecipes)
        );
        maid.setAndSyncData(RecipeFilterDataKeys.steamerKey(), sanitized);
    }

    private static List<ResourceLocation> sanitize(List<ResourceLocation> ids, Set<ResourceLocation> knownRecipes) {
        return ids.stream().distinct().filter(knownRecipes::contains).sorted().toList();
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
