package xyz.haoshoku.haonick;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.haoshoku.haonick.config.HaoConfig;
import xyz.haoshoku.haonick.database.DBConnection;
import xyz.haoshoku.haonick.listener.*;
import xyz.haoshoku.haonick.manager.HaoCommandManager;
import xyz.haoshoku.haonick.manager.HaoConfigManager;
import xyz.haoshoku.haonick.manager.PlaceholderAPIManager;
import xyz.haoshoku.haonick.metrics.Metrics;
import xyz.haoshoku.haonick.util.ConfigUtils;
import xyz.haoshoku.haonick.util.PlaceholderUtils;
import xyz.haoshoku.haonick.util.TabUtils;
import xyz.haoshoku.nick.api.NickAPI;

public class HaoNick extends JavaPlugin {

    private static HaoNick plugin;
    private HaoConfigManager configManager;
    private HaoCommandManager commandManager;
    private PlaceholderAPIManager placeholderAPIManager;
    private DBConnection dbConnection;

    @Override
    public void onLoad() {
        JavaPlugin.getPlugin( HaoNick.class ); // Is here to prevent possible bugs for 1.8.8 servers
        plugin = this;;
        this.configManager = new HaoConfigManager();
    }

    @Override
    public void onEnable() {
        this.configManager.setPrefix();
        this.commandManager = new HaoCommandManager();
        this.registerListener();
        this.registerNickAPIData();


        HaoConfig settingsConfig = this.configManager.getSettingsConfig();
        if ( settingsConfig.getBoolean( "settings.keep_nick.mysql.active" ) ) {
            this.dbConnection = new DBConnection( settingsConfig.getString( "settings.keep_nick.mysql.host" ),
                    settingsConfig.getInt( "settings.keep_nick.mysql.port" ),
                    settingsConfig.getString( "settings.keep_nick.mysql.database" ),
                    settingsConfig.getString( "settings.keep_nick.mysql.username" ),
                    settingsConfig.getString( "settings.keep_nick.mysql.password" ),
                    settingsConfig.getString( "settings.keep_nick.mysql.table_name" ) );
            this.dbConnection.connect();
        }

        if ( Bukkit.getPluginManager().getPlugin( "PlaceholderAPI" ) != null ) {
            this.placeholderAPIManager = new PlaceholderAPIManager();
            this.placeholderAPIManager.register();
        }

        int id = 9848;
        Metrics metrics = new Metrics( this, id );

        this.startCountdown();
        new ConfigUtils();
    }

    @Override
    public void onDisable() {
        HaoConfig settingsConfig = this.configManager.getSettingsConfig();
        if ( settingsConfig.getBoolean( "settings.keep_nick.mysql.active" ) ) {
            if ( this.dbConnection != null )
                this.dbConnection.disconnect();
        }
    }

    private void registerListener() {
        Listener[] listeners = new Listener[] { new AsyncPlayerChatListener(), new AsyncPlayerPreLoginListener(),
                new PlayerCommandPreprocessListener(), new PlayerJoinListener(), new PlayerKickListener(), new PlayerLoginListener(), new PlayerQuitListener() };

        for ( Listener listener : listeners )
            Bukkit.getPluginManager().registerEvents( listener, this );

    }

    private void registerNickAPIData() {
        if ( Bukkit.getPluginManager().getPlugin( "NickAPI" ) != null ) {
            Bukkit.getPluginManager().registerEvents( new NickFinishListener(), this );
            NickAPI.getConfig().setGameProfileChanges( true );
        }
    }

    public static HaoNick getPlugin() {
        return plugin;
    }

    public HaoConfigManager getConfigManager() {
        return this.configManager;
    }

    public DBConnection getDBConnection() {
        return this.dbConnection;
    }

    public void setDBConnection( DBConnection dbConnection ) {
        this.dbConnection = dbConnection;
    }

    public HaoCommandManager getCommandManager() {
        return commandManager;
    }

    private void startCountdown() {
        if ( this.configManager.getSettingsConfig().getBoolean( "settings.tab.header_and_footer.active" ) ) {
            Bukkit.getScheduler().runTaskTimerAsynchronously( this, () -> {
                HaoConfig settingsConfig = this.configManager.getSettingsConfig();

                for ( Player player : Bukkit.getOnlinePlayers() ) {
                    String header = settingsConfig.getMessage( "settings.tab.header_and_footer.header", player );
                    String footer = settingsConfig.getMessage( "settings.tab.header_and_footer.footer", player );

                    header = header.replace( "%online%", String.valueOf( Bukkit.getOnlinePlayers().size( ) ) );
                    footer = footer.replace( "%online%", String.valueOf( Bukkit.getOnlinePlayers().size( ) ) );
                    header = header.replace( "%maxPlayers%", String.valueOf( Bukkit.getMaxPlayers() ) );
                    footer = footer.replace( "%maxPlayers%", String.valueOf( Bukkit.getMaxPlayers() ) );
                    header = header.replace( "%name%", player.getName() );
                    footer = footer.replace( "%name%", player.getName() );
                    header = PlaceholderUtils.applyPlaceholder( player, header );
                    footer = PlaceholderUtils.applyPlaceholder( player, footer );
                    TabUtils.sendTabList( player, header, footer );
                }
            }, 20L, 20L * this.configManager.getSettingsConfig().getInt( "settings.tab.header_and_footer.update_interval" ) );
        }

        if ( this.configManager.getSettingsConfig().getBoolean( "settings.tab.timer.active" ) ) {
            Bukkit.getScheduler().runTaskTimer( this, TabUtils::updateNamesFromScoreboard, 20L, 20L * this.configManager.getSettingsConfig().getInt( "settings.tab.timer.update_interval" ) );
        }
    }

    public void updatePlaceholderAPI() {
        if ( Bukkit.getPluginManager().getPlugin( "PlaceholderAPI" ) != null ) {
            this.placeholderAPIManager.unregister();
            this.placeholderAPIManager.register();
        }
    }
}
