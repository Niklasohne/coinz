package de.ronon_lul.simplecoinz.npc.api;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import de.ronon_lul.simplecoinz.npc.NPCTypes;
import de.ronon_lul.simplecoinz.npc.api.events.NPCClickEvent;
import de.ronon_lul.simplecoinz.utils.NMSWrapper;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * part src: https://github.com/JordanOsterberg/NPCAPI/blob/master/src/main/java/dev/jcsoftware/npcs/versioned/NPC_Reflection.java
 * <p>
 * Simple NPC implementation with Reflexion to not relay on NMS
 */
public class SimpleNPC implements NPC {

    private final JavaPlugin plugin;
    private final UUID uuid = UUID.randomUUID();
    @Getter
    private final String name;
    private final String entityName;
    private final String texture;
    private final String signature;
    private final boolean hideNametag;
    private final Consumer<NPCClickEvent> handler;
    private final NPCTypes type;

    private Object entityPlayerObj;

    private final Set<UUID> viewers = new HashSet<>();

    public SimpleNPC(JavaPlugin plugin, NPCInfos infos) {
        this.plugin = plugin;
        this.name = infos.getName();
        this.texture = infos.getTexture();
        this.signature = infos.getSignature();
        this.hideNametag = infos.isHideNameTag();

        this.entityName = hideNametag ? uuid.toString() : name;

        this.handler = infos.getHandler();
        addToWorld(infos.getLocation());
        type = infos.getType();
    }


    /**
     * make the NPC ready to get spawned on an world
     *
     * @param location where the NPC will spawn
     */
    @SneakyThrows
    private void addToWorld(Location location) {
        NMSWrapper nmsWrapper = NMSWrapper.getInstance();

        Object minecraftServer = nmsWrapper.getCraftBukkitClass("CraftServer").getMethod("getServer").invoke(Bukkit.getServer());
        Object worldServer = nmsWrapper.getCraftBukkitClass("CraftWorld").getMethod("getHandle").invoke(location.getWorld());

        GameProfile gameProfile = makeGameProfile();

        //Get NMS constructors
        Constructor<?> entityPlayerConstructor = nmsWrapper.getNMSClass("EntityPlayer").getDeclaredConstructors()[0];
        Constructor<?> interactManagerConstructor = nmsWrapper.getNMSClass("PlayerInteractManager").getDeclaredConstructors()[0];
        Object interactManager = interactManagerConstructor.newInstance(worldServer);


        this.entityPlayerObj = entityPlayerConstructor.newInstance(minecraftServer, worldServer, gameProfile, interactManager);

        this.entityPlayerObj.getClass().getMethod("setLocation", double.class, double.class, double.class, float.class, float.class).invoke(entityPlayerObj,
                location.getX(),
                location.getY(),
                location.getZ(),
                location.getYaw(),
                location.getPitch()
        );
    }

    private GameProfile makeGameProfile() {
        GameProfile gameProfile = new GameProfile(uuid, entityName);
        gameProfile.getProperties().put("textures", new Property("textures", texture, signature));
        return gameProfile;
    }


