package com.ohmona.survival;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
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

    //
    // Main
    //
    @Override
    public void onEnable() {
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
    // useful methods
    //
    public String getTime(int time) {
        int min = time / 60;
        int sec = time % 60;

        return min + " : " + sec;
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
