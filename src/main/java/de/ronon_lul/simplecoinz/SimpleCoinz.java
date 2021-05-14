package de.ronon_lul.simplecoinz;

import de.ronon_lul.simplecoinz.gui.BankScoreBoard;
import de.ronon_lul.simplecoinz.gui.GuiManager;
import de.ronon_lul.simplecoinz.utils.ConfigManager;
import de.ronon_lul.simplecoinz.utils.Database.MongoDBConnector;
import de.ronon_lul.simplecoinz.bank.databaseClasses.BankUser;
import de.ronon_lul.simplecoinz.bank.CommandsBank;
import de.ronon_lul.simplecoinz.npc.CommandsNPC;
import de.ronon_lul.simplecoinz.npc.NPCController;
import de.ronon_lul.simplecoinz.utils.ConsoleLogger;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public final class SimpleCoinz extends JavaPlugin implements Listener {

    private static SimpleCoinz main;
    private static final ConsoleLogger logger = new ConsoleLogger("SimpleCoinz", System.out::println);

    private final List<Consumer<Void>> disableFunctions = new ArrayList<>();

    public static SimpleCoinz getMain() {
        return main;
    }


    //Command Handler
    private CommandsBank commandsBank;
    private CommandsNPC commandsNPC;

    //------------------------------------------------------
    //Enable the Plugin
    @Override
    public void onEnable() {
        logger.log("Enabled");

        //load Config
        ConfigManager configManager = new ConfigManager(this);


        getServer().getPluginManager().registerEvents(this, this);
        GuiManager.setupGuiManager(this);
        MongoDBConnector.setup(configManager.getDbUrl(), configManager.getName());
        main = this;

        //creat Command Handler
        commandsBank = new CommandsBank(logger);
        commandsNPC = new CommandsNPC(new NPCController(this));

    }

    //------------------------------------------------------
    //Disable the Plugin
    @Override
    public void onDisable() {
        disableFunctions.forEach(x -> x.accept(null));
        logger.log("Disabled");
    }

    /**
     * add an void consumer for tasks that need to be done before disabling the Plugin
     * @param func which is needed to execute before the disable
     */
    public void adddisableFunctions(Consumer<Void> func) {
        disableFunctions.add(func);
    }


    //------------------------------------------------------
    //Handler
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        addUser(event.getPlayer());
        new BankScoreBoard(event.getPlayer());
    }

    /**
     * Check if the user already has an account on the Database, if not create it
     * @param player who joined
     */
    private void addUser(Player player) {
        System.out.println("DEBUG LUL");
        Bukkit.getScheduler().runTaskLaterAsynchronously(this, () -> BankUser.asyncBankUserByID(player.getUniqueId().toString(), bankUser -> {
            if (bankUser != null) {
                player.sendMessage("welcome back " + bankUser.getName() + ", your current Bank balance is :" + bankUser.getBank());
            } else {
                //the player ist new!
                BankUser b = new BankUser(player.getName(), player.getUniqueId().toString());
                b.addBankUserToDB();
                player.sendMessage("Welcome to the server, your BankAccount will be initiated soon");
            }
        }), 0);
    }


    /**
     * catches commands
     * @param sender who send the command
     * @param command what was send
     * @param label command as string
     * @param args [possible] arguments
     * @return
     *
     * sends the command to all commandHandlers
     */
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Cou can not do this!");
            return true;
        }

        Player player = (Player) sender;

        //NPC commands
        if (label.equalsIgnoreCase("spawn_npc"))
            return commandsNPC.addNPC(sender, args);


        //Debugging commands for Bank
        if (label.equalsIgnoreCase("showBank")) {
            return commandsBank.showBank(player);
        }
        if (label.equalsIgnoreCase("receive")) {
            return commandsBank.receive(player, args);
        }
        if (label.equalsIgnoreCase("spend")) {
            return commandsBank.spend(player, args);
        }
        if (label.equalsIgnoreCase("cashIN")) {
            return commandsBank.cashIn(player, args);
        }
        if (label.equalsIgnoreCase("cashOut")) {
            return commandsBank.cashOut(player, args);
        }
        return false;
    }
}
