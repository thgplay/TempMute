package com.tke.mute.listener;

import com.tke.mute.controller.MuteController;
import com.tke.mute.data.UserMute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class MuteListener implements Listener {

    private MuteController controller;

    public MuteListener(){
        this.controller = MuteController.getInstance();
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e){
        Player p = e.getPlayer();
        UserMute userMute = this.controller.getUserMuted(p.getUniqueId());
        if (userMute == null) return;
        if (!this.controller.isAfter(userMute.getTime())){
            e.setCancelled(true);
            p.sendMessage(new String[] {
                    "§cYou have been mutated!",
                    "§cStaffer: §f" + userMute.getStaffer(),
                    "§cReason: §f" + userMute.getReason(),
                    "§cTime remaining: §f" + this.controller.getTime(userMute.getTime())
            });
            return;
        } else
            this.controller.remove(userMute);
    }

}
