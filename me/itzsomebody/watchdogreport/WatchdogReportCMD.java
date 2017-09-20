package me.itzsomebody.watchdogreport;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import me.konsolas.aac.api.AACAPIProvider;
import me.konsolas.aac.api.HackType;

public class WatchdogReportCMD implements CommandExecutor {
	
	WatchdogReport plugin;
	
	public WatchdogReportCMD(WatchdogReport plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("watchdogreport")) {
			if (args != null) {
				if (args.length == 2) {
					if (getProperNaming(args[1]) == "INVALID") {
						sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getLangConfig().getString("prefix")) + ChatColor.translateAlternateColorCodes('&', plugin.getLangConfig().getString("unknownHacks")));
						return false;
					} else {
						vlChecker(sender, args[0], getAACNaming(getProperNaming(args[1])), getProperNaming(args[1]), getVLsToBanOn(getProperNaming(args[1])));
						return true;
					}
				} else if (args.length == 3) {
					if (getProperNaming(args[1]) == "INVALID" || getProperNaming(args[2]) == "INVALID") {
						sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getLangConfig().getString("prefix")) + ChatColor.translateAlternateColorCodes('&', plugin.getLangConfig().getString("unknownHacks")));
						return false;
					} else if (getProperNaming(args[1]) == getProperNaming(args[2])) {
						vlChecker(sender, args[0], getAACNaming(getProperNaming(args[1])), getProperNaming(args[1]), getVLsToBanOn(getProperNaming(args[1])));
						return true;
					} else {
						vlCheckerTwoArgs(sender, args[0], getProperNaming(args[1]), getProperNaming(args[2]), getAACNaming(getProperNaming(args[1])), getAACNaming(getProperNaming(args[2])), getVLsToBanOn(getProperNaming(args[1])), getVLsToBanOn(getProperNaming(args[2])));
						return true;
					}
				} else if (args.length == 4) {
					if (getProperNaming(args[1]) == "INVALID" || getProperNaming(args[2]) == "INVALID") {
						sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getLangConfig().getString("prefix")) + ChatColor.translateAlternateColorCodes('&', plugin.getLangConfig().getString("unknownHacks")));
						return false;
					} else if (getProperNaming(args[1]) == getProperNaming(args[2]) && getProperNaming(args[2]) == getProperNaming(args[3])) {
						vlChecker(sender, args[0], getAACNaming(getProperNaming(args[1])), getProperNaming(args[1]), getVLsToBanOn(getProperNaming(args[1])));
						return true;
					} else if (getProperNaming(args[1]) == getProperNaming(args[2]) && getProperNaming(args[2]) != getProperNaming(args[3])) {
						vlCheckerTwoArgs(sender, args[0], getProperNaming(args[1]), getProperNaming(args[3]), getAACNaming(getProperNaming(args[1])), getAACNaming(getProperNaming(args[3])), getVLsToBanOn(getProperNaming(args[1])), getVLsToBanOn(getProperNaming(args[3])));
						return true;
					} else if (getProperNaming(args[1]) == getProperNaming(args[3]) && getProperNaming(args[2]) != getProperNaming(args[3])) {
						vlCheckerTwoArgs(sender, args[0], getProperNaming(args[1]), getProperNaming(args[2]), getAACNaming(getProperNaming(args[1])), getAACNaming(getProperNaming(args[2])), getVLsToBanOn(getProperNaming(args[1])), getVLsToBanOn(getProperNaming(args[2])));
						return true;
					} else {
						vlCheckerThreeArgs(sender, args[0], getProperNaming(args[1]), getProperNaming(args[2]), getProperNaming(args[3]), getAACNaming(getProperNaming(args[1])), getAACNaming(getProperNaming(args[2])), getAACNaming(getProperNaming(args[3])), getVLsToBanOn(getProperNaming(args[1])), getVLsToBanOn(getProperNaming(args[2])), getVLsToBanOn(getProperNaming(args[3])));
						return true;
					}
				} else {
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getLangConfig().getString("correctUsage")));
					return false;
			    }
		    } else {
				sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getLangConfig().getString("correctUsage")));
				return false;
		    }
		}
		return false;
	} 

	
	public void vlChecker(CommandSender sender, String reportedPlayer, HackType theHack, String reportedHack, int violations) {
		
	    // Player we will check. Make final because schedulers.
        final Player toCheck = Bukkit.getPlayer(reportedPlayer);
	    
        // The config values
        
        // Check if they are online
        if (toCheck == null) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getLangConfig().getString("prefix")) + ChatColor.translateAlternateColorCodes('&', plugin.getLangConfig().getString("playerNotFound").replace("%player%", reportedPlayer)));
            return;
        }
        
        // Check if someone is trying to report themself
        if (toCheck == sender) {
        	sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getLangConfig().getString("prefix")) + ChatColor.translateAlternateColorCodes('&', plugin.getLangConfig().getString("preventSelfReporting")));
        	return;
        }
        
        // Check if player has already been reported by this person
        if (plugin.checkPlayer(sender.getName(), toCheck.getName()) == true) {
        	sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getLangConfig().getString("prefix")) + ChatColor.translateAlternateColorCodes('&', plugin.getLangConfig().getString("alreadyReported").replace("%player%", toCheck.getName())));
        	return;
        } else {
        	plugin.addPlayer(sender.getName(), toCheck.getName());
        }
       
        // Announcing that we are indeed going to start watching a player
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getLangConfig().getString("prefix")) + ChatColor.translateAlternateColorCodes('&', plugin.getLangConfig().getString("successfulReportOneHack").replace("%player%", toCheck.getName()).replace("%hack%", reportedHack)));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getLangConfig().getString("prefix")) + ChatColor.translateAlternateColorCodes('&', plugin.getLangConfig().getString("thankPlayerForReport")));
        
        // Tell da admins
        Bukkit.broadcast(ChatColor.translateAlternateColorCodes('&', plugin.getLangConfig().getString("prefix")) + ChatColor.translateAlternateColorCodes('&', plugin.getLangConfig().getString("broadcaseOneHack").replace("%player%", toCheck.getName()).replace("%hack%", reportedHack).replace("%reporter%", sender.getName())), "AAC.notify");
        
        // Their current VL when command was ran. Also final
		final int currentVL = AACAPIProvider.getAPI().getViolationLevel(Bukkit.getPlayer(reportedPlayer), theHack);
        
        new BukkitRunnable() {
        	int counter = 0;
            public void run() {

            	if (Bukkit.getPlayer(reportedPlayer) != null) {
                    // Fetch the newVL since currentVL
            		int newVL = AACAPIProvider.getAPI().getViolationLevel(Bukkit.getPlayer(reportedPlayer), theHack);
            
            
            		// If their VL increased by this much, execute the command below
            		if ((newVL - currentVL) >= violations) {
            			if (AACAPIProvider.getAPI().getPing(toCheck) >= plugin.getConfig().getInt("maximumPing") || AACAPIProvider.getAPI().getTPS() >= plugin.getConfig().getInt("minimumTPS")) {
            				final String ripHacker = toCheck.getName();
            				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "kick " + ripHacker + " §fInternal exception: io.netty.handler.timeout.readTimeoutexception");
            				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("banCommand").replace("%player%", ripHacker)));
            				sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getLangConfig().getString("prefix")) + ChatColor.translateAlternateColorCodes('&', plugin.getLangConfig().getString("hackerGotCaught").replace("%player%", ripHacker)));
            				Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', plugin.getLangConfig().getString("thanksForReporting")));
                	
            				// Tell staff what they got banned for
            				Bukkit.broadcast(ChatColor.translateAlternateColorCodes('&', plugin.getLangConfig().getString("prefix")) + ChatColor.translateAlternateColorCodes('&', plugin.getLangConfig().getString("broadcastOneHackBanned").replace("%player%", ripHacker).replace("%hack%", reportedHack)), "AAC.notify");
            				
            				// Remove player from reported list
            				plugin.removePlayer(sender.getName(), ripHacker);
            				
            				cancel();
                	}
                	
            		} else if (counter >= plugin.getConfig().getInt("checkPlayer") ) {
            			cancel();
                		yourReportGotNinjad(sender, toCheck.getName());
        				plugin.removePlayer(sender.getName(), toCheck.getName());
            		}
            		counter++;
            	} else {
            		sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getLangConfig().getString("prefix")) + ChatColor.translateAlternateColorCodes('&', plugin.getLangConfig().getString("playerLoggedOut1").replace("%player%", reportedPlayer)));
                	sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getLangConfig().getString("playerLoggedOut2")));
    				plugin.removePlayer(sender.getName(), toCheck.getName());
                    cancel();
            	}
            }
        }.runTaskTimer(plugin, plugin.getConfig().getInt("tickDelay"), plugin.getConfig().getInt("tickDelay"));
	}
	
	public void vlCheckerTwoArgs(CommandSender sender, String reportedPlayer, String hackReported1, String hackReported2, HackType theHack1, HackType theHack2, int violationsForHack1, int violationsForHack2) {
		
		// The player who is reported
		final Player toCheck = Bukkit.getPlayer(reportedPlayer);
		
		if (toCheck == null) {
	           sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getLangConfig().getString("prefix")) + ChatColor.translateAlternateColorCodes('&', plugin.getLangConfig().getString("playerNotFound").replace("%player%", reportedPlayer)));
	           return;
	    }
	        
	    // Check if someone is trying to report themself
	    if (toCheck == sender) {
	    	sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getLangConfig().getString("prefix")) + ChatColor.translateAlternateColorCodes('&', plugin.getLangConfig().getString("preventSelfReporting")));
	        return;
	    }
	    
	    // Check if player has already been reported by this person
        if (plugin.checkPlayer(sender.getName(), toCheck.getName()) == true) {
        	sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getLangConfig().getString("prefix")) + ChatColor.translateAlternateColorCodes('&', plugin.getLangConfig().getString("alreadyReported").replace("%player%", toCheck.getName())));
        	return;
        } else {
        	plugin.addPlayer(sender.getName(), toCheck.getName());
        }
	    
	    // Announcing that we are indeed going to start watching a player
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getLangConfig().getString("prefix")) + ChatColor.translateAlternateColorCodes('&', plugin.getLangConfig().getString("successfulReportTwoHacks").replace("%player%", toCheck.getName()).replace("%hack1%", hackReported1).replace("%hack2%", hackReported2)));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getLangConfig().getString("prefix")) + ChatColor.translateAlternateColorCodes('&', plugin.getLangConfig().getString("thankPlayerForReport")));
        
        // Tell da admins
        Bukkit.broadcast(ChatColor.translateAlternateColorCodes('&', plugin.getLangConfig().getString("prefix")) + ChatColor.translateAlternateColorCodes('&', plugin.getLangConfig().getString("broadcastTwoHacks").replace("%player%", toCheck.getName()).replace("%hack1%", hackReported1).replace("%hack2%", hackReported2).replace("%reporter%", sender.getName())), "AAC.notify");
        
        // Their current VL when command was ran. Also final
		final int currentVLForHack1 = AACAPIProvider.getAPI().getViolationLevel(Bukkit.getPlayer(reportedPlayer), theHack1);
		final int currentVLForHack2 = AACAPIProvider.getAPI().getViolationLevel(Bukkit.getPlayer(reportedPlayer), theHack2);
		
        
        new BukkitRunnable() {
        	int counter = 0;
            public void run() {
            	if (Bukkit.getPlayer(reportedPlayer) != null) {
            		// Fetch the newVL since currentVL
                    int newVLForHack1 = AACAPIProvider.getAPI().getViolationLevel(Bukkit.getPlayer(reportedPlayer), theHack1);
                    int newVLForHack2 = AACAPIProvider.getAPI().getViolationLevel(Bukkit.getPlayer(reportedPlayer), theHack2);
                
                
                    // If their VL increased by this much, execute the command below
                    if (((newVLForHack1 - currentVLForHack1) >= violationsForHack1) && ((newVLForHack2 - currentVLForHack2) >= violationsForHack2)) {
                    	if (AACAPIProvider.getAPI().getPing(toCheck) >= plugin.getConfig().getInt("maximumPing") || AACAPIProvider.getAPI().getTPS() >= plugin.getConfig().getInt("minimumTPS")) {
                    		final String ripHacker = toCheck.getName();
                        	Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "kick " + ripHacker + " §fInternal exception: io.netty.handler.timeout.readTimeoutexception");
                        	Bukkit.dispatchCommand(Bukkit.getConsoleSender(), ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("banCommand").replace("%player%", ripHacker)));
                        	sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getLangConfig().getString("prefix")) + ChatColor.translateAlternateColorCodes('&', plugin.getLangConfig().getString("hackerGotCaught").replace("%player%", ripHacker)));
                        	Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', plugin.getLangConfig().getString("thanksForReporting")));
                        	
                        	// Tell staff what they got banned for
                        	Bukkit.broadcast(ChatColor.translateAlternateColorCodes('&', plugin.getLangConfig().getString("prefix")) + ChatColor.translateAlternateColorCodes('&', plugin.getLangConfig().getString("broadcastTwoHackBanned").replace("%player%", ripHacker).replace("%hack1%", hackReported1).replace("%hack2%", hackReported2)), "AAC.notify");
                        	plugin.removePlayer(sender.getName(), ripHacker);
                        	cancel();
                    	}
                    } else if ((newVLForHack1 - currentVLForHack1) >= violationsForHack1) {
                    	if (AACAPIProvider.getAPI().getPing(toCheck) >= plugin.getConfig().getInt("maximumPing") || AACAPIProvider.getAPI().getTPS() >= plugin.getConfig().getInt("minimumTPS")) {
                    		final String ripHacker = toCheck.getName();
                        	Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "kick " + ripHacker + " §fInternal exception: io.netty.handler.timeout.readTimeoutexception");
                        	Bukkit.dispatchCommand(Bukkit.getConsoleSender(), ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("banCommand").replace("%player%", ripHacker)));
                        	sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getLangConfig().getString("prefix")) + ChatColor.translateAlternateColorCodes('&', plugin.getLangConfig().getString("hackerGotCaught").replace("%player%", ripHacker)));
                        	Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', plugin.getLangConfig().getString("thanksForReporting")));
                        	
                        	// Tell staff what they got banned for
                        	Bukkit.broadcast(ChatColor.translateAlternateColorCodes('&', plugin.getLangConfig().getString("prefix")) + ChatColor.translateAlternateColorCodes('&', plugin.getLangConfig().getString("broadcastOneHackBanned").replace("%player%", ripHacker).replace("%hack%", hackReported1)), "AAC.notify");
                        	plugin.removePlayer(sender.getName(), ripHacker);
                        	cancel();
                    	}
                    } else if ((newVLForHack2 - currentVLForHack2) >= violationsForHack2) {
                    	if (AACAPIProvider.getAPI().getPing(toCheck) >= plugin.getConfig().getInt("maximumPing") || AACAPIProvider.getAPI().getTPS() >= plugin.getConfig().getInt("minimumTPS")) {
                        	final String ripHacker = toCheck.getName();
                        	Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "kick " + ripHacker + " §fInternal exception: io.netty.handler.timeout.readTimeoutexception");
                        	Bukkit.dispatchCommand(Bukkit.getConsoleSender(), ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("banCommand").replace("%player%", ripHacker)));
                        	sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getLangConfig().getString("prefix")) + ChatColor.translateAlternateColorCodes('&', plugin.getLangConfig().getString("hackerGotCaught").replace("%player%", ripHacker)));
                        	Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', plugin.getLangConfig().getString("thanksForReporting")));
                        	
                        	// Tell staff what they got banned for
                        	Bukkit.broadcast(ChatColor.translateAlternateColorCodes('&', plugin.getLangConfig().getString("prefix")) + ChatColor.translateAlternateColorCodes('&', plugin.getLangConfig().getString("broadcastOneHackBanned").replace("%player%", ripHacker).replace("%hack%", hackReported2)), "AAC.notify");
                        	plugin.removePlayer(sender.getName(), ripHacker);
                        	cancel();
                    	}
                    } else if (counter >= plugin.getConfig().getInt("checkPlayer") ) {
                    	cancel();
                    	yourReportGotNinjad(sender, toCheck.getName());
                    	plugin.removePlayer(sender.getName(), toCheck.getName());
                    }
                    counter++;
            	} else {
                	sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getLangConfig().getString("prefix")) + ChatColor.translateAlternateColorCodes('&', plugin.getLangConfig().getString("playerLoggedOut1").replace("%player%", reportedPlayer)));
                	sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getLangConfig().getString("playerLoggedOut2")));
                	plugin.removePlayer(sender.getName(), toCheck.getName());
                    cancel();
                }
            } 
        }.runTaskTimer(plugin, plugin.getConfig().getInt("tickDelay"), plugin.getConfig().getInt("tickDelay"));
	}
	
	public void vlCheckerThreeArgs(CommandSender sender, String reportedPlayer, String hackReported1, String hackReported2, String hackReported3, HackType theHack1, HackType theHack2, HackType theHack3, int violationsForHack1, int violationsForHack2, int violationsForHack3) {

		// The player who is reported
		final Player toCheck = Bukkit.getPlayer(reportedPlayer);
		
		if (toCheck == null) {
	           sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getLangConfig().getString("prefix")) + ChatColor.translateAlternateColorCodes('&', plugin.getLangConfig().getString("playerNotFound").replace("%player%", reportedPlayer)));
	           return;
	    }
	        
	    // Check if someone is trying to report themself
	    if (toCheck == sender) {
	    	sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getLangConfig().getString("prefix")) + ChatColor.translateAlternateColorCodes('&', plugin.getLangConfig().getString("preventSelfReporting")));
	        return;
	    }
	    
	    // Check if player has already been reported by this person
        if (plugin.checkPlayer(sender.getName(), toCheck.getName()) == true) {
        	sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getLangConfig().getString("prefix")) + ChatColor.translateAlternateColorCodes('&', plugin.getLangConfig().getString("alreadyReported").replace("%player%", toCheck.getName())));
        	return;
        } else {
        	plugin.addPlayer(sender.getName(), toCheck.getName());
        }
	    
	    // Announcing that we are indeed going to start watching a player
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getLangConfig().getString("prefix")) + ChatColor.translateAlternateColorCodes('&', plugin.getLangConfig().getString("successfulReportThreeHacks").replace("%player%", toCheck.getName()).replace("%hack1%", hackReported1).replace("%hack2%", hackReported2).replace("%hack3%", hackReported3)));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getLangConfig().getString("prefix")) + ChatColor.translateAlternateColorCodes('&', plugin.getLangConfig().getString("thankPlayerForReport")));
        
        // Tell da admins
        Bukkit.broadcast(ChatColor.translateAlternateColorCodes('&', plugin.getLangConfig().getString("prefix")) + ChatColor.translateAlternateColorCodes('&', plugin.getLangConfig().getString("broadcastThreeHacks").replace("%player%", toCheck.getName()).replace("%hack1%", hackReported1).replace("%hack2%", hackReported2).replace("%reporter%", sender.getName()).replace("%hack3%", hackReported3)), "AAC.notify");
        
        // Their current VL when command was ran. Also final
		final int currentVLForHack1 = AACAPIProvider.getAPI().getViolationLevel(Bukkit.getPlayer(reportedPlayer), theHack1);
		final int currentVLForHack2 = AACAPIProvider.getAPI().getViolationLevel(Bukkit.getPlayer(reportedPlayer), theHack2);
		final int currentVLForHack3 = AACAPIProvider.getAPI().getViolationLevel(Bukkit.getPlayer(reportedPlayer), theHack3);
		
        
        new BukkitRunnable() {
        	int counter = 0;
            public void run() {

            	if (Bukkit.getPlayer(reportedPlayer) != null) {
            	    // Fetch the newVL since currentVL
                    int newVLForHack1 = AACAPIProvider.getAPI().getViolationLevel(Bukkit.getPlayer(reportedPlayer), theHack1);
                    int newVLForHack2 = AACAPIProvider.getAPI().getViolationLevel(Bukkit.getPlayer(reportedPlayer), theHack2);
                    int newVLForHack3 = AACAPIProvider.getAPI().getViolationLevel(Bukkit.getPlayer(reportedPlayer), theHack3);
                
                
                    // If their VL increased by this much, execute the command below
                    if (((newVLForHack1 - currentVLForHack1) >= violationsForHack1) && ((newVLForHack2 - currentVLForHack2) >= violationsForHack2) && ((newVLForHack3 - currentVLForHack3) >= violationsForHack3)) {
                    	if (AACAPIProvider.getAPI().getPing(toCheck) >= plugin.getConfig().getInt("maximumPing") || AACAPIProvider.getAPI().getTPS() >= plugin.getConfig().getInt("minimumTPS")) {
                        	final String ripHacker = toCheck.getName();
                        	Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "kick " + ripHacker + " §fInternal exception: io.netty.handler.timeout.readTimeoutexception");
                        	Bukkit.dispatchCommand(Bukkit.getConsoleSender(), ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("banCommand").replace("%player%", ripHacker)));
                        	sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getLangConfig().getString("broadcastThreeHacksBanned").replace("%player%", ripHacker).replace("%hack1%", hackReported1).replace("%hack2%", hackReported2).replace("%hack3%", hackReported3)));
                        	Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', plugin.getLangConfig().getString("thanksForReporting")));
                        	
                        	// Tell staff what they got banned for
                        	Bukkit.broadcast(ChatColor.translateAlternateColorCodes('&', plugin.getLangConfig().getString("prefix")) + ChatColor.translateAlternateColorCodes('&', plugin.getLangConfig().getString("broadcastThreeHacksBanned").replace("%player%", ripHacker).replace("%hack1%", hackReported1).replace("%hack2%", hackReported2).replace("%hack3%", hackReported3)), "AAC.notify");
                        	plugin.removePlayer(sender.getName(), ripHacker);
                        	cancel();
                    	}
                    } else if (((newVLForHack1 - currentVLForHack1) >= violationsForHack1) && ((newVLForHack2 - currentVLForHack2) >= violationsForHack2)) {
                    	if (AACAPIProvider.getAPI().getPing(toCheck) >= plugin.getConfig().getInt("maximumPing") || AACAPIProvider.getAPI().getTPS() >= plugin.getConfig().getInt("minimumTPS")) {
                    		final String ripHacker = toCheck.getName();
                        	Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "kick " + ripHacker + " §fInternal exception: io.netty.handler.timeout.readTimeoutexception");
                        	Bukkit.dispatchCommand(Bukkit.getConsoleSender(), ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("banCommand").replace("%player%", ripHacker)));
                        	sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getLangConfig().getString("prefix")) + ChatColor.translateAlternateColorCodes('&', plugin.getLangConfig().getString("hackerGotCaught").replace("%player%", ripHacker)));
                        	Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', plugin.getLangConfig().getString("thanksForReporting")));
                        	
                        	// Tell staff what they got banned for
                        	Bukkit.broadcast(ChatColor.translateAlternateColorCodes('&', plugin.getLangConfig().getString("prefix")) + ChatColor.translateAlternateColorCodes('&', plugin.getLangConfig().getString("broadcastTwoHacksBanned").replace("%player%", ripHacker).replace("%hack1%", hackReported1).replace("%hack2%", hackReported2)), "AAC.notify");
                        	plugin.removePlayer(sender.getName(), ripHacker);
                        	cancel();
                    	}
                    } else if (((newVLForHack3 - currentVLForHack3) >= violationsForHack1) && ((newVLForHack2 - currentVLForHack2) >= violationsForHack2)) {
                    	if (AACAPIProvider.getAPI().getPing(toCheck) >= plugin.getConfig().getInt("maximumPing") || AACAPIProvider.getAPI().getTPS() >= plugin.getConfig().getInt("minimumTPS")) {
                    		final String ripHacker = toCheck.getName();
                        	Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "kick " + ripHacker + " §fInternal exception: io.netty.handler.timeout.readTimeoutexception");
                        	Bukkit.dispatchCommand(Bukkit.getConsoleSender(), ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("banCommand").replace("%player%", ripHacker)));
                        	sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getLangConfig().getString("prefix")) + ChatColor.translateAlternateColorCodes('&', plugin.getLangConfig().getString("hackerGotCaught").replace("%player%", ripHacker)));
                        	Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', plugin.getLangConfig().getString("thanksForReporting")));
                        	
                        	// Tell staff what they got banned for
                        	Bukkit.broadcast(ChatColor.translateAlternateColorCodes('&', plugin.getLangConfig().getString("prefix")) + ChatColor.translateAlternateColorCodes('&', plugin.getLangConfig().getString("broadcastTwoHacksBanned").replace("%player%", ripHacker).replace("%hack1%", hackReported3).replace("%hack2%", hackReported2)), "AAC.notify");
                        	plugin.removePlayer(sender.getName(), ripHacker);
                        	cancel();
                    	}
                    } else if ((newVLForHack1 - currentVLForHack1) >= violationsForHack1) {
                    	if (AACAPIProvider.getAPI().getPing(toCheck) >= plugin.getConfig().getInt("maximumPing") || AACAPIProvider.getAPI().getTPS() >= plugin.getConfig().getInt("minimumTPS")) {
                        	final String ripHacker = toCheck.getName();
                        	Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "kick " + ripHacker + " §fInternal exception: io.netty.handler.timeout.readTimeoutexception");
                        	Bukkit.dispatchCommand(Bukkit.getConsoleSender(), ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("banCommand").replace("%player%", ripHacker)));
                        	sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getLangConfig().getString("prefix")) + ChatColor.translateAlternateColorCodes('&', plugin.getLangConfig().getString("hackerGotCaught").replace("%player%", ripHacker)));
                        	Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', plugin.getLangConfig().getString("thanksForReporting")));
                        	
                        	// Tell staff what they got banned for
                        	Bukkit.broadcast(ChatColor.translateAlternateColorCodes('&', plugin.getLangConfig().getString("prefix")) + ChatColor.translateAlternateColorCodes('&', plugin.getLangConfig().getString("broadcastOneHackBanned").replace("%player%", ripHacker).replace("%hack%", hackReported1)), "AAC.notify");
                        	plugin.removePlayer(sender.getName(), ripHacker);
                        	cancel();
                    	}
                    } else if ((newVLForHack2 - currentVLForHack2) >= violationsForHack2) {
                    	if (AACAPIProvider.getAPI().getPing(toCheck) >= plugin.getConfig().getInt("maximumPing") || AACAPIProvider.getAPI().getTPS() >= plugin.getConfig().getInt("minimumTPS")) {
                        	final String ripHacker = toCheck.getName();
                        	Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "kick " + ripHacker + " §fInternal exception: io.netty.handler.timeout.readTimeoutexception");
                        	Bukkit.dispatchCommand(Bukkit.getConsoleSender(), ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("banCommand").replace("%player%", ripHacker)));
                        	sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getLangConfig().getString("prefix")) + ChatColor.translateAlternateColorCodes('&', plugin.getLangConfig().getString("hackerGotCaught").replace("%player%", ripHacker)));
                        	Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', plugin.getLangConfig().getString("thanksForReporting")));
                        	
                        	// Tell staff what they got banned for
                        	Bukkit.broadcast(ChatColor.translateAlternateColorCodes('&', plugin.getLangConfig().getString("prefix")) + ChatColor.translateAlternateColorCodes('&', plugin.getLangConfig().getString("broadcastOneHackBanned").replace("%player%", ripHacker).replace("%hack%", hackReported2)), "AAC.notify");
                        	plugin.removePlayer(sender.getName(), ripHacker);
                        	cancel();
                    	}
                    } else if ((newVLForHack3 - currentVLForHack3) >= violationsForHack3) {
                    	if (AACAPIProvider.getAPI().getPing(toCheck) >= plugin.getConfig().getInt("maximumPing") || AACAPIProvider.getAPI().getTPS() >= plugin.getConfig().getInt("minimumTPS")) {
                        	final String ripHacker = toCheck.getName();
                        	Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "kick " + ripHacker + " §fInternal exception: io.netty.handler.timeout.readTimeoutexception");
                        	Bukkit.dispatchCommand(Bukkit.getConsoleSender(), ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("banCommand").replace("%player%", ripHacker)));
                        	sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getLangConfig().getString("prefix")) + ChatColor.translateAlternateColorCodes('&', plugin.getLangConfig().getString("hackerGotCaught").replace("%player%", ripHacker)));
                        	Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', plugin.getLangConfig().getString("thanksForReporting")));
                        	
                        	// Tell staff what they got banned for
                        	Bukkit.broadcast(ChatColor.translateAlternateColorCodes('&', plugin.getLangConfig().getString("prefix")) + ChatColor.translateAlternateColorCodes('&', plugin.getLangConfig().getString("broadcastOneHackBanned").replace("%player%", ripHacker).replace("%hack%", hackReported3)), "AAC.notify");
                        	plugin.removePlayer(sender.getName(), ripHacker);
                        	cancel();
                    	}
                    } else if (counter >= plugin.getConfig().getInt("checkPlayer") ) {
                    	cancel();
                    	yourReportGotNinjad(sender, toCheck.getName());
                    	plugin.removePlayer(sender.getName(), toCheck.getName());
                    }
                    counter++;
            	} else {
                	sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getLangConfig().getString("prefix")) + ChatColor.translateAlternateColorCodes('&', plugin.getLangConfig().getString("playerLoggedOut1").replace("%player%", reportedPlayer)));
                	sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getLangConfig().getString("playerLoggedOut2")));
                	plugin.removePlayer(sender.getName(), toCheck.getName());
                    cancel();
                }
                
            } 
        }.runTaskTimer(plugin, plugin.getConfig().getInt("tickDelay"), plugin.getConfig().getInt("tickDelay"));
	}

    public void yourReportGotNinjad(CommandSender sender, String playerName) {
    	sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getLangConfig().getString("prefix")) + ChatColor.translateAlternateColorCodes('&', plugin.getLangConfig().getString("playerLoggedOut1").replace("%player%", playerName)));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', this.plugin.getLangConfig().getString("notEnoughEvidence")));
    }
    
    public String getProperNaming(String hackReported) {
		if (hackReported.equalsIgnoreCase("aimbot") || hackReported.equalsIgnoreCase("killaura") || hackReported.equalsIgnoreCase("forcefield") || hackReported.equalsIgnoreCase("ka")) {
			return "KillAura";
		} else if (hackReported.equalsIgnoreCase("knockback") || hackReported.equalsIgnoreCase("AntiKnockback") || hackReported.equalsIgnoreCase("velocity") || hackReported.equalsIgnoreCase("antikb") || hackReported.equalsIgnoreCase("kb") || hackReported.equalsIgnoreCase("anti-kb")) {
			return "AntiKnockback";
		} else if (hackReported.equalsIgnoreCase("fly") || hackReported.equalsIgnoreCase("flight") || hackReported.equalsIgnoreCase("flying")) {
			return "Fly";
		} else if (hackReported.equalsIgnoreCase("autoclicker") || hackReported.equalsIgnoreCase("auto-clicker") || hackReported.equalsIgnoreCase("macro") || hackReported.equalsIgnoreCase("macros")) {
			return "AutoClicker";
		} else if (hackReported.equalsIgnoreCase("speed")) {
			return "Speed";
		} else if (hackReported.equalsIgnoreCase("reach")) {
			return "Reach";
		}
		return "INVALID";
	}
	
	public HackType getAACNaming(String hackName) {
		if (hackName == "KillAura") {
			return me.konsolas.aac.api.HackType.KILLAURA;
		} else if (hackName == "AntiKnockback") {
			return me.konsolas.aac.api.HackType.VELOCITY;
		} else if (hackName == "Fly") {
			return me.konsolas.aac.api.HackType.FLY;
		} else if (hackName == "AutoClicker") {
			return me.konsolas.aac.api.HackType.FIGHTSPEED;
		} else if (hackName == "Speed") {
			return me.konsolas.aac.api.HackType.SPEED;
		} else if (hackName == "Reach") {
			return me.konsolas.aac.api.HackType.HITBOX;
		}
		return null;
	}
	
	public int getVLsToBanOn(String hackName) {
		int KillAuraVL = plugin.getConfig().getInt("banOnVL.KillAura");
		int AntiKnockBackVL = plugin.getConfig().getInt("banOnVL.AntiKnockback");
		int FlyVL = plugin.getConfig().getInt("banOnVL.Fly");
		int AutoClickerVL = plugin.getConfig().getInt("banOnVL.AutoClicker");
		int SpeedVL = plugin.getConfig().getInt("banOnVL.Speed");
		int ReachVL = plugin.getConfig().getInt("banOnVL.Reach");
		
		
		if (hackName == "KillAura") {
			return KillAuraVL;
		} else if (hackName == "AntiKnockback") {
			return AntiKnockBackVL;
		} else if (hackName == "Fly") {
			return FlyVL;
		} else if (hackName == "AutoClicker") {
			return AutoClickerVL;
		} else if (hackName == "Speed") {
			return SpeedVL;
		} else if (hackName == "Reach") {
			return ReachVL;
		}
		return 0;
	}
}
