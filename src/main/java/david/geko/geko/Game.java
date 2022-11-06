package david.geko.geko;

import java.util.ArrayList;

public class Game {
    protected ArrayList<Player> players;
    protected Player myPlayer;
    protected int round;
    protected boolean isMultiplayer;

    public Game() {
        players = new ArrayList<>();
        round = 1;
    }

    protected void performAllActions(){
        // performs all actions, updates game status

    }

    protected void botMakesAction(int playerID){
        Player player = players.get(playerID);

        Action action = player.isGunLoaded() ? Bot.makeAnyAction() : Bot.shootOrShield() ;
        switch (action){
            case SHOOT -> throw new RuntimeException("not implemented");
            case SHIELD -> player.setShieldActive(true);
            case RELOAD -> player.setGunLoaded(true);
        }

    }

    void addPlayers(Player player) {
        if (players.size() < 3) {
            players.add(player);
        } else {
            throw new RuntimeException("Cannot add 4rth player.");
        }
    }

    void incrementRound() {
        round++;
    }

    void setMultiplayer(boolean multiplayer) {
       isMultiplayer = multiplayer;
    }
}
