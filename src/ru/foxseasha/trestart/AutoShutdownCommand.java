package ru.foxseasha.trestart;

import java.util.Calendar;
import java.util.Iterator;
import ru.foxseasha.trestart.AutoShutdownPlugin;
import ru.foxseasha.trestart.misc.Log;
import ru.foxseasha.trestart.misc.Util;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AutoShutdownCommand implements CommandExecutor {

	   private final AutoShutdownPlugin plugin;
	   private Log log;


	   public AutoShutdownCommand(AutoShutdownPlugin plugin) {
	      this.plugin = plugin;
	      this.log = plugin.log;
	   }

	   public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
	      if(sender instanceof Player && !((Player)sender).hasPermission("autoshutdown.admin")) {
	         Util.replyError(sender, "У вас нету прав.");
	         return true;
	      } else {
	         if(args.length == 0) {
	            args = new String[]{"HELP"};
	         }

	         Calendar shutdownTime;
	         Iterator var8;
	         switch(AutoShutdownCommand.SubCommand.toSubCommand(args[0].toUpperCase()).ordinal()) {
	         case 0:
	            Util.reply(sender, "AutoShutdown plugin help:");
	            Util.reply(sender, " /%s help", new Object[]{command.getName()});
	            Util.reply(sender, "     Shows this help page");
	            Util.reply(sender, " /%s reload", new Object[]{command.getName()});
	            Util.reply(sender, "     Reloads the configuration file");
	            Util.reply(sender, " /%s cancel", new Object[]{command.getName()});
	            Util.reply(sender, "     Cancels the currently executing shutdown");
	            Util.reply(sender, " /%s set HH:MM:SS", new Object[]{command.getName()});
	            Util.reply(sender, "     Sets a new scheduled shutdown time");
	            Util.reply(sender, " /%s set now", new Object[]{command.getName()});
	            Util.reply(sender, "     Orders the server to shutdown immediately");
	            Util.reply(sender, " /%s list", new Object[]{command.getName()});
	            Util.reply(sender, "     lists the currently scheduled shutdowns");
	            break;
	         case 1:
	            Util.reply(sender, "Reloading...");
	            this.plugin.settings.reloadConfig();
	            this.plugin.scheduleAll();
	            Util.reply(sender, "Configuration reloaded.");
	            break;
	         case 2:
	            if(this.plugin.shutdownTimer != null) {
	               this.plugin.shutdownTimer.cancel();
	               this.plugin.shutdownTimer.purge();
	               this.plugin.shutdownTimer = null;
	               this.plugin.shutdownImminent = false;
	               Util.broadcast("Shutdown was aborted.");
	            } else {
	               Util.replyError(sender, "There is no impending shutdown. If you wish to remove");
	               Util.replyError(sender, "a scheduled shutdown, remove it from the configuration");
	               Util.replyError(sender, "and reload.");
	            }
	            break;
	         case 3:
	            if(args.length < 2) {
	               Util.replyError(sender, "Usage:");
	               Util.replyError(sender, "   /as set <time>");
	               Util.replyError(sender, "<time> can be either \'now\' or a 24h time in HH:MM format.");
	               return true;
	            }

	            Calendar stopTime = null;

	            try {
	               stopTime = this.plugin.scheduleShutdownTime(args[1]);
	            } catch (Exception var9) {
	               Util.replyError(sender, "Usage:");
	               Util.replyError(sender, "   /as set <time>");
	               Util.replyError(sender, "<time> can be either \'now\' or a 24h time in HH:MM format.");
	            }

	            if(stopTime != null) {
	               Util.reply(sender, "Shutdown scheduled for %s", new Object[]{stopTime.getTime().toString()});
	            }

	            String timeString = "";
	            var8 = this.plugin.shutdownTimes.iterator();

	            while(var8.hasNext()) {
	               shutdownTime = (Calendar)var8.next();
	               if(((Calendar)this.plugin.shutdownTimes.first()).equals(shutdownTime)) {
	                  timeString = timeString.concat(String.format("%d:%02d", new Object[]{Integer.valueOf(shutdownTime.get(11)), Integer.valueOf(shutdownTime.get(12))}));
	               } else {
	                  timeString = timeString.concat(String.format(",%d:%02d", new Object[]{Integer.valueOf(shutdownTime.get(11)), Integer.valueOf(shutdownTime.get(12))}));
	               }
	            }

	            this.plugin.settings.getConfig().set("times.shutdowntimes", timeString);
	            break;
	         case 4:
	            if(this.plugin.shutdownTimes.size() != 0) {
	               Util.reply(sender, "Shutdowns scheduled at");
	               var8 = this.plugin.shutdownTimes.iterator();

	               while(var8.hasNext()) {
	                  shutdownTime = (Calendar)var8.next();
	                  Util.reply(sender, "   %s", new Object[]{shutdownTime.getTime().toString()});
	               }

	               return true;
	            } else {
	               Util.replyError(sender, "No shutdowns scheduled.");
	               break;
	            }
	         case 5:
	            Util.replyError(sender, "Unknown command. Use /as help to list available commands.");
	         }

	         return true;
	      }
	   }

	   static enum SubCommand {

	      HELP("HELP", 0),
	      RELOAD("RELOAD", 1),
	      CANCEL("CANCEL", 2),
	      SET("SET", 3),
	      LIST("LIST", 4),
	      UNKNOWN("UNKNOWN", 5);
	      private static final AutoShutdownCommand.SubCommand[] ENUM$VALUES = new AutoShutdownCommand.SubCommand[]{HELP, RELOAD, CANCEL, SET, LIST, UNKNOWN};


	      private SubCommand(String var1, int var2) {}

	      private static AutoShutdownCommand.SubCommand toSubCommand(String str) {
	         try {
	            return valueOf(str);
	         } catch (Exception var2) {
	            return HELP;
	         }
	      }
	   }
	}
