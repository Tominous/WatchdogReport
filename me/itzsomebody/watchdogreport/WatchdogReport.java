package me.itzsomebody.watchdogreport;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class WatchdogReport extends JavaPlugin implements Listener {	
	public void onEnable() {
		getLogger().info("WDR has been enabled.");
		this.getCommand("watchdogreport").setExecutor(new WatchdogReportCMD(this));
		createConfigs();
		makeReportDirectory();
		removeAllReports();
		this.getServer().getPluginManager().registerEvents(this, this);
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerCommandPreprocessEvent(PlayerCommandPreprocessEvent event) {
		String message = event.getMessage().substring(1).split(" ")[0];
		String customCMD = this.getConfig().getString("customReportCommand");
		if (message.startsWith(customCMD)) {
			event.setMessage(event.getMessage().replace(customCMD, "watchdogreport"));
		}
	}
	
	public void onDisable() {
		getLogger().info("WDR has been disabled.");
	}
	
	private File configf, langf;
    private FileConfiguration config, lang;
	
	public FileConfiguration getLangConfig() {
        return this.lang;
    }
	
	public FileConfiguration getConfig() {
		return this.config;
	}
	
	public void createConfigs() {

        configf = new File(this.getDataFolder(), "config.yml");
        langf = new File(this.getDataFolder(), "lang.yml");

        if (!configf.exists()) {
            configf.getParentFile().mkdirs();
            this.saveResource("config.yml", false);
        }
        if (!langf.exists()) {
            langf.getParentFile().mkdirs();
            this.saveResource("lang.yml", false);
         }

        config = new YamlConfiguration();
        lang = new YamlConfiguration();
        try {
            config.load(configf);
            lang.load(langf);
        } catch (Exception e) {
        	// Just catch everything
			e.printStackTrace();
		}
    }
	
	@SuppressWarnings("serial")
	public boolean checkPlayer(String name, String reportedplayer) {
		try {
            File fileToRead = new File(this.getDataFolder() + "/reports/" + name + ".txt");
            
            if (!fileToRead.exists()) {
            	return false;
            }
            
            List<String> currentList = new ArrayList<String>() {};
            BufferedReader br = new BufferedReader(new FileReader(fileToRead));
            String line;
            while ((line = br.readLine()) != null) {
            	currentList.add(line);
            }
            
            if (currentList.contains(reportedplayer)) {
            	br.close();
            	return true;
            }
            
            br.close();
            currentList.clear();
            return false;
        } catch (Throwable t) {
            t.printStackTrace();
        }
		return false;
	}
	
	@SuppressWarnings("serial")
	public void addPlayer(String name, String reportedplayer) {
		try {
			File fileToRead = new File(this.getDataFolder() + "/reports/" + name + ".txt");
			File path = new File(this.getDataFolder() + "/reports");
            
            if (!path.exists() && !path.isDirectory()) {
            	path.mkdir();
            }
            
            if (!fileToRead.exists()) {
            	fileToRead.createNewFile();
            	List<String> saveaslist = new ArrayList<String>() {
            		{
            			this.add(reportedplayer);
            		}
            	};
                BufferedWriter bw = new BufferedWriter(new FileWriter(fileToRead));
                for (String s: saveaslist) {
                	bw.append(s);
                	bw.newLine();
                }
                bw.close();
                saveaslist.clear();
            } else {
            	List<String> currentlist = new ArrayList<String>() {
            		{
            			this.add(reportedplayer);
            		}
            	};
                BufferedReader br = new BufferedReader(new FileReader(fileToRead));
                String line;
                
                while ((line = br.readLine()) != null) {
                	currentlist.add(line);
                }
                br.close();
                
                BufferedWriter bw = new BufferedWriter(new FileWriter(fileToRead));
                for (String s: currentlist) {
                	bw.append(s);
                	bw.newLine();
                }
                bw.close();
                currentlist.clear();
            }
		} catch (Throwable t) {
			t.printStackTrace();
		} 
	}
	
	@SuppressWarnings("serial")
	public void removePlayer(String name, String reportedplayer) {
		try {
			File fileToRead = new File(this.getDataFolder() + "/reports/" + name + ".txt");
			File path = new File(this.getDataFolder() + "/reports");
            
            if (!path.exists() && !path.isDirectory()) {
            	path.mkdir();
            }
            
            if (!fileToRead.exists()) {
            	return;
            } else {
            	List<String> currentlist = new ArrayList<String>() {};
                
                BufferedReader br = new BufferedReader(new FileReader(fileToRead));
                String line;
                
                while ((line = br.readLine()) != null) {
                	currentlist.add(line);
                }
                br.close();
                
                
                for (int i = 0;  i < currentlist.size(); i++){
                    String s = currentlist.get(i);
                    if (s.equalsIgnoreCase(reportedplayer)) {
                    	currentlist.remove(i);
                    }
                }
                
                
                BufferedWriter bw = new BufferedWriter(new FileWriter(fileToRead));
                for (String s: currentlist) {                	
                	bw.append(s);
                	bw.newLine();
                }
                bw.close();
                
                if (currentlist.isEmpty()) {
                	fileToRead.delete();
                }
                currentlist.clear();
            }
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}
	
	public void removeAllReports() {
		try {
			String path = this.getDataFolder() + "/reports/";
			File files = new File(path + ".");
			FileUtils.cleanDirectory(files);
			getLogger().info("Successfully removed previous report files.");
		} catch (IOException ioexc) {
			getLogger().info("Error attempting to remove previous report files.");
			ioexc.printStackTrace();
		}
	}
	
	public void makeReportDirectory() {
		try {
			File path = new File(this.getDataFolder() + "/reports");
            
            if (!path.exists() && !path.isDirectory()) {
            	path.mkdir();
            }
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}
}