package xyz.haoshoku.haonick.handler;

import net.luckperms.api.LuckPerms;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import xyz.haoshoku.haonick.util.ErrorUtils;

public class LuckPermsHandler {

    private final RegisteredServiceProvider<LuckPerms> provider;
    private LuckPerms api;

    public LuckPermsHandler() {
        provider = Bukkit.getServicesManager().getRegistration( LuckPerms.class );
        if ( provider != null )
            this.api = provider.getProvider();
    }

    public String applyLuckPermsData( Player player, String key ) {
        String value = "";
        if ( key.equals( "prefix" ) )
            value = this.api.getUserManager().getUser( player.getUniqueId() )
                    .getCachedData().getMetaData().getPrefix();
        else
            value = this.api.getUserManager().getUser( player.getUniqueId() )
                    .getCachedData().getMetaData().getSuffix();

        if ( value == null ) {
            ErrorUtils.err( "No " + key + " found in " + player.getName() + "'s group" );
            return "";
        }

        return value;
    }
}
