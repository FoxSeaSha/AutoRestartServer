package ru.foxseasha.trestart;

import java.io.File;
import java.io.IOException;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;

public class SettingsManager {

	   Plugin p;
	   FileConfiguration config;
	   File cfile;
	   static SettingsManager instance = new SettingsManager();


	   public static SettingsManager getInstance() {
	      return instance;
	   }

	   public void setup(Plugin p) {
	      this.p = p;
	      this.config = p.getConfig();
	      p.saveDefaultConfig();
	      this.cfile = new File(p.getDataFolder(), "config.yml");
	   }

	   public FileConfiguration getConfig() {
	      return this.config;
	   }

	   public void saveConfig() {
	      try {
	         this.config.save(this.cfile);
	      } catch (IOException var2) {
	         Bukkit.getServer().getLogger().severe(ChatColor.RED + "Unable to save configuration.");
	      }

	   }

	   public void reloadConfig() {
	      if(this.cfile.exists()) {
	         this.config = YamlConfiguration.loadConfiguration(this.cfile);
	      } else {
	         this.p.saveDefaultConfig();
	      }

	   }

	   public PluginDescriptionFile getDesc() {
	      return this.p.getDescription();
	   }
	}
