package ru.foxseasha.trestart;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Timer;
import java.util.TreeSet;
import ru.foxseasha.trestart.AutoShutdownCommand;
import ru.foxseasha.trestart.SettingsManager;
import ru.foxseasha.trestart.ShutdownScheduleTask;
import ru.foxseasha.trestart.ShutdownTask;
import ru.foxseasha.trestart.WarnTask;
import ru.foxseasha.trestart.misc.Log;
import ru.foxseasha.trestart.misc.Util;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

public class AutoShutdownPlugin extends JavaPlugin {

    public String pluginName;
    public Log log;
    protected ShutdownScheduleTask task;
    protected Timer backgroundTimer;
    protected Timer shutdownTimer;
    protected BukkitScheduler scheduler;
    protected boolean shutdownImminent;
    protected TreeSet<Calendar> shutdownTimes;
    protected ArrayList<Integer> warnTimes;
    SettingsManager settings;
    
    public AutoShutdownPlugin() {
        this.task = null;
        this.backgroundTimer = null;
        this.shutdownTimer = null;
        this.scheduler = null;
        this.shutdownImminent = false;
        this.shutdownTimes = new TreeSet<Calendar>();
        this.warnTimes = new ArrayList<Integer>();
        this.settings = SettingsManager.getInstance();
    }


	   public void onDisable() {
	      this.shutdownImminent = false;
	      if(this.backgroundTimer != null) {
	         this.backgroundTimer.cancel();
	         this.backgroundTimer.purge();
	         this.backgroundTimer = null;
	      }

	      if(this.shutdownTimer != null) {
	         this.shutdownTimer.cancel();
	         this.shutdownTimer.purge();
	         this.shutdownTimer = null;
	      }

	      this.log.info("%s disabled.", new Object[]{this.settings.getDesc().getFullName()});
	   }

	   public void onEnable() {
	      this.pluginName = this.getDescription().getName();
	      this.log = new Log(this.pluginName);
	      this.settings.setup(this);
	      this.scheduler = this.getServer().getScheduler();
	      this.shutdownImminent = false;
	      this.shutdownTimes.clear();
	      AutoShutdownCommand autoShutdownCommandExecutor = new AutoShutdownCommand(this);
	      this.getCommand("autoshutdown").setExecutor(autoShutdownCommandExecutor);
	      this.getCommand("as").setExecutor(autoShutdownCommandExecutor);
	      this.scheduleAll();
	      Util.init(this, this.log);
	      if(this.backgroundTimer != null) {
	         this.backgroundTimer.cancel();
	         this.backgroundTimer.purge();
	         this.backgroundTimer = null;
	      }

	      this.backgroundTimer = new Timer();
	      if(this.shutdownTimer != null) {
	         this.shutdownTimer.cancel();
	         this.shutdownTimer.purge();
	         this.shutdownTimer = null;
	      }

	      Calendar now = Calendar.getInstance();
	      now.set(13, 0);
	      now.add(12, 1);
	      now.add(14, 50);

	      try {
	         this.backgroundTimer.scheduleAtFixedRate(new ShutdownScheduleTask(this), now.getTime(), 60000L);
	      } catch (Exception var4) {
	         this.log.severe("Failed to schedule AutoShutdownTask: %s", new Object[]{var4.getMessage()});
	      }

	      this.log.info(this.pluginName + " включен!");
	   }

	   protected void scheduleAll() {
	      this.shutdownTimes.clear();
	      this.warnTimes.clear();
	      String[] shutdownTimeStrings = null;

	      try {
	         shutdownTimeStrings = this.settings.getConfig().getString("times.shutdowntimes").split(",");
	      } catch (Exception var7) {
	         shutdownTimeStrings[0] = this.settings.getConfig().getString("times.shutdowntimes");
	      }

	      try {
	         String[] var5 = shutdownTimeStrings;
	         int var4 = shutdownTimeStrings.length;

	         for(int warnTime = 0; warnTime < var4; ++warnTime) {
	            String e = var5[warnTime];
	            this.scheduleShutdownTime(e);
	         }

	         String[] var9 = this.getConfig().getString("times.warntimes").split(",");
	         String[] var6 = var9;
	         int var11 = var9.length;

	         for(var4 = 0; var4 < var11; ++var4) {
	            String var10 = var6[var4];
	            this.warnTimes.add(Integer.decode(var10));
	         }
	      } catch (Exception var8) {
	         this.log.severe("Unable to configure Auto Shutdown using the configuration file.");
	         this.log.severe("Is the format of shutdowntimes correct? It should be only HH:MM.");
	         this.log.severe("Error: %s", new Object[]{var8.getMessage()});
	      }

	   }

	   protected Calendar scheduleShutdownTime(String timeSpec) throws Exception {
	      if(timeSpec == null) {
	         return null;
	      } else {
	         Calendar now;
	         if(timeSpec.matches("^now$")) {
	            now = Calendar.getInstance();
	            int shutdownTime1 = this.getConfig().getInt("times.gracetime", 20);
	            now.add(13, shutdownTime1);
	            this.shutdownImminent = true;
	            this.shutdownTimer = new Timer();
	            Iterator var5 = this.warnTimes.iterator();

	            while(var5.hasNext()) {
	               Integer timecomponent1 = (Integer)var5.next();
	               long longWarnTime = timecomponent1.longValue() * 1000L;
	               if(longWarnTime <= (long)(shutdownTime1 * 1000)) {
	                  this.shutdownTimer.schedule(new WarnTask(this, timecomponent1.longValue()), (long)(shutdownTime1 * 1000) - longWarnTime);
	               }
	            }

	            this.shutdownTimer.schedule(new ShutdownTask(this), now.getTime());
	            Util.broadcast("The server has been scheduled for immediate shutdown.");
	            return now;
	         } else if(!timeSpec.matches("^[0-9]{1,2}:[0-9]{2}$")) {
	            throw new Exception("Incorrect time specification. The format is HH:MM in 24h time.");
	         } else {
	            now = Calendar.getInstance();
	            Calendar shutdownTime = Calendar.getInstance();
	            String[] timecomponent = timeSpec.split(":");
	            shutdownTime.set(11, Integer.valueOf(timecomponent[0]).intValue());
	            shutdownTime.set(12, Integer.valueOf(timecomponent[1]).intValue());
	            shutdownTime.set(13, 0);
	            shutdownTime.set(14, 0);
	            if(now.compareTo(shutdownTime) >= 0) {
	               shutdownTime.add(5, 1);
	            }

	            this.shutdownTimes.add(shutdownTime);
	            return shutdownTime;
	         }
	      }
	   }

	   public void kickAll() {
	      if(this.getConfig().getBoolean("kickonshutdown", true)) {
	         this.log.info("Kicking all players ...");
	         this.log.info(this.settings.getConfig().getString("messages.kickreason"));
	         Player[] players = this.getServer().getOnlinePlayers();
	         Player[] var5 = players;
	         int var4 = players.length;

	         for(int var3 = 0; var3 < var4; ++var3) {
	            Player player = var5[var3];
	            this.log.info("Kicking player %s.", new Object[]{player.getName()});
	            player.kickPlayer(this.settings.config.getString("messages.kickreason"));
	         }

	      }
	   }

	   public SettingsManager getSettings() {
	      return this.settings;
	   }
	}
