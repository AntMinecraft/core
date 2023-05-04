package com.antonio32a.core.command;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import cloud.commandframework.annotations.suggestions.Suggestions;
import cloud.commandframework.context.CommandContext;
import com.antonio32a.core.api.player.PlayerCache;
import com.antonio32a.privateapi.data.PlayerProfile;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public final class ProfileCommands {
    private static final PlayerCache playerCache = PlayerCache.INSTANCE;

    @CommandMethod("profile test")
    @CommandPermission("core.command.profile.test")
    public void test(@NotNull Player player) {
        playerCache.getOrFetch(player.getUniqueId()).whenComplete((profile, throwable) -> {
            if (throwable != null) {
                player.sendMessage(Component.text("<red>An error occurred while fetching your profile.</red>"));
                throwable.printStackTrace();
                return;
            }

            if (profile == null) {
                player.sendMessage(Component.text("<red>Profile not found.</red>"));
                return;
            }

            @Nullable Integer test = profile.getTest();
            if (test == null) {
                test = 0;
            }

            test++;
            profile.setTest(test);
            player.sendMessage(Component.text("Test: " + test));
        });
    }

    @CommandMethod("profile wipe <target>")
    @CommandPermission("core.command.profile.wipe")
    public void wipe(
        @NotNull Player player,
        @Argument(value = "target", suggestions = "onlinePlayers") @NotNull String target
    ) {
        playerCache.getOrFetchByName(target).whenComplete((profile, throwable) -> {
            if (throwable != null) {
                player.sendMessage(Component.text("<red>Couldn't fetch profile.</red>"));
                throwable.printStackTrace();
                return;
            }

            if (profile == null) {
                player.sendMessage(Component.text("<red>Profile not found.</red>"));
                return;
            }

            PlayerProfile newProfile = PlayerProfile.createDefault(UUID.fromString(profile.getId()), profile.getName());
            playerCache.update(newProfile).whenComplete((ignored, updateThrowable) -> {
                if (updateThrowable != null) {
                    player.sendMessage(Component.text("<red>Couldn't update profile.</red>"));
                    updateThrowable.printStackTrace();
                    return;
                }

                player.sendMessage(Component.text("Wiped profile."));
            });
        });
    }

    @Suggestions("onlinePlayers")
    public List<String> onlinePlayers(@NotNull CommandContext<Player> context, @NotNull String input) {
        return Bukkit.getOnlinePlayers()
            .stream()
            .map(Player::getName)
            .filter(name -> name.toLowerCase().startsWith(input.toLowerCase()))
            .toList();
    }
}
