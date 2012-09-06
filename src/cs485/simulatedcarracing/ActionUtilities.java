package cs485.simulatedcarracing;

import scr.Action;

public class ActionUtilities {

    public static Action deepCopy(Action action) {
        Action a = new Action();
        a.accelerate = action.accelerate;
        a.brake = action.brake;
        a.clutch = action.clutch;
        a.focus = action.focus;
        a.gear = action.gear;
        a.steering = action.steering;
        a.restartRace = action.restartRace;
        return a;
    }

}
