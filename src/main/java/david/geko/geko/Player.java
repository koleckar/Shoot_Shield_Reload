package david.geko.geko;

public class Player {
    private String playerName;
    private String characterName;
    private int lives;

    private boolean isGunLoaded;
    private boolean isShieldActive;
    private boolean isShooting;

    protected Player() {
        setLives(3);
        setGunLoaded(true);
        setShieldActive(false);
        setShooting(false);
    }


    public boolean isShooting() {
        return isShooting;
    }

    public void setShooting(boolean shooting) {
        isShooting = shooting;
    }

    public int getLives() {
        return lives;
    }

    public void setLives(int lives) {
        this.lives = lives;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public String getCharacterName() {
        return characterName;
    }

    public void setCharacterName(String characterName) {
        this.characterName = characterName;
    }

    public boolean isGunLoaded() {
        return isGunLoaded;
    }

    public void setGunLoaded(boolean gunLoaded) {
        isGunLoaded = gunLoaded;
    }

    public boolean isShieldActive() {
        return isShieldActive;
    }

    public void setShieldActive(boolean shieldActive) {
        isShieldActive = shieldActive;
    }
}
