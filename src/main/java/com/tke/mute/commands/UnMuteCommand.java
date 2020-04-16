package com.tke.mute.commands;

import com.tke.mute.api.Command;
import com.tke.mute.controller.MuteController;
import com.tke.mute.data.UserMute;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Date;

public class UnMuteCommand extends Command {

    private MuteController controller;

    public UnMuteCommand() {
        super("unmute");
        this.controller = MuteController.getInstance();
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!sender.hasPermission("mute.unmute")) return true;
        if (args.length == 0){
            sender.sendMessage("§cUse: §f/unmute §7<player>");
            return true;
        }
        UserMute userMute = controller.getUserMuted(args[0]);
        if (userMute == null){
            sender.sendMessage("§cThat player is not mutated.");
            return true;
        }
        controller.remove(userMute);
        sender.sendMessage("§aPlayer mute has been removed.");
        return false;
    }


}
