package com.tke.mute.controller;

import com.google.common.collect.Sets;
import com.tke.mute.api.Database;
import com.tke.mute.data.UserMute;
import lombok.Getter;
import org.bukkit.Bukkit;

import java.util.Date;
import java.util.LinkedHashSet;
import java.util.UUID;

public class MuteController {

    @Getter
    private static MuteController instance;

    public MuteController(){
        instance = this;
        this.datas = Sets.newLinkedHashSet();
    }

    @Getter
    private LinkedHashSet<UserMute> datas;

    public boolean hasMuted(UUID uuid){
        return this.datas.stream().anyMatch(s -> s.getUuid().equals(uuid));
    }

    public UserMute getUserMuted(UUID uuid){
        return this.datas.stream().filter(s -> s.getUuid().equals(uuid)).findFirst().orElse(null);
    }

    public UserMute getUserMuted(String playerName){
        return this.datas.stream().filter(s -> Bukkit.getOfflinePlayer(s.getUuid()).getName().equals(playerName)).findFirst().orElse(null);
    }

    public boolean isAfter(long expire){
        return new Date().after(new Date(expire));
    }

    public void remove(UserMute userMute){
        this.datas.remove(userMute);
        Database.getInstance().remove(userMute);
    }

    public void create(UUID uuid, long time, String staffer, String reason){
        UserMute userMute = new UserMute();
        userMute.setUuid(uuid);
        userMute.setTime(time);
        userMute.setStaffer(staffer);
        userMute.setReason(reason);
        this.datas.add(userMute);
        Database.getInstance().create(userMute);
    }

    public String getTime(long expire){
        long time,now,seconds,minutes,hours,days;
        now = new Date().getTime();
        time = (expire - now);
        seconds = (time / 1000 % 60);
        minutes = (time / 1000 / 60 % 60);
        hours = (time / 1000 / 60 / 60 % 24);
        days = (time / 1000 / 60 / 60 / 24);
        return days + "d " + hours + "h " + minutes + "m " + seconds + "s ";
    }


}
