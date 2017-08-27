package me.itzsomebody.watchdogreport;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import me.konsolas.aac.api.AACAPIProvider;
import me.konsolas.aac.api.HackType;

public class WatchdogReportCMD implements CommandExecutor {
	
	public WatchdogReport plugin = (me.itzsomebody.watchdogreport.WatchdogReport) JavaPlugin.getProvidingPlugin(WatchdogReport.class);
	
	public void WatchdogReport(WatchdogReport plugin) {
		WatchdogReport.plugin = plugin; // Store the plugin in situations where you need it.
	}
	
	public final int KillAuraVL = plugin.getConfig().getInt("banOnVL.KillAura");
	public final int AntiKnockbackVL = plugin.getConfig().getInt("banOnVL.AntiKnockback");
	public final int FlyVL = plugin.getConfig().getInt("banOnVL.Fly");
	public final int AutoClickerVL = plugin.getConfig().getInt("banOnVL.AutoClicker");
	public final int SpeedVL = plugin.getConfig().getInt("banOnVL.Speed");
	public final int ReachVL = plugin.getConfig().getInt("banOnVL.Reach");
	public final int maxPlayerPing = plugin.getConfig().getInt("maximumPing");
	public final int minServerTPS = plugin.getConfig().getInt("minimumTPS");
	public final String banCommand = plugin.getConfig().getString("banCommand");
	public final int tickDelay = plugin.getConfig().getInt("tickDelay");
	public final int checkPlayerXTimes = plugin.getConfig().getInt("checkPlayer");
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("watchdogreport")) {
			if (args != null) {
				if (args.length == 2) {
					if (getProperNaming(args[1]) == "INVALID") {
						sender.sendMessage(plugin.getLangConfig().getString("prefix").replace("&", "§") + plugin.getLangConfig().getString("unknownHacks").replace("&", "§"));
						return false;
					} else {
						vlChecker(sender, args[0], getAACNaming(getProperNaming(args[1])), getProperNaming(args[1]), getVLsToBanOn(getProperNaming(args[1])));
						return true;
					}
				} else if (args.length == 3) {
					if (getProperNaming(args[1]) == "INVALID" || getProperNaming(args[2]) == "INVALID") {
						sender.sendMessage(plugin.getLangConfig().getString("prefix").replace("&", "§") + plugin.getLangConfig().getString("unknownHacks").replace("&", "§"));
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
						sender.sendMessage(plugin.getLangConfig().getString("prefix").replace("&", "§") + plugin.getLangConfig().getString("unknownHacks").replace("&", "§"));
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
					sender.sendMessage(plugin.getLangConfig().getString("correctUsage").replace("&", "§"));
					return false;
			    }
		    } else {
				sender.sendMessage(plugin.getLangConfig().getString("correctUsage").replace("&", "§"));
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
            sender.sendMessage(plugin.getLangConfig().getString("prefix").replace("&", "§") + plugin.getLangConfig().getString("playerNotFound").replace("&", "§").replace("%player%", reportedPlayer));
            return;
        }
        
        // Check if someone is trying to report themself
        if (toCheck == sender) {
        	sender.sendMessage(plugin.getLangConfig().getString("prefix").replace("&", "§") + plugin.getLangConfig().getString("preventSelfReporting").replace("&", "§"));
        	return;
        }
       
        // Announcing that we are indeed going to start watching a player
        sender.sendMessage(plugin.getLangConfig().getString("prefix").replace("&", "§") + plugin.getLangConfig().getString("successfulReportOneHack").replace("&", "§").replace("%player%", toCheck.getName()).replace("%hack%", reportedHack));
        sender.sendMessage(plugin.getLangConfig().getString("prefix").replace("&", "§") + plugin.getLangConfig().getString("thankPlayerForReport").replace("&", "§"));
        
        // Tell da admins
        Bukkit.broadcast(plugin.getLangConfig().getString("prefix").replace("&", "§") + plugin.getLangConfig().getString("broadcaseOneHack").replace("&", "§").replace("%player%", toCheck.getName()).replace("%hack%", reportedHack).replace("%reporter%", sender.getName()), "AAC.notify");
        
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
            			if (AACAPIProvider.getAPI().getPing(toCheck) >= maxPlayerPing || AACAPIProvider.getAPI().getTPS() >= minServerTPS) {
            				final String ripHacker = toCheck.getName();
            				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "kick " + ripHacker + " §fInternal exception: io.netty.handler.timeout.readTimeoutexception");
            				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), banCommand.replace("%player%", ripHacker).replace("&", "§"));
            				sender.sendMessage(plugin.getLangConfig().getString("prefix").replace("&", "§") + plugin.getLangConfig().getString("hackerGotCaught").replace("%player%", ripHacker).replace("&", "§"));
            				Bukkit.broadcastMessage(plugin.getLangConfig().getString("thanksForReporting").replace("&", "§"));
                	
            				// Tell staff what they got banned for
            				Bukkit.broadcast(plugin.getLangConfig().getString("prefix").replace("&", "§") + plugin.getLangConfig().getString("broadcastOneHackBanned").replace("&", "§").replace("%player%", ripHacker).replace("%hack%", reportedHack), "AAC.notify");
            				
            				cancel();
                	}
                	
            		} else if (counter >= checkPlayerXTimes) {
            			cancel();
                		yourReportGotNinjad(sender, toCheck.getName());
            		}
            		counter++;
            	} else {
            		sender.sendMessage(plugin.getLangConfig().getString("prefix").replace("&", "§") + plugin.getLangConfig().getString("playerLoggedOut1").replace("&", "§").replace("%player%", reportedPlayer));
                	sender.sendMessage(plugin.getLangConfig().getString("playerLoggedOut2").replace("&", "§"));
                    cancel();
            	}
            }
        }.runTaskTimer(plugin, tickDelay, tickDelay);
	}
	
	public void vlCheckerTwoArgs(CommandSender sender, String reportedPlayer, String hackReported1, String hackReported2, HackType theHack1, HackType theHack2, int violationsForHack1, int violationsForHack2) {
		
		// The player who is reported
		final Player toCheck = Bukkit.getPlayer(reportedPlayer);
		
		if (toCheck == null) {
	           sender.sendMessage(plugin.getLangConfig().getString("prefix").replace("&", "§") + plugin.getLangConfig().getString("playerNotFound").replace("&", "§").replace("%player%", reportedPlayer));
	           return;
	    }
	        
	    // Check if someone is trying to report themself
	    if (toCheck == sender) {
	    	sender.sendMessage(plugin.getLangConfig().getString("prefix").replace("&", "§") + plugin.getLangConfig().getString("preventSelfReporting").replace("&", "§"));
	        return;
	    }
	    
	    // Announcing that we are indeed going to start watching a player
        sender.sendMessage(plugin.getLangConfig().getString("prefix").replace("&", "§") + plugin.getLangConfig().getString("successfulReportTwoHacks").replace("&", "§").replace("%player%", toCheck.getName()).replace("%hack1%", hackReported1).replace("%hack2%", hackReported2));
        sender.sendMessage(plugin.getLangConfig().getString("prefix").replace("&", "§") + plugin.getLangConfig().getString("thankPlayerForReport").replace("&", "§"));
        
        // Tell da admins
        Bukkit.broadcast(plugin.getLangConfig().getString("prefix").replace("&", "§") + plugin.getLangConfig().getString("broadcastTwoHacks").replace("&", "§").replace("%player%", toCheck.getName()).replace("%hack1%", hackReported1).replace("%hack2%", hackReported2).replace("%reporter%", sender.getName()), "AAC.notify");
        
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
                    	if (AACAPIProvider.getAPI().getPing(toCheck) >= maxPlayerPing || AACAPIProvider.getAPI().getTPS() >= minServerTPS) {
                    		final String ripHacker = toCheck.getName();
                        	Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "kick " + ripHacker + " §fInternal exception: io.netty.handler.timeout.readTimeoutexception");
                        	Bukkit.dispatchCommand(Bukkit.getConsoleSender(), banCommand.replace("%player%", ripHacker).replace("&", "§"));
                        	sender.sendMessage(plugin.getLangConfig().getString("prefix").replace("&", "§") + plugin.getLangConfig().getString("hackerGotCaught").replace("%player%", ripHacker).replace("&", "§"));
                        	Bukkit.broadcastMessage(plugin.getLangConfig().getString("thanksForReporting").replace("&", "§"));
                        	
                        	// Tell staff what they got banned for
                        	Bukkit.broadcast(plugin.getLangConfig().getString("prefix").replace("&", "§") + plugin.getLangConfig().getString("broadcastTwoHackBanned").replace("&", "§").replace("%player%", ripHacker).replace("%hack1%", hackReported1).replace("%hack2%", hackReported2), "AAC.notify");
                        	cancel();
                    	}
                    } else if ((newVLForHack1 - currentVLForHack1) >= violationsForHack1) {
                    	if (AACAPIProvider.getAPI().getPing(toCheck) >= maxPlayerPing || AACAPIProvider.getAPI().getTPS() >= minServerTPS) {
                    		final String ripHacker = toCheck.getName();
                        	Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "kick " + ripHacker + " §fInternal exception: io.netty.handler.timeout.readTimeoutexception");
                        	Bukkit.dispatchCommand(Bukkit.getConsoleSender(), banCommand.replace("%player%", ripHacker).replace("&", "§"));
                        	sender.sendMessage(plugin.getLangConfig().getString("prefix").replace("&", "§") + plugin.getLangConfig().getString("hackerGotCaught").replace("%player%", ripHacker).replace("&", "§"));
                        	Bukkit.broadcastMessage(plugin.getLangConfig().getString("thanksForReporting").replace("&", "§"));
                        	
                        	// Tell staff what they got banned for
                        	Bukkit.broadcast(plugin.getLangConfig().getString("prefix").replace("&", "§") + plugin.getLangConfig().getString("broadcastOneHackBanned").replace("&", "§").replace("%player%", ripHacker).replace("%hack%", hackReported1), "AAC.notify");
                        	cancel();
                    	}
                    } else if ((newVLForHack2 - currentVLForHack2) >= violationsForHack2) {
                    	if (AACAPIProvider.getAPI().getPing(toCheck) >= maxPlayerPing || AACAPIProvider.getAPI().getTPS() >= minServerTPS) {
                        	final String ripHacker = toCheck.getName();
                        	Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "kick " + ripHacker + " §fInternal exception: io.netty.handler.timeout.readTimeoutexception");
                        	Bukkit.dispatchCommand(Bukkit.getConsoleSender(), banCommand.replace("%player%", ripHacker).replace("&", "§"));
                        	sender.sendMessage(plugin.getLangConfig().getString("prefix").replace("&", "§") + plugin.getLangConfig().getString("hackerGotCaught").replace("%player%", ripHacker).replace("&", "§"));
                        	Bukkit.broadcastMessage(plugin.getLangConfig().getString("thanksForReporting").replace("&", "§"));
                        	
                        	// Tell staff what they got banned for
                        	Bukkit.broadcast(plugin.getLangConfig().getString("prefix").replace("&", "§") + plugin.getLangConfig().getString("broadcastOneHackBanned").replace("&", "§").replace("%player%", ripHacker).replace("%hack%", hackReported2), "AAC.notify");
                        	cancel();
                    	}
                    } else if (counter >= checkPlayerXTimes) {
                    	cancel();
                    	yourReportGotNinjad(sender, toCheck.getName());
                    }
                    counter++;
            	} else {
                	sender.sendMessage(plugin.getLangConfig().getString("prefix").replace("&", "§") + plugin.getLangConfig().getString("playerLoggedOut1").replace("&", "§").replace("%player%", reportedPlayer));
                	sender.sendMessage(plugin.getLangConfig().getString("playerLoggedOut2").replace("&", "§"));
                    cancel();
                }
            } 
        }.runTaskTimer(plugin, tickDelay, tickDelay);
	}
	
	public void vlCheckerThreeArgs(CommandSender sender, String reportedPlayer, String hackReported1, String hackReported2, String hackReported3, HackType theHack1, HackType theHack2, HackType theHack3, int violationsForHack1, int violationsForHack2, int violationsForHack3) {

		// The player who is reported
		final Player toCheck = Bukkit.getPlayer(reportedPlayer);
		
		if (toCheck == null) {
	           sender.sendMessage(plugin.getLangConfig().getString("prefix").replace("&", "§") + plugin.getLangConfig().getString("playerNotFound").replace("&", "§").replace("%player%", reportedPlayer));
	           return;
	    }
	        
	    // Check if someone is trying to report themself
	    if (toCheck == sender) {
	    	sender.sendMessage(plugin.getLangConfig().getString("prefix").replace("&", "§") + plugin.getLangConfig().getString("preventSelfReporting").replace("&", "§"));
	        return;
	    }
	    
	    // Announcing that we are indeed going to start watching a player
        sender.sendMessage(plugin.getLangConfig().getString("prefix").replace("&", "§") + plugin.getLangConfig().getString("successfulReportThreeHacks").replace("&", "§").replace("%player%", toCheck.getName()).replace("%hack1%", hackReported1).replace("%hack2%", hackReported2).replace("%hack3%", hackReported3));
        sender.sendMessage(plugin.getLangConfig().getString("prefix").replace("&", "§") + plugin.getLangConfig().getString("thankPlayerForReport").replace("&", "§"));
        
        // Tell da admins
        Bukkit.broadcast(plugin.getLangConfig().getString("prefix").replace("&", "§") + plugin.getLangConfig().getString("broadcastThreeHacks").replace("&", "§").replace("%player%", toCheck.getName()).replace("%hack1%", hackReported1).replace("%hack2%", hackReported2).replace("%reporter%", sender.getName()).replace("%hack3%", hackReported3), "AAC.notify");
        
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
                    	if (AACAPIProvider.getAPI().getPing(toCheck) >= maxPlayerPing || AACAPIProvider.getAPI().getTPS() >= minServerTPS) {
                        	final String ripHacker = toCheck.getName();
                        	Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "kick " + ripHacker + " §fInternal exception: io.netty.handler.timeout.readTimeoutexception");
                        	Bukkit.dispatchCommand(Bukkit.getConsoleSender(), banCommand.replace("%player%", ripHacker).replace("&", "§"));
                        	sender.sendMessage(plugin.getLangConfig().getString("broadcastThreeHacksBanned").replace("&", "§").replace("%player%", ripHacker).replace("%hack1%", hackReported1).replace("%hack2%", hackReported2).replace("%hack3%", hackReported3));
                        	Bukkit.broadcastMessage(plugin.getLangConfig().getString("thanksForReporting").replace("&", "§"));
                        	
                        	// Tell staff what they got banned for
                        	Bukkit.broadcast(plugin.getLangConfig().getString("prefix").replace("&", "§") + plugin.getLangConfig().getString("broadcastThreeHacksBanned").replace("&", "§").replace("%player%", ripHacker).replace("%hack1%", hackReported1).replace("%hack2%", hackReported2).replace("%hack3%", hackReported3), "AAC.notify");
                        	cancel();
                    	}
                    } else if (((newVLForHack1 - currentVLForHack1) >= violationsForHack1) && ((newVLForHack2 - currentVLForHack2) >= violationsForHack2)) {
                    	if (AACAPIProvider.getAPI().getPing(toCheck) >= maxPlayerPing || AACAPIProvider.getAPI().getTPS() >= minServerTPS) {
                    		final String ripHacker = toCheck.getName();
                        	Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "kick " + ripHacker + " §fInternal exception: io.netty.handler.timeout.readTimeoutexception");
                        	Bukkit.dispatchCommand(Bukkit.getConsoleSender(), banCommand.replace("%player%", ripHacker).replace("&", "§"));
                        	sender.sendMessage(plugin.getLangConfig().getString("prefix").replace("&", "§") + plugin.getLangConfig().getString("hackerGotCaught").replace("%player%", ripHacker).replace("&", "§"));
                        	Bukkit.broadcastMessage(plugin.getLangConfig().getString("thanksForReporting").replace("&", "§"));
                        	
                        	// Tell staff what they got banned for
                        	Bukkit.broadcast(plugin.getLangConfig().getString("prefix").replace("&", "§") + plugin.getLangConfig().getString("broadcastTwoHacksBanned").replace("&", "§").replace("%player%", ripHacker).replace("%hack1%", hackReported1).replace("%hack2%", hackReported2), "AAC.notify");
                        	cancel();
                    	}
                    } else if (((newVLForHack3 - currentVLForHack3) >= violationsForHack1) && ((newVLForHack2 - currentVLForHack2) >= violationsForHack2)) {
                    	if (AACAPIProvider.getAPI().getPing(toCheck) >= maxPlayerPing || AACAPIProvider.getAPI().getTPS() >= minServerTPS) {
                    		final String ripHacker = toCheck.getName();
                        	Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "kick " + ripHacker + " §fInternal exception: io.netty.handler.timeout.readTimeoutexception");
                        	Bukkit.dispatchCommand(Bukkit.getConsoleSender(), banCommand.replace("%player%", ripHacker).replace("&", "§"));
                        	sender.sendMessage(plugin.getLangConfig().getString("prefix").replace("&", "§") + plugin.getLangConfig().getString("hackerGotCaught").replace("%player%", ripHacker).replace("&", "§"));
                        	Bukkit.broadcastMessage(plugin.getLangConfig().getString("thanksForReporting").replace("&", "§"));
                        	
                        	// Tell staff what they got banned for
                        	Bukkit.broadcast(plugin.getLangConfig().getString("prefix").replace("&", "§") + plugin.getLangConfig().getString("broadcastTwoHacksBanned").replace("&", "§").replace("%player%", ripHacker).replace("%hack1%", hackReported3).replace("%hack2%", hackReported2), "AAC.notify");
                        	cancel();
                    	}
                    } else if ((newVLForHack1 - currentVLForHack1) >= violationsForHack1) {
                    	if (AACAPIProvider.getAPI().getPing(toCheck) >= maxPlayerPing || AACAPIProvider.getAPI().getTPS() >= minServerTPS) {
                        	final String ripHacker = toCheck.getName();
                        	Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "kick " + ripHacker + " §fInternal exception: io.netty.handler.timeout.readTimeoutexception");
                        	Bukkit.dispatchCommand(Bukkit.getConsoleSender(), banCommand.replace("%player%", ripHacker).replace("&", "§"));
                        	sender.sendMessage(plugin.getLangConfig().getString("prefix").replace("&", "§") + plugin.getLangConfig().getString("hackerGotCaught").replace("%player%", ripHacker).replace("&", "§"));
                        	Bukkit.broadcastMessage(plugin.getLangConfig().getString("thanksForReporting").replace("&", "§"));
                        	
                        	// Tell staff what they got banned for
                        	Bukkit.broadcast(plugin.getLangConfig().getString("prefix").replace("&", "§") + plugin.getLangConfig().getString("broadcastOneHackBanned").replace("&", "§").replace("%player%", ripHacker).replace("%hack%", hackReported1), "AAC.notify");
                        	cancel();
                    	}
                    } else if ((newVLForHack2 - currentVLForHack2) >= violationsForHack2) {
                    	if (AACAPIProvider.getAPI().getPing(toCheck) >= maxPlayerPing || AACAPIProvider.getAPI().getTPS() >= minServerTPS) {
                        	final String ripHacker = toCheck.getName();
                        	Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "kick " + ripHacker + " §fInternal exception: io.netty.handler.timeout.readTimeoutexception");
                        	Bukkit.dispatchCommand(Bukkit.getConsoleSender(), banCommand.replace("%player%", ripHacker).replace("&", "§"));
                        	sender.sendMessage(plugin.getLangConfig().getString("prefix").replace("&", "§") + plugin.getLangConfig().getString("hackerGotCaught").replace("%player%", ripHacker).replace("&", "§"));
                        	Bukkit.broadcastMessage(plugin.getLangConfig().getString("thanksForReporting").replace("&", "§"));
                        	
                        	// Tell staff what they got banned for
                        	Bukkit.broadcast(plugin.getLangConfig().getString("prefix").replace("&", "§") + plugin.getLangConfig().getString("broadcastOneHackBanned").replace("&", "§").replace("%player%", ripHacker).replace("%hack%", hackReported2), "AAC.notify");
                        	cancel();
                    	}
                    } else if ((newVLForHack3 - currentVLForHack3) >= violationsForHack3) {
                    	if (AACAPIProvider.getAPI().getPing(toCheck) >= maxPlayerPing || AACAPIProvider.getAPI().getTPS() >= minServerTPS) {
                        	final String ripHacker = toCheck.getName();
                        	Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "kick " + ripHacker + " §fInternal exception: io.netty.handler.timeout.readTimeoutexception");
                        	Bukkit.dispatchCommand(Bukkit.getConsoleSender(), banCommand.replace("%player%", ripHacker).replace("&", "§"));
                        	sender.sendMessage(plugin.getLangConfig().getString("prefix").replace("&", "§") + plugin.getLangConfig().getString("hackerGotCaught").replace("%player%", ripHacker).replace("&", "§"));
                        	Bukkit.broadcastMessage(plugin.getLangConfig().getString("thanksForReporting").replace("&", "§"));
                        	
                        	// Tell staff what they got banned for
                        	Bukkit.broadcast(plugin.getLangConfig().getString("prefix").replace("&", "§") + plugin.getLangConfig().getString("broadcastOneHackBanned").replace("&", "§").replace("%player%", ripHacker).replace("%hack%", hackReported3), "AAC.notify");
                        	cancel();
                    	}
                    } else if (counter >= checkPlayerXTimes) {
                    	cancel();
                    	yourReportGotNinjad(sender, toCheck.getName());
                    }
                    counter++;
            	} else {
                	sender.sendMessage(plugin.getLangConfig().getString("prefix").replace("&", "§") + plugin.getLangConfig().getString("playerLoggedOut1").replace("&", "§").replace("%player%", reportedPlayer));
                	sender.sendMessage(plugin.getLangConfig().getString("playerLoggedOut2").replace("&", "§"));
                    cancel();
                }
                
            } 
        }.runTaskTimer(plugin, tickDelay, tickDelay);
	}

    public void yourReportGotNinjad(CommandSender sender, String playerName) {
    	sender.sendMessage(plugin.getLangConfig().getString("prefix").replace("&", "§") + "§cYour report against §e" + playerName + "§c has been closed, but I'll keep watching them!");
        sender.sendMessage("§c§lI couldn't find enough evidence, please open a report at our website!" );
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
			return me.konsolas.aac.api.HackType.FORCEFIELD;
		} else if (hackName == "AntiKnockback") {
			return me.konsolas.aac.api.HackType.KNOCKBACK;
		} else if (hackName == "Fly") {
			return me.konsolas.aac.api.HackType.FLY;
		} else if (hackName == "AutoClicker") {
			return me.konsolas.aac.api.HackType.FIGHTSPEED;
		} else if (hackName == "Speed") {
			return me.konsolas.aac.api.HackType.SPEED;
		} else if (hackName == "Reach") {
			return me.konsolas.aac.api.HackType.REACH;
		}
		return null;
	}
	
	public int getVLsToBanOn(String hackName) {
		if (hackName == "KillAura") {
			return KillAuraVL;
		} else if (hackName == "AntiKnockback") {
			return AntiKnockbackVL;
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
