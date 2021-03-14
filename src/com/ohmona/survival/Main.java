package com.ohmona.survival;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.CommandBlock;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.Collection;

public class Main extends JavaPlugin implements CommandExecutor, Listener {

    //Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
    //Objective obj = board.registerNewObjective("members", "dummy", "members");
    //org.bukkit.scoreboard.Team member = board.registerNewTeam("members");
    //org.bukkit.scoreboard.Team spectator = board.registerNewTeam("spectator");
    World w;
    World w2;
    World w3;
    WorldBorder border1 = w.getWorldBorder();
    WorldBorder border2 = w2.getWorldBorder();
    WorldBorder border3 = w3.getWorldBorder();

    //
    // Main
    //
    @Override
    public void onEnable() {

        for(int i = 0; i < 3; i++) {
            if(Bukkit.getWorlds().get(i).getName().endsWith("world")) {
                w = Bukkit.getWorlds().get(i);
            }
            else if(Bukkit.getWorlds().get(i).getName().endsWith("_nether")) {
                w2 = Bukkit.getWorlds().get(i);
            }
            else if(Bukkit.getWorlds().get(i).getName().endsWith("_the_end")) {
                w3 = Bukkit.getWorlds().get(i);
            }
        }

        Bukkit.getConsoleSender().sendMessage("plugin enable");

        getServer().getPluginManager().registerEvents(this,this);
        this.getCommand("game").setExecutor(this);

    }

    @Override
    public void onDisable() {
        Bukkit.getConsoleSender().sendMessage("plugin disable");
    }
    //
    // event
    //
    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        e.setJoinMessage("");
        Bukkit.broadcastMessage(e.getPlayer().getName() + " joined to game");
    }
    //
    // cmd
    //
    int time_second = 0;
    boolean isTimerRunning;
    boolean isTimerOn = false;
    int isTimerUpOrDown; // 0 -> up 1 -> down
    int timerMin;
    int timerSec;

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(sender instanceof Player) {
            Player p = (Player) sender;

            try {
                if (args != null) {
                    switch (args[0]) {
                        case "start":
                            if (isTimerRunning == true) {
                                p.sendMessage("timer is already running!");
                                break;
                            }
                            isTimerRunning = true;
                            switch (args[1]) {
                                case "up":
                                    time_second = 0;
                                    isTimerUpOrDown = 0;
                                    if(isTimerOn == false) {
                                    beginTimer();
                                    }
                                    isTimerOn = true;
                                    break;

                                case "down":
                                    try {
                                        if (!isNumeric(args[2]) || !isNumeric(args[3])) {
                                            isTimerRunning = false;
                                            p.sendMessage("an error");
                                            break;
                                        }
                                        timerMin = Integer.parseInt(args[2]);
                                        timerSec = Integer.parseInt(args[3]);
                                        if(timerMin >= 60 || timerSec >= 60) {
                                            isTimerRunning = false;
                                            p.sendMessage("an error");
                                            break;
                                        }
                                        isTimerUpOrDown = 1;
                                        time_second = timerMin * 60 + timerSec;
                                        if(isTimerOn == false) {
                                            beginTimer();
                                        }
                                        isTimerOn = true;
                                        break;
                                    }
                                    catch (IndexOutOfBoundsException e1) {
                                        isTimerRunning = false;
                                        p.sendMessage("an error");
                                        break;
                                    }

                                default:
                                    isTimerRunning = false;
                                    p.sendMessage("up or down");
                                    break;
                            }
                            break;

                        case "pause":
                            isTimerRunning = false;
                            break;

                        case "resume":
                            if (isTimerRunning == true) {
                                p.sendMessage("timer is already running!");
                                break;
                            }
                            isTimerRunning = true;
                            break;
                        case "stop":
                            isTimerRunning = false;
                            resetTime();
                            break;

                        case "member":
                            /*if(args[1] != null) {
                            p.sendMessage("you typed : " + args[1]);
                            }*/
                            break;

                        default:
                            p.sendMessage("you typed somthing wrong ㅠㅠ");
                            break;
                    }
                }
            } catch (IndexOutOfBoundsException e1) {
                p.sendMessage("you typed nothing");
            }
        }

        return false;
    }

    public void beginTimer() {
        //
        //  begin game timer
        //
        beginGameTimer();
        int timer = Bukkit.getScheduler().scheduleAsyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                if(isTimerRunning == true) {
                    if(isTimerUpOrDown == 0) {
                        for (Player players : Bukkit.getServer().getOnlinePlayers()) {
                            players.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.BOLD + "" + getTime(time_second)));
                        }
                        time_second++;
                    }
                    else if(isTimerUpOrDown == 1) {
                        if(time_second < 1) {
                            for (Player players : Bukkit.getServer().getOnlinePlayers()) {
                                players.sendTitle("Time is over!", "", 0, 60, 60);
                            }
                            isTimerRunning = false;
                        }
                        for (Player players : Bukkit.getServer().getOnlinePlayers()) {
                            players.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.BOLD + "" + getTime(time_second)));
                        }
                        time_second--;
                    }
                }
            }
        }, 1L , 20L);
    }

    //
    //  gamerule
    //
    public void beginGameTimer() {
        Bukkit.getScheduler().scheduleAsyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                if(isTimerUpOrDown == 0) {
                    if (isGameBegan()) {
                        border1.setCenter(0, 0);
                        border2.setCenter(0, 0);
                        border3.setCenter(0, 0);
                        border1.setSize(2000);
                        border2.setSize(2000);
                        border3.setSize(2000);
                        for (Player players : Bukkit.getServer().getOnlinePlayers()) {
                            players.teleport(new Location(w, 0, w.getHighestBlockYAt(0, 0), 0));
                        }
                    }
                    if (isTime(10, 0)) {
                        for (Player players : Bukkit.getServer().getOnlinePlayers()) {
                            players.sendTitle("", "warning", 20, 60, 60);
                            if (players.getWorld().getName().endsWith("_nether") || players.getWorld().getName().endsWith("_end")) {
                                players.teleport(new Location(w, 0, w.getHighestBlockYAt(0, 0), 0));
                                players.sendMessage("you are teleported because of different dimension");
                            }
                        }
                        border1.setSize(10, 240);
                    }
                    if (isTime(15, 0)) {
                        stopTimer();
                    }
                }
                else {
                    Bukkit.broadcastMessage("please restart the timer");
                    Bukkit.broadcastMessage("and use /game start up");
                    stopTimer();
                }
            }
        }, 0 , 1L);
    }

    //
    // useful methods
    //
    public String getTime(int time) {
        int min = time / 60;
        int sec = time % 60;

        return min + " : " + sec;
    }
    public boolean isTime(int min, int sec) {
        int times = min * 60 + sec + 1;
        if(time_second == times) {
            return true;
        }
        else {
            return false;
        }
    }
    public boolean isGameBegan() {
        if(isTime(0,0)) {
            return true;
        }
        else {
            return false;
        }
    }
    public void stopTimer() {
        isTimerRunning = false;
        resetTime();
    }

    public void resetTime() {
        time_second = 0;
    }

    public static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            double d = Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

}
