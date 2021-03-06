package com.robotgryphon.compactmachines.util;

import com.robotgryphon.compactmachines.data.CompactMachineServerData;
import com.robotgryphon.compactmachines.data.machines.CompactMachinePlayerData;
import com.robotgryphon.compactmachines.network.MachinePlayersChangedPacket;
import com.robotgryphon.compactmachines.network.NetworkHandler;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.Optional;

public class CompactMachinePlayerUtil {
    public static void addPlayerToMachine(ServerPlayerEntity serverPlayer, BlockPos machinePos, int machineId) {
        MinecraftServer serv = serverPlayer.getServer();
        CompactMachineServerData serverData = CompactMachineServerData.getInstance(serv);
        Optional<CompactMachinePlayerData> playerData = serverData.getPlayerData(machineId);

        playerData.ifPresent(d -> {
            d.addPlayer(serverPlayer);
            serverData.markDirty();

            MachinePlayersChangedPacket p = new MachinePlayersChangedPacket(machineId, serverPlayer.getUniqueID(), MachinePlayersChangedPacket.EnumPlayerChangeType.ENTERED);
            NetworkHandler.MAIN_CHANNEL.send(
                    PacketDistributor.TRACKING_CHUNK.with(() -> serverPlayer.getServerWorld().getChunkAt(machinePos)),
                    p);
        });
    }

    public static void removePlayerFromMachine(ServerPlayerEntity serverPlayer, BlockPos machinePos, int machineId) {
        CompactMachineServerData serverData = CompactMachineServerData.getInstance(serverPlayer.getServer());
        Optional<CompactMachinePlayerData> playerData = serverData.getPlayerData(machineId);

        playerData.ifPresent(d -> {
            d.removePlayer(serverPlayer);
            serverData.markDirty();

            MachinePlayersChangedPacket p = new MachinePlayersChangedPacket(machineId, serverPlayer.getUniqueID(), MachinePlayersChangedPacket.EnumPlayerChangeType.EXITED);
            NetworkHandler.MAIN_CHANNEL.send(
                    PacketDistributor.TRACKING_CHUNK.with(() -> serverPlayer.getServerWorld().getChunkAt(machinePos)),
                    p);
        });
    }
}
