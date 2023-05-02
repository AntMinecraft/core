package com.antonio32a.core.data.player;

import com.antonio32a.core.data.config.Config;
import com.antonio32a.core.data.config.ConfigLoader;
import com.antonio32a.privateapi.data.PlayerProfile;
import com.antonio32a.privateapi.responses.ErrorResponse;
import com.antonio32a.privateapi.responses.Response;
import com.antonio32a.privateapi.responses.player.GetPlayerResponse;
import com.antonio32a.privateapi.responses.player.UpdatePlayerResponse;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public final class PlayerClient {
    public static final PlayerClient INSTANCE = new PlayerClient();

    private static final String PLAYER_ROUTE = "/player";
    private static final Gson GSON = new Gson();
    private final ExecutorService executor = Executors.newCachedThreadPool();
    private final OkHttpClient client = new OkHttpClient();
    private final Config config = ConfigLoader.INSTANCE.getConfig();
    private final HttpUrl playerUrl = Objects.requireNonNull(HttpUrl.parse(config.getPrivateApiUrl() + PLAYER_ROUTE));

    /**
     * Get a player profile by UUID.
     *
     * @param uuid The UUID of the player.
     * @return A CompletableFuture that will be completed with the player profile or null if the profile does not exist.
     */
    @NotNull
    public CompletableFuture<@Nullable PlayerProfile> getPlayer(@NotNull UUID uuid) {
        Request request = new Request.Builder()
            .url(
                playerUrl.newBuilder()
                    .addQueryParameter("id", uuid.toString())
                    .build()
            ).build();

        return makeRequest(GetPlayerResponse.class, request).thenApply(GetPlayerResponse::getPlayer);
    }

    /**
     * Get a player profile by name.
     *
     * @param name The name of the player.
     * @return A CompletableFuture that will be completed with the player profile or null if the profile does not exist.
     */
    @NotNull
    public CompletableFuture<@Nullable PlayerProfile> getPlayerByName(@NotNull String name) {
        Request request = new Request.Builder()
            .url(
                playerUrl.newBuilder()
                    .addQueryParameter("name", name)
                    .build()
            ).build();

        return makeRequest(GetPlayerResponse.class, request).thenApply(GetPlayerResponse::getPlayer);
    }

    /**
     * Update a player profile.
     *
     * @param player The player profile to update.
     * @return A CompletableFuture of void.
     */
    @NotNull
    public CompletableFuture<Void> updatePlayer(@NotNull PlayerProfile player) {
        Request request = new Request.Builder()
            .url(playerUrl)
            .post(
                RequestBody.create(
                    new Gson().toJson(player),
                    MediaType.parse("application/json")
                )
            ).build();

        return makeRequest(UpdatePlayerResponse.class, request).thenApply(response -> null);
    }

    @NotNull
    private <R extends Response> CompletableFuture<R> makeRequest(Class<R> clazz, @NotNull Request request) {
        return CompletableFuture.supplyAsync(() -> {
            try (okhttp3.Response httpResponse = client.newCall(request).execute()) {
                @Nullable ResponseBody body = httpResponse.body();
                if (body == null) {
                    throw new IOException("Response body was null.");
                }

                if (httpResponse.code() != 200) {
                    ErrorResponse errorResponse = GSON.fromJson(body.string(), ErrorResponse.class);
                    log.error("Error {} from private API: {}", errorResponse.getError(), httpResponse.code());
                    throw new IOException(errorResponse.getError());
                }

                return GSON.fromJson(body.string(), clazz);
            } catch (IOException exception) {
                throw new RuntimeException(exception);
            }
        }, executor);
    }
}
