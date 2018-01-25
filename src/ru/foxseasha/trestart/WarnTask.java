package ru.foxseasha.trestart;

import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import ru.foxseasha.trestart.AutoShutdownPlugin;
import ru.foxseasha.trestart.misc.Log;
import ru.foxseasha.trestart.misc.Util;

public class WarnTask extends TimerTask {

	   protected final AutoShutdownPlugin plugin;
	   protected final Log log;
	   protected long seconds = 0L;


	   public WarnTask(AutoShutdownPlugin plugin, long seconds) {
	      this.plugin = plugin;
	      this.log = plugin.log;
	      this.seconds = seconds;
	   }

	   public void run() {
	      this.plugin.getServer().getScheduler().scheduleSyncDelayedTask(this.plugin, new Runnable() {
	         public void run() {
	            if(TimeUnit.SECONDS.toMinutes(WarnTask.this.seconds) > 0L) {
	               if(TimeUnit.SECONDS.toMinutes(WarnTask.this.seconds) == 1L) {
	                  if(WarnTask.this.seconds - TimeUnit.MINUTES.toSeconds(TimeUnit.SECONDS.toMinutes(WarnTask.this.seconds)) == 0L) {
	                     Util.broadcast(WarnTask.this.plugin.settings.config.getString("messages.shutdownmessage") + " через 1 " + WarnTask.this.plugin.settings.config.getString("messages.minute") + "...");
	                  } else {
	                     Util.broadcast(WarnTask.this.plugin.settings.config.getString("messages.shutdownmessage") + " через 1 " + WarnTask.this.plugin.settings.config.getString("messages.minute") + " %d " + WarnTask.this.plugin.settings.config.getString("messages.second") + " ...", new Object[]{Long.valueOf(WarnTask.this.seconds - TimeUnit.MINUTES.toSeconds(TimeUnit.SECONDS.toMinutes(WarnTask.this.seconds)))});
	                  }
	               } else if(WarnTask.this.seconds - TimeUnit.MINUTES.toSeconds(TimeUnit.SECONDS.toMinutes(WarnTask.this.seconds)) == 0L) {
	                  Util.broadcast(WarnTask.this.plugin.settings.config.getString("messages.shutdownmessage") + " через %d " + WarnTask.this.plugin.settings.config.getString("messages.minute") + " ...", new Object[]{Long.valueOf(TimeUnit.SECONDS.toMinutes(WarnTask.this.seconds))});
	               } else {
	                  Util.broadcast(WarnTask.this.plugin.settings.config.getString("messages.shutdownmessage") + " через %d " + WarnTask.this.plugin.settings.config.getString("messages.minute") + " %d " + WarnTask.this.plugin.settings.config.getString("messages.second") + " ...", new Object[]{Long.valueOf(TimeUnit.SECONDS.toMinutes(WarnTask.this.seconds)), Long.valueOf(WarnTask.this.seconds - TimeUnit.MINUTES.toSeconds(TimeUnit.SECONDS.toMinutes(WarnTask.this.seconds)))});
	               }
	            } else if(TimeUnit.SECONDS.toSeconds(WarnTask.this.seconds) == 1L) {
	               Util.broadcast(WarnTask.this.plugin.settings.config.getString("messages.shutdownmessage") + " Сейчас!");
	            } else {
	               Util.broadcast(WarnTask.this.plugin.settings.config.getString("messages.shutdownmessage") + " через %d " + WarnTask.this.plugin.settings.config.getString("messages.second") + " ...", new Object[]{Long.valueOf(WarnTask.this.seconds)});
	            }

	         }
	      });
	   }
	}