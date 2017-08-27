package me.itzsomebody.watchdogreport;

import java.io.File;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class WatchdogReport extends JavaPlugin {

	public static WatchdogReport plugin;
	
	private File configf, langf;
    private FileConfiguration config, lang;

	public void onEnable() {
		getLogger().info("WDR has been enabled.");
		this.getCommand("watchdogreport").setExecutor(new WatchdogReportCMD());
		createConfigs();
	}
	
	public void checkAACVersion() {
		String AACVersion = Bukkit.getServer().getPluginManager().getPlugin("Watchdog").getDescription().getVersion();
		if (AACVersion != "1.9.10") {
			getLogger().info("Please use the correct version of Watchdog (1.9.10)");
			getServer().getPluginManager().disablePlugin(this);
		}
	}
	
	public FileConfiguration getLangConfig() {
        return this.lang;
    }
	
	private void createConfigs() {

        configf = new File(getDataFolder(), "config.yml");
        langf = new File(getDataFolder(), "lang.yml");

        if (!configf.exists()) {
            configf.getParentFile().mkdirs();
            saveResource("config.yml", false);
        }
        if (!langf.exists()) {
            langf.getParentFile().mkdirs();
            saveResource("lang.yml", false);
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
	
	public void onDisable() {
		getLogger().info("WDR has been disabled.");
	}
}