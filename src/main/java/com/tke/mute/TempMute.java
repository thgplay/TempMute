package com.tke.mute;

import com.tke.mute.api.Database;
import com.tke.mute.commands.MuteCommand;
import com.tke.mute.commands.UnMuteCommand;
import com.tke.mute.controller.MuteController;
import com.tke.mute.listener.MuteListener;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.util.stream.Stream;

public class TempMute extends JavaPlugin {

    @Getter
    private static TempMute instance;

    @Override
    public void onEnable() {
        instance = this;
        init();
    }

    private void init(){
        initControllers();
        initCommands();
        initListeners();
    }

    private void initControllers(){
        new MuteController();
        try {
            new Database();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private void initCommands(){
        new MuteCommand();
        new UnMuteCommand();
    }

    private void initListeners(){
        Stream.of(
                new MuteListener()
        ).forEach(s -> Bukkit.getPluginManager().registerEvents(s, this));
    }

    @Override
    public void onDisable() {
        Database.getInstance().close();
        HandlerList.unregisterAll(this);
    }
}
