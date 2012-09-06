package scr;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Mar 4, 2008
 * Time: 3:35:31 PM
 */
public class Action {

    public double accelerate = 0; // 0..1
    public double brake = 0; // 0..1
    public double clutch=0; // 0..1
    public int gear = 0; // -1..6
    public double steering = 0;  // -1..1
    public boolean restartRace = false;
    public int focus = 360;//ML Desired focus angle in degrees [-90; 90], set to 360 if no focus reading is desired!

    public String toString () {
        limitValues ();
        return "(accel " + accelerate  + ") " +
               "(brake " + brake  + ") " +
               "(clutch " + clutch  + ") " +
               "(gear " + gear + ") " +
               "(steer " + steering + ") " +
               "(meta " + (restartRace ? 1 : 0) 
               + ") " + "(focus " + focus //ML
               + ")";
    }
    
    public double[] toArray(){
    	limitValues();
    	double[] actionArray = new double[7];
    	actionArray[0] = this.accelerate;
    	actionArray[1] = this.brake;
    	actionArray[2] = this.clutch;
    	actionArray[3] = (double) this.gear;
    	actionArray[4] = this.steering;
    	if (!restartRace)
    		actionArray[5] = 0.0;
    	else
    		actionArray[5] = 1.0;
    	actionArray[6] = (double) this.focus;
    	return actionArray;
    }

    public void limitValues () {
        accelerate = Math.max (0, Math.min (1, accelerate));
        brake = Math.max (0, Math.min (1, brake));
        clutch = Math.max(0, Math.min(1, clutch));
        steering = Math.max (-1, Math.min (1, steering));
        gear = Math.max (-1, Math.min (6, gear));
        
    }
}
