package xyz.haoshoku.haonick.handler;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xyz.haoshoku.haonick.HaoNick;
import xyz.haoshoku.nick.api.NickAPI;

public class PlaceholderAPIHandler extends PlaceholderExpansion {

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

            case "fake_rank": case "fakerank":
                return fakeRank == null ? "null" : fakeRank;

            case "fake_rank_prefix": case "fankrank_prefix": case "fakerankprefix":
                return fakeRank == null ? "null" : HaoNick.getPlugin().getConfigManager().getFakeRanksConfig().getString( "fake_ranks." + fakeRank + ".tab.prefix" );

            case "fake_rank_suffix": case "fakerank_suffix": case "fakeranksuffix":
                return fakeRank == null ? "null" : HaoNick.getPlugin().getConfigManager().getFakeRanksConfig().getString( "fake_ranks." + fakeRank + ".tab.suffix" );
        }

        return super.onPlaceholderRequest( player, params );
    }


}
