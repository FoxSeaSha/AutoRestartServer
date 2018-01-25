package ru.foxseasha.trestart;

import java.util.Iterator;
import java.util.TimerTask;
import ru.foxseasha.trestart.AutoShutdownPlugin;
import ru.foxseasha.trestart.misc.Log;
import org.bukkit.Server;
import org.bukkit.World;

public class ShutdownTask extends TimerTask {

	   protected AutoShutdownPlugin plugin = null;
	   protected Log log = null;


	   ShutdownTask(AutoShutdownPlugin instance) {
	      this.plugin = instance;
	      this.log = this.plugin.log;
	   }

	   public void run() {
	      this.plugin.getServer().getScheduler().scheduleSyncDelayedTask(this.plugin, new Runnable() {
	         public void run() {
	            ShutdownTask.this.log.info("Shutdown in progress.");
	            ShutdownTask.this.plugin.kickAll();
	            ShutdownTask.this.plugin.getServer().savePlayers();
	            Server server = ShutdownTask.this.plugin.getServer();
	            server.savePlayers();
	            Iterator<World> var3 = server.getWorlds().iterator();

	            while(var3.hasNext()) {
	               World world = (World)var3.next();
	               world.save();
	               server.unloadWorld(world, true);
	            }

	            server.shutdown();
	         }
	      });
	   }
	}