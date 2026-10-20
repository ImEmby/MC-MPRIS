package de.emby.mcpris;

import com.mojang.brigadier.Command;
import de.emby.mcpris.mpris.MprisService;
import de.emby.mcpris.utils.Song;
import de.emby.mcpris.utils.etc;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;
public class ClientCommands {

    @SubscribeEvent
    public static void register(RegisterClientCommandsEvent event) {
        event.getDispatcher().register(
                Commands.literal("mcpris")
                        .then(Commands.literal("get")
                                .then(Commands.literal("title")
                                        .executes(context -> {
                                            context.getSource().sendSystemMessage(
                                                    Component.literal(
                                                            Song.getTitle()
                                                    )
                                            );

                                            return Command.SINGLE_SUCCESS;
                                            }))

                                .then(Commands.literal("artist")
                                        .executes(context -> {
                                            context.getSource().sendSystemMessage(
                                                    Component.literal(
                                                            Song.getArtist()
                                                    )
                                            );

                                            return Command.SINGLE_SUCCESS;
                                        }))

                                .then(Commands.literal("album")
                                        .executes(context -> {
                                            context.getSource().sendSystemMessage(
                                                    Component.literal(
                                                            Song.getAlbum()
                                                    )
                                            );

                                            return Command.SINGLE_SUCCESS;
                                        }))

                                .then(Commands.literal("playing")
                                        .executes(context -> {
                                            context.getSource().sendSystemMessage(
                                                    Component.literal(
                                                            etc.boolToString(
                                                                    Song.getPlaying()
                                                            )
                                                    )
                                            );

                                            return Command.SINGLE_SUCCESS;
                                        }))
                        )
                        .then(Commands.literal("ctrl")
                                .then(Commands.literal("next")
                                        .executes(context -> {
                                            MprisService.nextTrack();
                                            return Command.SINGLE_SUCCESS;
                                        }))

                                .then(Commands.literal("prev")
                                        .executes(context -> {
                                            MprisService.previousTrack();
                                            return Command.SINGLE_SUCCESS;
                                        }))

                                .then(Commands.literal("pause")
                                        .executes(context -> {
                                            MprisService.setPlaying(false);
                                            return Command.SINGLE_SUCCESS;
                                        }))

                                .then(Commands.literal("play")
                                        .executes(context -> {
                                            MprisService.setPlaying(true);
                                            return Command.SINGLE_SUCCESS;
                                        }))

                                .then(Commands.literal("toggle")
                                        .executes(context -> {
                                            MprisService.togglePlayback();
                                            return Command.SINGLE_SUCCESS;
                                        }))
                        )
        );
    }
}