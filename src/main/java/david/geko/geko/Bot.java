package david.geko.geko;

import java.util.Random;

public class Bot {
    //todo: implement multiple strategies?

    static public Action makeAnyAction() {
        return switch (new Random().nextInt(Action.values().length)) {
            case 0 -> Action.SHOOT;
            case 1 -> Action.SHIELD;
            case 2 -> Action.RELOAD;
            default -> throw new RuntimeException("illegal Action.");
        };
    }

    static public Action shootOrShield() {
        return switch (new Random().nextInt(Action.values().length - 1)) {
            case 0 -> Action.SHOOT;
            case 1 -> Action.SHIELD;
            default -> throw new RuntimeException("illegal Action.");
        };
    }

}
