package de.ronon_lul.simplecoinz.utils;

import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * Helper Class to surpass the NMS version problem with Reflection
 */
public class NMSWrapper {

    public enum ServerVersion {
        v1_16_R3, //unused
        REFLECTED,
        UNKNOWN;

        @Override
        public String toString() {
            return name();
        }
    }

    @Getter
    private static final NMSWrapper instance = new NMSWrapper();

    @Getter
    private final ServerVersion serverVersion;
    @Getter
    private final String serverVersionString;

    private NMSWrapper() {
        serverVersionString = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        ServerVersion version = ServerVersion.UNKNOWN;
        for (ServerVersion option : ServerVersion.values()) {
            if (option.name().equalsIgnoreCase(serverVersionString)) {
                version = option;
            }
        }
        this.serverVersion = version;
    }


    /**
     * Send a specific Packet to an Player
     *
     * @param player who should receive the packet
     * @param packet you want to send
     */
    @SneakyThrows
    public void sendPacket(Player player, Object packet) {
        if (player == null) return;
        Object handle = getHandle(player);
        Object playerConnection = handle.getClass().getField("playerConnection").get(handle);

        playerConnection.getClass().getMethod("sendPacket", getNMSClass("Packet")).invoke(playerConnection, packet);
    }

    /**
     * get the handle for a specific Player
     *
     * @param player you want the handle for
     * @return handle
     */
    @SneakyThrows
    public Object getHandle(Player player) {
        return player.getClass().getMethod("getHandle").invoke(player);
    }

    /**
     * Request a specific NMS Class
     *
     * @param name of the class
     * @return nmsClass
     */
    @SneakyThrows
    public Class<?> getNMSClass(String name) {
        return Class.forName("net.minecraft.server." + getServerVersion() + "." + name);
    }

    /**
     * Request a specific CraftBukkit Class
     *
     * @param name of the class
     * @return the Class
     */
    @SneakyThrows
    public Class<?> getCraftBukkitClass(String name) {
        return Class.forName("org.bukkit.craftbukkit." + getServerVersion() + "." + name);
    }
}
