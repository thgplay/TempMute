package com.tke.mute.api;

import com.tke.mute.TempMute;
import com.tke.mute.controller.MuteController;
import com.tke.mute.data.UserMute;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import java.sql.*;
import java.util.UUID;

public class Database implements AutoCloseable {

	@Getter
	private static Database instance;

	private static FileConfiguration cfg;



	
	static {
		cfg = TempMute.getInstance().getConfig();
		TempMute.getInstance().saveDefaultConfig();
	}
	
	private Connection connection;
	private String database;
	
	public Database() throws SQLException {
		instance = this;
		try {
			database = cfg.getString("database");
			String host = "jdbc:mysql://"+cfg.getString("hostname")+":"+cfg.getInt("port")+"/" + database + "?autoReconnect=true";
			connection = DriverManager.getConnection(host, cfg.getString("username"), cfg.getString("password"));
		} catch (Exception ex){
			TempMute.getInstance().getPluginLoader().disablePlugin(TempMute.getInstance());
			ex.printStackTrace();
			return;
		}
		Bukkit.getScheduler().runTaskTimerAsynchronously(TempMute.getInstance(), this::ping, 0, 1200);

		createTables();
		load();
	}

	public void createTables() throws SQLException {
		try (Statement s = createStatement()) {
			s.executeUpdate("CREATE TABLE IF NOT EXISTS `" + database + "`.`mute` "
					+ "(`id` BIGINT NOT NULL AUTO_INCREMENT,"
					+ "`uuid` VARCHAR(36) NOT NULL, "
					+ "`staffer` VARCHAR(16) NOT NULL,"
					+ "`reason` VARCHAR(64) NOT NULL,"
					+ "`expire` BIGINT NOT NULL,"
					+ "PRIMARY KEY(`id`)) Engine=InnoDB");
		}
	}
	
	private void ping() {
		try (Statement s = createStatement()) {
			ResultSet rs = s.executeQuery("/* ping */ SELECT 1");
			rs.close();
		} catch (Exception e) {}
	}
	
	public void create(UserMute userMute){
		String sql = "INSERT INTO `" + this.database + "`.`mute` (`uuid`, `staffer`, `reason`, `expire`) " +
				"VALUES (?,?,?,?)";
		Bukkit.getScheduler().runTaskAsynchronously(TempMute.getInstance(), () -> {
			try (PreparedStatement ps = prepareStatement(sql)){
				ps.setString(1, userMute.getUuid().toString());
				ps.setString(2, userMute.getStaffer());
				ps.setString(3, userMute.getReason());
				ps.setLong(4, userMute.getTime());

				ps.execute();
			}catch (Exception ex){
				ex.printStackTrace();
			}
		});
	}

	public void remove(UserMute userMute){
		String sql = "DELETE FROM `" + this.database + "`.`mute` WHERE `uuid` = ?";
		Bukkit.getScheduler().runTaskAsynchronously(TempMute.getInstance(), () -> {
			try (PreparedStatement ps = prepareStatement(sql)){
				ps.setString(1, userMute.getUuid().toString());
				ps.executeUpdate();
			}catch (Exception ex){
				ex.printStackTrace();
			}
		});
	}

	public void load(){
		String sql = "SELECT * FROM `" + this.database + "`.`mute`";
		Bukkit.getScheduler().runTaskAsynchronously(TempMute.getInstance(), () -> {
			try (PreparedStatement ps = prepareStatement(sql)){

				ResultSet rs = ps.executeQuery();

				while (rs.next()){
					UUID uuid = UUID.fromString(rs.getString("uuid"));
					String staffer = rs.getString("staffer");
					String reason = rs.getString("reason");
					long expire = rs.getLong("expire");

					UserMute userMute = new UserMute();
					userMute.setUuid(uuid);
					userMute.setStaffer(staffer);
					userMute.setReason(reason);
					userMute.setTime(expire);

					MuteController.getInstance().getDatas().add(userMute);
				}

			}catch (Exception ex){
				ex.printStackTrace();
			}
		});
	}


	
	public PreparedStatement prepareStatement(String sql) throws SQLException {
		return connection.prepareStatement(sql);
	}
	
	public Statement createStatement() throws SQLException {
		return connection.createStatement();
	}

	@Override
	public void close() {
		try {
			connection.close();
			connection = null;
		} catch (Exception e) {}
	}

}
