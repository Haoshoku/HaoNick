package xyz.haoshoku.haonick.handler;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xyz.haoshoku.haonick.HaoNick;
import xyz.haoshoku.haonick.util.ErrorUtils;
import xyz.haoshoku.nick.api.NickAPI;

public class PlaceholderAPIHandler extends PlaceholderExpansion {

    private LuckPermsHandler handler;

    public PlaceholderAPIHandler() {
        if ( Bukkit.getPluginManager().getPlugin( "LuckPerms" ) != null )
            this.handler = new LuckPermsHandler();
    }

    @Override
    public @NotNull String getIdentifier() {
        return "haonick";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Haoshoku";
    }

    @Override
    public @NotNull String getVersion() {
        return HaoNick.getPlugin().getDescription().getVersion();
    }

    @Override
    public String onPlaceholderRequest( Player player, @NotNull String params ) {
        if ( player == null || !player.isOnline() )
            return "";

        String fakeRank = HaoUserHandler.getUser( player ).getFakeRank();
        String rank = HaoUserHandler.getUser( player ).getRank();
        switch ( params.toLowerCase() ) {
            case "original_name": case "originalname":
                return NickAPI.getOriginalName( player );

            case "nick_name": case "nicked_name": case "nickname": case "name":
                return NickAPI.getName( player );

            case "isnicked": case "is_nicked":
                return String.valueOf( NickAPI.isNicked( player ) );

            case "isskinchanged": case "is_skin_changed":
                return String.valueOf( NickAPI.isSkinChanged( player ) );

            case "uuid":
                return String.valueOf( NickAPI.getUniqueId( player ) );

            case "rank": case "fake_rank": case "fakerank":
                return fakeRank == null ? "null" : fakeRank;

            case "rank_prefix": case "rankprefix": case "fakerank_prefix": case "fake_rank_prefix": case "fakerankprefix": {
                if ( NickAPI.isNicked( player ) )
                    return fakeRank == null ? "" : HaoNick.getPlugin().getConfigManager().getFakeRanksConfig().getString( "fake_ranks." + fakeRank + ".tab.prefix" );
                else if ( !HaoNick.getPlugin().getConfigManager().getSettingsConfig().getBoolean( "settings.luckperms_support" ) )
                    return rank == null ? "" : HaoNick.getPlugin().getConfigManager().getRanksConfig().getString( "ranks." + rank + ".tab.prefix" );
                else if ( handler != null )
                    return this.handler.applyLuckPermsData( player, "prefix" );
                else
                    ErrorUtils.err( "You have enabled LuckPerms support in your settings.yml but you do not have installed LuckPerms. Logic?" );
                    return "";
            }

            case "rank_suffix": case "ranksuffix": case "fakerank_suffix": case "fake_rank_suffix": case "fakeranksuffix": {
                if ( NickAPI.isNicked( player ) )
                    return fakeRank == null ? "" : HaoNick.getPlugin().getConfigManager().getFakeRanksConfig().getString( "fake_ranks." + fakeRank + ".tab.suffix" );
                else if ( !HaoNick.getPlugin().getConfigManager().getSettingsConfig().getBoolean( "settings.luckperms_support" ) )
                    return rank == null ? "" : HaoNick.getPlugin().getConfigManager().getRanksConfig().getString( "ranks." + rank + ".tab.suffix" );
                else if ( handler != null )
                    return this.handler.applyLuckPermsData( player, "suffix" );
                else
                    ErrorUtils.err( "You have enabled LuckPerms support in your settings.yml but you do not have installed LuckPerms. Logic?" );
                return "";
            }
        }

        return super.onPlaceholderRequest( player, params );
    }



}