    /**
     * show a specific Player this NPC
     * <p>
     * first checks if they are in the same world
     *
     * @param player the player
     */
    @Override
    public void showTo(Player player) {
        if (!player.getWorld().getName().equals(getLocation().getWorld().getName()))
            return;
        try {
            NMSWrapper nmsWrapper = NMSWrapper.getInstance();

            viewers.add(player.getUniqueId());

            // PacketPlayOutPlayerInfo
            Object addPlayerEnum = nmsWrapper.getNMSClass("PacketPlayOutPlayerInfo$EnumPlayerInfoAction").getField("ADD_PLAYER").get(null);
            Constructor<?> packetPlayOutPlayerInfoConstructor = nmsWrapper.getNMSClass("PacketPlayOutPlayerInfo")
                    .getConstructor(
                            nmsWrapper.getNMSClass("PacketPlayOutPlayerInfo$EnumPlayerInfoAction"),
                            Class.forName("[Lnet.minecraft.server." + nmsWrapper.getServerVersionString() + ".EntityPlayer;")
                    );

            Object array = Array.newInstance(nmsWrapper.getNMSClass("EntityPlayer"), 1);
            Array.set(array, 0, entityPlayerObj);

            Object packetPlayOutPlayerInfo = packetPlayOutPlayerInfoConstructor.newInstance(addPlayerEnum, array);
            sendPacket(player, packetPlayOutPlayerInfo);

            // PacketPlayOutNamedEntitySpawn
            Constructor<?> packetPlayOutNamedEntitySpawnConstructor = nmsWrapper.getNMSClass("PacketPlayOutNamedEntitySpawn")
                    .getConstructor(nmsWrapper.getNMSClass("EntityHuman"));
            Object packetPlayOutNamedEntitySpawn = packetPlayOutNamedEntitySpawnConstructor.newInstance(this.entityPlayerObj);
            sendPacket(player, packetPlayOutNamedEntitySpawn);

            // Scoreboard Team
            Object scoreboardManager = Bukkit.getServer().getClass().getMethod("getScoreboardManager")
                    .invoke(Bukkit.getServer());
            Object mainScoreboard = scoreboardManager.getClass().getMethod("getMainScoreboard")
                    .invoke(scoreboardManager);
            Object scoreboard = mainScoreboard.getClass().getMethod("getHandle").invoke(mainScoreboard);

            Method getTeamMethod = scoreboard.getClass().getMethod("getTeam", String.class);
            Constructor<?> scoreboardTeamConstructor = nmsWrapper.getNMSClass("ScoreboardTeam").getDeclaredConstructor(nmsWrapper.getNMSClass("Scoreboard"), String.class);

            Object scoreboardTeam = getTeamMethod.invoke(scoreboard, entityName) == null ?
                    scoreboardTeamConstructor.newInstance(scoreboard, entityName) :
                    getTeamMethod.invoke(scoreboard, entityName);

            Class<?> nameTagStatusEnum = nmsWrapper.getNMSClass("ScoreboardTeamBase$EnumNameTagVisibility");
            Method setNameTagVisibility = scoreboardTeam.getClass().getMethod("setNameTagVisibility", nameTagStatusEnum);

            if (hideNametag) {
                setNameTagVisibility.invoke(scoreboardTeam, nameTagStatusEnum.getField("NEVER").get(null));
            } else {
                setNameTagVisibility.invoke(scoreboardTeam, nameTagStatusEnum.getField("ALWAYS").get(null));
            }

            Class<?> collisionStatusEnum = nmsWrapper.getNMSClass("ScoreboardTeamBase$EnumTeamPush");
            Method setCollisionRule = scoreboardTeam.getClass().getMethod("setCollisionRule", collisionStatusEnum);

            setCollisionRule.invoke(scoreboardTeam, collisionStatusEnum.getField("NEVER").get(null));

            if (hideNametag) {
                Object grayChatFormat = nmsWrapper.getNMSClass("EnumChatFormat").getField("GRAY").get(null);
                scoreboardTeam.getClass().getMethod("setColor", nmsWrapper.getNMSClass("EnumChatFormat"))
                        .invoke(scoreboardTeam, grayChatFormat);

                Constructor<?> chatMessageConstructor = nmsWrapper.getNMSClass("ChatMessage").getDeclaredConstructor(String.class);
                scoreboardTeam.getClass().getMethod("setPrefix", nmsWrapper.getNMSClass("IChatBaseComponent"))
                        .invoke(scoreboardTeam, chatMessageConstructor.newInstance(ChatColor.COLOR_CHAR + "7[NPC] "));
            }

            Class<?> packetPlayOutScoreboardTeamClass = nmsWrapper.getNMSClass("PacketPlayOutScoreboardTeam");
            Constructor<?> packetPlayOutScoreboardTeamTeamIntConstructor = packetPlayOutScoreboardTeamClass.getConstructor(nmsWrapper.getNMSClass("ScoreboardTeam"), int.class);
            Constructor<?> packetPlayOutScoreboardTeamTeamCollectionIntConstructor = packetPlayOutScoreboardTeamClass.getConstructor(nmsWrapper.getNMSClass("ScoreboardTeam"), Collection.class, int.class);

            sendPacket(player, packetPlayOutScoreboardTeamTeamIntConstructor.newInstance(scoreboardTeam, 1));
            sendPacket(player, packetPlayOutScoreboardTeamTeamIntConstructor.newInstance(scoreboardTeam, 0));
            sendPacket(
                    player,
                    packetPlayOutScoreboardTeamTeamCollectionIntConstructor.newInstance(
                            scoreboardTeam,
                            Collections.singletonList(entityName),
                            3
                    )
            );

            sendHeadRotationPacket(player);

            Bukkit.getServer().getScheduler().runTaskTimer(plugin, task -> {
                Player currentlyOnline = Bukkit.getPlayer(player.getUniqueId());
                if (currentlyOnline == null || !currentlyOnline.isOnline()) {
                    task.cancel();
                    return;
                }

                sendHeadRotationPacket(player);
            }, 0, 2);

            Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> {
                try {
                    Object removePlayerEnum = nmsWrapper.getNMSClass("PacketPlayOutPlayerInfo$EnumPlayerInfoAction").getField("REMOVE_PLAYER").get(null);
                    Object removeFromTabPacket = packetPlayOutPlayerInfoConstructor.newInstance(removePlayerEnum, array);
                    sendPacket(player, removeFromTabPacket);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }, 20);

            Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> fixSkinHelmetLayerForPlayer(player), 8);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * hide this NPC for a specific player
     *
     * @param player the specific player
     */
    @Override
    public void hideTo(Player player) {
        if (!viewers.contains(player.getUniqueId())) return;
        viewers.remove(player.getUniqueId());

        try {
            Constructor<?> destroyPacketConstructor = NMSWrapper.getInstance()
                    .getNMSClass("PacketPlayOutEntityDestroy").getConstructor(int[].class);
            Object packet = destroyPacketConstructor.newInstance((Object) new int[]{getId()});
            sendPacket(player, packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * show this NPC to every only Player
     */
    @Override
    public void showAll() {
        Bukkit.getOnlinePlayers().forEach(this::showTo);
    }

    /**
     * hide this NPC from every only Player
     */
    @Override
    public void hideAll() {
        Set<Player> onlineViewers = viewers.stream()
                .map(Bukkit::getPlayer)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        onlineViewers.forEach(this::hideTo);
    }
    /**
     * makes sure the NPC always looks at a player
     * @param player the player
     */
    private void sendHeadRotationPacket(Player player) {
        if (!player.getWorld().getName().equals(getLocation().getWorld().getName()))
            return;

        NMSWrapper nmsWrapper = NMSWrapper.getInstance();

        Location original = getLocation();
        Location location = original.clone().setDirection(player.getLocation().subtract(original.clone()).toVector());

        byte yaw = (byte) (location.getYaw() * 256 / 360);
        byte pitch = (byte) (location.getPitch() * 256 / 360);

        try {
            // PacketPlayOutEntityHeadRotation
            Constructor<?> packetPlayOutEntityHeadRotationConstructor = nmsWrapper.getNMSClass("PacketPlayOutEntityHeadRotation")
                    .getConstructor(nmsWrapper.getNMSClass("Entity"), byte.class);

            Object packetPlayOutEntityHeadRotation = packetPlayOutEntityHeadRotationConstructor.newInstance(entityPlayerObj, yaw);
            sendPacket(player, packetPlayOutEntityHeadRotation);

            Constructor<?> packetPlayOutEntityLookConstructor = nmsWrapper.getNMSClass("PacketPlayOutEntity$PacketPlayOutEntityLook")
                    .getConstructor(int.class, byte.class, byte.class, boolean.class);
            Object packetPlayOutEntityLook = packetPlayOutEntityLookConstructor.newInstance(getId(), yaw, pitch, false);
            sendPacket(player, packetPlayOutEntityLook);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * makes sure the NPCs helmet (if existing) will get displayed in the right way
     * @param player the player
     */
    public void fixSkinHelmetLayerForPlayer(Player player) {
        if (!player.getWorld().toString().equals(getLocation().toString()))
            return;
        Byte skinFixByte = 0x01 | 0x02 | 0x04 | 0x08 | 0x10 | 0x20 | 0x40;
        sendMetadata(player, skinFixByte);
    }

    private void sendMetadata(Player player, Byte o) {
        NMSWrapper nmsWrapper = NMSWrapper.getInstance();

        try {
            Object dataWatcher = entityPlayerObj.getClass().getMethod("getDataWatcher").invoke(entityPlayerObj);
            Class<?> dataWatcherRegistryClass = nmsWrapper.getNMSClass("DataWatcherRegistry");
            Object registry = dataWatcherRegistryClass.getField("a").get(null);
            Method watcherCreateMethod = registry.getClass().getMethod("a", int.class);

            Method dataWatcherSetMethod = dataWatcher.getClass().getMethod("set", nmsWrapper.getNMSClass("DataWatcherObject"), Object.class);
            dataWatcherSetMethod.invoke(dataWatcher, watcherCreateMethod.invoke(registry, 16), o);

            Constructor<?> packetPlayOutEntityMetadataConstructor = nmsWrapper.getNMSClass("PacketPlayOutEntityMetadata")
                    .getDeclaredConstructor(int.class, nmsWrapper.getNMSClass("DataWatcher"), boolean.class);
            sendPacket(player, packetPlayOutEntityMetadataConstructor.newInstance(getId(), dataWatcher, false));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendPacket(Player player, Object packet) {
        NMSWrapper.getInstance().sendPacket(player, packet);
    }

    @Override
    public Location getLocation() {
        try {
            Class<?> EntityPlayer = entityPlayerObj.getClass();

            Object minecraftWorld = EntityPlayer.getMethod("getWorld").invoke(entityPlayerObj);
            Object craftWorld = minecraftWorld.getClass().getMethod("getWorld").invoke(minecraftWorld);

            double locX = (double) EntityPlayer.getMethod("locX").invoke(entityPlayerObj);
            double locY = (double) EntityPlayer.getMethod("locY").invoke(entityPlayerObj);
            double locZ = (double) EntityPlayer.getMethod("locZ").invoke(entityPlayerObj);
            float yaw = (float) EntityPlayer.getField("yaw").get(entityPlayerObj);
            float pitch = (float) EntityPlayer.getField("pitch").get(entityPlayerObj);

            return new Location(
                    (World) craftWorld,
                    locX,
                    locY,
                    locZ,
                    yaw,
                    pitch
            );
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public int getId() {
        if (entityPlayerObj == null) return -1;

        try {
            Method getId = entityPlayerObj.getClass().getSuperclass().getSuperclass().getSuperclass().getDeclaredMethod("getId");
            return (int) getId.invoke(entityPlayerObj);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    @Override
    public NPCTypes getType() {
        return type;
    }

    public void handleInteraction(NPCClickEvent event) {
        handler.accept(event);
    }


}
