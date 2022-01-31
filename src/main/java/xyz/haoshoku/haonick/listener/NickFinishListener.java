package xyz.haoshoku.haonick.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import xyz.haoshoku.haonick.scoreboard.ScoreboardHandling;
import xyz.haoshoku.haonick.util.NickUtils;
import xyz.haoshoku.nick.api.NickAPI;
import xyz.haoshoku.nick.events.NickFinishEvent;

public class NickFinishListener implements Listener {

    @EventHandler
    public void onFinish( NickFinishEvent event ) {
        Player player = event.getPlayer();

        if ( !player.isOnline() || Bukkit.getPlayer( event.getOriginalUniqueId() ) == null )
            return;

        if ( !event.getOriginalName().equalsIgnoreCase( event.getName() ) )
            ScoreboardHandling.updateNamesFromScoreboardDelayed();

        if ( player.getUniqueId() != event.getUniqueId() )
            NickUtils.setNickedValue( player, "nicked_uuid", event.getUniqueId().toString() );
        else
            NickUtils.setNickedValue( player, "nicked_uuid", "-" );

        if ( !event.getOriginalName().equals( event.getName() ) )
            NickUtils.setNickedValue( player, "nicked_tag", event.getName() );
        else
            NickUtils.setNickedValue( player, "nicked_tag", "-" );

        if ( !this.checkEquality( event.getOriginalSkinData(), event.getSkinData() ) ) {
            NickUtils.setNickedValue( player, "nicked_skin_value", event.getSkinData()[0] );
            NickUtils.setNickedValue( player, "nicked_skin_signature", event.getSkinData()[1] );
        } else {
            NickUtils.setNickedValue( player, "nicked_skin_value", "-" );
            NickUtils.setNickedValue( player, "nicked_skin_signature", "-" );
        }

        if ( !NickAPI.getOriginalName( player ).equals( player.getName() ) )
            NickUtils.setNickedValue( player, "nicked_game_profile_name", player.getName() );
        else
            NickUtils.setNickedValue( player, "nicked_game_profile_name", "-" );

    }

    private boolean checkEquality( String[] s1, String[] s2 ) {
        /*
        SRC: https://www.techiedelight.com/compare-two-string-arrays-java/
         */
        if ( s1 == s2 )
            return true;

        if ( s1 == null || s2 == null )
            return false;

        int n = s1.length;
        if ( n != s2.length )
            return false;

        for ( int i = 0; i < n; i++ ) {
            if ( !s1[i].equals( s2[i] ) )
                return false;
        }
        return true;
    }


}
