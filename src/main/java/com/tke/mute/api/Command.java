package com.tke.mute.api;

import com.tke.mute.TempMute;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Command extends org.bukkit.command.Command {

    public Command(String name){
        super(name);
        register();
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        try{
            if(testPermission(sender)){
                boolean result = onCommand(sender, label, args);

                if(!result){
                    sender.sendMessage(ChatColor.RED + "/" + label + " " + getUsage() + " §e- §7" + getDescription());
                }
                return true;
            }
        } catch(Exception ex){
            ex.printStackTrace();
        }
        return false;
    }

    public boolean onCommand(CommandSender sender, String label, String[] args) {
        if(sender instanceof Player){
            return onCommand((Player) sender, label, args);
        }
        return false;
    }

    public boolean onCommand(Player player, String label, String[] args){
        return false;
    }

    public void sendPermissionMessage(CommandSender sender){
        sender.sendMessage(ChatColor.DARK_RED + getPermissionMessage());
    }

    private void register(){
        try {
            Object craftServer = getOBClass("CraftServer").cast(Bukkit.getServer());
            Object commandMap = craftServer.getClass().getMethod("getCommandMap").invoke(craftServer);

            commandMap.getClass().getMethod("register", String.class, org.bukkit.command.Command.class).invoke(commandMap, TempMute.getInstance().getDescription().getName(), this);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private Class<?> getOBClass(String name) throws ClassNotFoundException {
        String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        return Class.forName("org.bukkit.craftbukkit." + version + "." + name);
    }

    public static class CommandExeception extends Exception {
        public CommandExeception(String message){
            super(message);
        }

    }

}