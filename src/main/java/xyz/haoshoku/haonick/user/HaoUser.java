package xyz.haoshoku.haonick.user;

import org.bukkit.entity.Player;

import java.util.UUID;

public class HaoUser {

    private UUID uuid;
    private Player player;
    private long nickModuleCooldown, randomModuleCooldown, skinModuleCooldown, unnickModuleCooldown, fakeRankModuleCooldown;

    private String fakeRank, rank;

    private boolean fakeRankLoop;
    /*
    MYSQL Load-Data
     */

    private UUID nickedUUID;
    private String nickedTag, nickedSkinValue, nickedSkinSignature, nickedGameProfile;

    public HaoUser( UUID uuid ) {
        this.uuid = uuid;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer( Player player ) {
        this.player = player;
    }

    public String getFakeRank() {
        return fakeRank;
    }

    public void setFakeRank( String fakeRank ) {
        this.fakeRank = fakeRank;
    }

    public String getRank() {
        return rank;
    }

    public void setRank( String rank ) {
        this.rank = rank;
    }

    public long getFakeRankModuleCooldown() {
        return fakeRankModuleCooldown;
    }

    public void setFakeRankModuleCooldown( long fakeRankModuleCooldown ) {
        this.fakeRankModuleCooldown = fakeRankModuleCooldown;
    }

    public boolean isFakeRankLoop() {
        return this.fakeRankLoop;
    }

    public void setFakeRankLoop( boolean fakeRankLoop ) {
        this.fakeRankLoop = fakeRankLoop;
    }

    public long getNickModuleCooldown() {
        return this.nickModuleCooldown;
    }

    public void setNickModuleCooldown( long nickModuleCooldown ) {
        this.nickModuleCooldown = nickModuleCooldown;
    }

    public long getRandomModuleCooldown() {
        return this.randomModuleCooldown;
    }

    public void setRandomModuleCooldown( long randomModuleCooldown ) {
        this.randomModuleCooldown = randomModuleCooldown;
    }

    public long getSkinModuleCooldown() {
        return this.skinModuleCooldown;
    }

    public void setSkinModuleCooldown( long skinModuleCooldown ) {
        this.skinModuleCooldown = skinModuleCooldown;
    }

    public long getUnnickModuleCooldown() {
        return unnickModuleCooldown;
    }

    public void setUnnickModuleCooldown( long unnickModuleCooldown ) {
        this.unnickModuleCooldown = unnickModuleCooldown;
    }

    public UUID getNickedUUID() {
        return this.nickedUUID;
    }

    public void setNickedUUID( UUID nickedUUID ) {
        this.nickedUUID = nickedUUID;
    }

    public String getNickedTag() {
        return this.nickedTag;
    }

    public void setNickedTag( String nickedTag ) {
        this.nickedTag = nickedTag;
    }

    public String getNickedSkinValue() {
        return this.nickedSkinValue;
    }

    public void setNickedSkinValue( String nickedSkinValue ) {
        this.nickedSkinValue = nickedSkinValue;
    }

    public String getNickedSkinSignature() {
        return this.nickedSkinSignature;
    }

    public void setNickedSkinSignature( String nickedSkinSignature ) {
        this.nickedSkinSignature = nickedSkinSignature;
    }

    public String getNickedGameProfile() {
        return this.nickedGameProfile;
    }

    public void setNickedGameProfile( String nickedGameProfile ) {
        this.nickedGameProfile = nickedGameProfile;
    }

}
