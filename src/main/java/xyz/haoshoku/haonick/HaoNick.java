package xyz.haoshoku.haonick;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.haoshoku.haonick.config.HaoConfig;
import xyz.haoshoku.haonick.database.DBConnection;
import xyz.haoshoku.haonick.handler.*;
import xyz.haoshoku.haonick.listener.*;
import xyz.haoshoku.haonick.metrics.Metrics;
import xyz.haoshoku.nick.api.NickAPI;

public class HaoNick extends JavaPlugin {

    private static HaoNick plugin;
    private HaoConfigHandler configManager;
    private HaoCommandHandler commandManager;
    private PlaceholderAPIHandler placeholderAPIManager;
    private DBConnection dbConnection;

    @Override
    public void onLoad() {
        JavaPlugin.getPlugin( HaoNick.class ); // Is here to prevent possible bugs for 1.8.8 servers
        plugin = this;;
        this.configManager = new HaoConfigHandler();
    }

    @Override
    public void onEnable() {
        this.commandManager = new HaoCommandHandler();
        this.registerListener();

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
            this.placeholderAPIManager = new PlaceholderAPIHandler();
            this.placeholderAPIManager.register();
        }

        int id = 9848;
        new Metrics( this, id );

        new HaoCountdownHandler().startCountdown();
        new HaoConfigUpdaterHandler();
        this.configManager.setPrefix();
        this.registerNickAPIData();


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
                new PlayerCommandPreprocessListener(), new PlayerDeathListener(), new PlayerJoinListener(), new PlayerKickListener(), new PlayerLoginListener(),
                new PlayerQuitListener(), new PluginDisableListener(), new PluginEnableListener() };

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

    public HaoConfigHandler getConfigManager() {
        return this.configManager;
    }

    public DBConnection getDBConnection() {
        return this.dbConnection;
    }
    

    public HaoCommandHandler getCommandManager() {
        return this.commandManager;
    }

    public PlaceholderAPIHandler getPlaceholderAPIManager() {
        return this.placeholderAPIManager;
    }
}
