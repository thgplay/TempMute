package com.tke.mute.commands;

import com.tke.mute.api.Command;
import com.tke.mute.controller.MuteController;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Date;

public class MuteCommand extends Command {

    private MuteController controller;

    public MuteCommand() {
        super("mute");
        this.controller = MuteController.getInstance();
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!sender.hasPermission("mute.mute")) return true;
        if (args.length < 3){
            sender.sendMessage("§cUse: §f/mute §7<player> <time> <reason>");
            return true;
        }
        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        if (target == null){
            sender.sendMessage("§cPlayer is not found.");
            return true;
        }
        if (controller.hasMuted(target.getUniqueId())){
            sender.sendMessage("§cPlayer is already mutated.");
            return true;
        }
        long time = serializer(args[1]);
        if (time == -1){
            sender.sendMessage("§cFormat invalid!");
            return true;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 2; i < args.length; i++){
            sb.append(args[i]).append(" ");
        }

        this.controller.create(target.getUniqueId(), time, sender.getName(), sb.toString());
        sender.sendMessage("§ePlayer §f" + target.getName() + " §ehas been mutated.");
        return false;
    }

    @Deprecated
    private long serializer(String path){
        Date date = new Date();
        int time;
        try {
            if (path.contains("s")) {
                time = Integer.parseInt(path.replace("s", ""));
                date.setSeconds(date.getSeconds() + time);
            } else if (path.contains("m")) {
                time = Integer.parseInt(path.replace("m", ""));
                date.setMinutes(date.getMinutes() + time);
            } else if (path.contains("h")) {
                time = Integer.parseInt(path.replace("h", ""));
                date.setHours(date.getHours() + time);
            } else if (path.contains("d")) {
                time = Integer.parseInt(path.replace("d", ""));
                date.setDate(date.getDate() + time);
            } else date.setSeconds(date.getSeconds() + Integer.parseInt(path));
            return date.getTime();
        } catch (Exception ignored){
            return -1;
        }
    }

}
