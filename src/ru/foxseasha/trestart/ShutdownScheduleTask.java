package ru.foxseasha.trestart;

import java.util.Calendar;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;
import ru.foxseasha.trestart.AutoShutdownPlugin;
import ru.foxseasha.trestart.ShutdownTask;
import ru.foxseasha.trestart.WarnTask;
import ru.foxseasha.trestart.misc.Util;

public class ShutdownScheduleTask extends TimerTask {

	   protected AutoShutdownPlugin plugin = null;


	   ShutdownScheduleTask(AutoShutdownPlugin instance) {
	      this.plugin = instance;
	   }

	   public void run() {
	      this.plugin.getServer().getScheduler().scheduleSyncDelayedTask(this.plugin, new Runnable() {
	         public void run() {
	            ShutdownScheduleTask.this.runTask();
	         }
	      });
	   }

	   private void runTask() {
	      if(!this.plugin.shutdownImminent) {
	         Calendar now = Calendar.getInstance();
	         long firstWarning = (long)(((Integer)this.plugin.warnTimes.get(0)).intValue() * 1000);
	         Iterator var5 = this.plugin.shutdownTimes.iterator();

	         while(var5.hasNext()) {
	            Calendar cal = (Calendar)var5.next();
	            if(cal.getTimeInMillis() - now.getTimeInMillis() <= firstWarning) {
	               this.plugin.shutdownImminent = true;
	               this.plugin.shutdownTimer = new Timer();
	               Iterator var7 = this.plugin.warnTimes.iterator();

	               while(var7.hasNext()) {
	                  Integer warnTime = (Integer)var7.next();
	                  long longWarnTime = warnTime.longValue() * 1000L;
	                  if(longWarnTime <= cal.getTimeInMillis() - now.getTimeInMillis()) {
	                     this.plugin.shutdownTimer.schedule(new WarnTask(this.plugin, warnTime.longValue()), cal.getTimeInMillis() - now.getTimeInMillis() - longWarnTime);
	                  }
	               }

	               this.plugin.shutdownTimer.schedule(new ShutdownTask(this.plugin), cal.getTime());
	               Util.broadcast(this.plugin.settings.config.getString("messages.shutdownmessage") + " в %s", new Object[]{cal.getTime().toString()});
	               break;
	            }
	         }

	      }
	   }
	}
