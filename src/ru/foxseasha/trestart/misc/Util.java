package ru.foxseasha.trestart.misc;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import ru.foxseasha.trestart.AutoShutdownPlugin;
import ru.foxseasha.trestart.misc.Log;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

public class Util {

    public static String pluginName;
    public static Log log;
    public static AutoShutdownPlugin plugin;
    
    static {
        Util.log = null;
        Util.plugin = null;
    }

	    public static void init(final Plugin plugin, final Log log) {
	        Util.plugin = (AutoShutdownPlugin)plugin;
	        Util.pluginName = plugin.getDescription().getName();
	        Util.log = log;
	    }
	    
	    public static String parseColor(final String s) {
	        return parseColor("%s", new Object[] { s });
	    }

	    public static String parseColor(final String format, final Object[] args) {
	        final String pre = String.format(format, args);
	        final StringBuffer post = new StringBuffer();
	        final Pattern p = Pattern.compile("\\&([0-9a-fA-F&])");
	        final Matcher m = p.matcher(pre);
	        while (m.find()) {
	            if (m.group(1).equalsIgnoreCase("&")) {
	                m.appendReplacement(post, "&");
	            }
	            else {
	                final int colorCode = Integer.parseInt(m.group(1), 16);
	                String color = null;
	                switch (colorCode) {
	                    case 0: {
	                        color = ChatColor.BLACK.toString();
	                        break;
	                    }
	                    case 1: {
	                        color = ChatColor.DARK_BLUE.toString();
	                        break;
	                    }
	                    case 2: {
	                        color = ChatColor.DARK_GREEN.toString();
	                        break;
	                    }
	                    case 3: {
	                        color = ChatColor.DARK_AQUA.toString();
	                        break;
	                    }
	                    case 4: {
	                        color = ChatColor.DARK_RED.toString();
	                        break;
	                    }
	                    case 5: {
	                        color = ChatColor.DARK_PURPLE.toString();
	                        break;
	                    }
	                    case 6: {
	                        color = ChatColor.GOLD.toString();
	                        break;
	                    }
	                    case 7: {
	                        color = ChatColor.GRAY.toString();
	                        break;
	                    }
	                    case 8: {
	                        color = ChatColor.DARK_GRAY.toString();
	                        break;
	                    }
	                    case 9: {
	                        color = ChatColor.BLUE.toString();
	                        break;
	                    }
	                    case 10: {
	                        color = ChatColor.GREEN.toString();
	                        break;
	                    }
	                    case 11: {
	                        color = ChatColor.AQUA.toString();
	                        break;
	                    }
	                    case 12: {
	                        color = ChatColor.RED.toString();
	                        break;
	                    }
	                    case 13: {
	                        color = ChatColor.LIGHT_PURPLE.toString();
	                        break;
	                    }
	                    case 14: {
	                        color = ChatColor.YELLOW.toString();
	                        break;
	                    }
	                    case 15: {
	                        color = ChatColor.WHITE.toString();
	                        break;
	                    }
	                    default: {
	                        color = ChatColor.WHITE.toString();
	                        break;
	                    }
	                }
	                m.appendReplacement(post, color);
	            }
	        }
	        m.appendTail(post);
	        return post.toString();
	    }

	    public static void replyError(final CommandSender sender, final String s) {
	        replyError(sender, "%s", new Object[] { s });
	    }

	    public static void replyError(final CommandSender sender, final String format, final Object[] args) {
	        final String msg = String.format(format, args);
	        final String formattedMessage = parseColor("&2[&a%s&2] &c%s", new Object[] { Util.pluginName, msg });
	        if (sender == null) {
	            Util.log.info(formattedMessage);
	        }
	        else {
	            sender.sendMessage(formattedMessage);
	        }
	    }
	    
	    public static void reply(final CommandSender sender, final String s) {
	        reply(sender, "%s", new Object[] { s });
	    }

	    public static void reply(final CommandSender sender, final String format, final Object[] args) {
	        final String msg = String.format(format, args);
	        final String formattedMessage = parseColor("&2[&a%s&2] &f%s", new Object[] { Util.pluginName, msg });
	        if (sender == null) {
	            Util.log.info(formattedMessage);
	        }
	        else {
	            sender.sendMessage(formattedMessage);
	        }
	    }

	   public static String getJoinedStrings(String[] args, int initialIndex) {
	      StringBuilder buffer = new StringBuilder();

	      for(int i = initialIndex; i < args.length; ++i) {
	         if(i != initialIndex) {
	            buffer.append(" ");
	         }

	         buffer.append(args[i]);
	      }

	      return buffer.toString();
	   }

	   public static void broadcast(String s) {
	      broadcast("%s", new Object[]{s});
	   }

	   public static void broadcast(String format, Object[] args) {
	      String msg = String.format(format, args);
	      String formattedMessage;
	      if(plugin.getSettings().getConfig().getBoolean("messages.showtag", true)) {
	         formattedMessage = parseColor("&2[&a%s&2] &f%s", new Object[]{pluginName, msg});
	      } else {
	         formattedMessage = parseColor("&2[&a%s&2] &f%s", new Object[]{"TTSRESTART", msg});
	      }

	      plugin.getServer().broadcastMessage(formattedMessage);
	   }
	}