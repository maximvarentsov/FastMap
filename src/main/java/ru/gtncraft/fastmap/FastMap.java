package ru.gtncraft.fastmap;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class FastMap extends JavaPlugin {

    private boolean ignorePackets;
    private ProtocolManager protocolManager;

    @Override
    public void onLoad() {
        protocolManager = ProtocolLibrary.getProtocolManager();
    }

    @Override
    public void onEnable() {
        protocolManager.addPacketListener(
                new PacketAdapter(this, ListenerPriority.NORMAL, PacketType.Play.Server.MAP) {
                    @Override
                    public void onPacketSending(final PacketEvent event) {
                        // Ignore our own packet transmission
                        if (ignorePackets) {
                            return;
                        }

                        int mapId = event.getPacket().getIntegers().read(0);
                        byte[] data = event.getPacket().getByteArrays().read(0);

                        byte type = data[0]; // 0 => [x y] [data]*,
                        // 1 => [x y data]*,
                        // 2 => [map_scale]

                        // See if we are sending the first column
                        if (type == 0 && data[1] == 0 && data[2] == 0) {
                            ignorePackets = true;
                            event.getPlayer().sendMap(Bukkit.getMap((short) mapId));
                            ignorePackets = false;
                        }
                    }
                }
        );
    }
}
