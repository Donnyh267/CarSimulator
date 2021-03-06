package cs485.simulatedcarracing;

import java.awt.BorderLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import scr.Action;
import scr.SensorModel;

/**
 * this class will help a human to race the car by using an actual GUI
 * 
 * @author geruk
 * 
 */
public class HumanDriver implements KeyListener {

	private final Action action;
	private final Timer timerUp, timerDown, timerLeft, timerRight, timerSpace;
	private final TimerTask taskUp, taskDown, taskLeft, taskRight, taskSpace;
	private boolean upHeld = false, downHeld = false, leftHeld = false,
			rightHeld = false;
	private static final float decreasingRate = (float) 0.05;
	private static final float increasingRate = (float) 0.2;
	private static final float increasingSteeringRate = (float) 0.05;
	private static final long delayKeyTime = 33;
	private MainController controller;

	/**
	 * display a GUI for human set up listener
	 */
	public HumanDriver(MainController controller) {
		this.controller = controller;
		action = new Action();
		timerUp = new Timer();
		timerDown = new Timer();
		timerLeft = new Timer();
		timerRight = new Timer();
		timerSpace = new Timer();
		taskUp = new TimerTask() {
			public void run() {
				if (upHeld) {
					action.accelerate += increasingRate;
				} else {
					if (action.accelerate >= decreasingRate)
						action.accelerate -= decreasingRate;
					else
						action.accelerate = 0;
				}
			}
		};
		taskDown = new TimerTask() {
			public void run() {
				if (downHeld) {
					action.brake += increasingRate;
				} else {
					if (action.brake >= decreasingRate)
						action.brake -= decreasingRate;
					else
						action.brake = 0;
				}
			}
		};
		taskLeft = new TimerTask() {
			public void run() {
				if (leftHeld) {
					action.steering += increasingSteeringRate;
				} else {
					if (action.steering >= decreasingRate)
						action.steering -= decreasingRate;
					else if (action.steering > 0)
						action.steering = 0;
				}
			}
		};
		taskRight = new TimerTask() {
			public void run() {
				if (rightHeld) {
					action.steering -= increasingSteeringRate;
				} else {
					if (action.steering <= -decreasingRate)
						action.steering += decreasingRate;
					else if (action.steering < 0)
						action.steering = 0;
				}
			}
		};
		taskSpace = new TimerTask() {
			public void run() {
				action.clutch = 0;
			}
		};
		timerUp.schedule(taskUp, 0, delayKeyTime);
		timerDown.schedule(taskDown, 0, delayKeyTime);
		timerLeft.schedule(taskLeft, 0, delayKeyTime);
		timerRight.schedule(taskRight, 0, delayKeyTime);
		// timerSpace.schedule(taskSpace, 0, delayKeyTime);
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				displayGUI();
			}
		});
	}

	JFrame frame;
	
	protected void displayGUI() {
		frame = new JFrame("Controller");
		JPanel panel = new JPanel(new BorderLayout());
		frame.setContentPane(panel);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		JLabel label = new JLabel();
		label
				.setText("<html><ul><li>Up,Down,Left,Right</li><li>Space = Clutch</li><li>Z = gear down, X = gear up</li></ul>");
		panel.add(label, BorderLayout.CENTER);
		panel.addKeyListener(this);
		panel.setFocusable(true);
		panel.requestFocus();
		panel.requestFocusInWindow();

		frame.pack();
		frame.setVisible(true);
	}

	public Action getAction(SensorModel sensors) {
		action.limitValues();
		action.clutch = clutching(sensors, (float) action.clutch);
		action.gear = getGear(sensors);
		return ActionUtilities.deepCopy(action);
	}

	private void displayInfo(KeyEvent arg0, String keyStatus) {
		System.out.println(keyStatus + ": " + arg0.getKeyCode() + " at "
				+ arg0.getWhen());
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
		displayInfo(arg0, "KeyPressed");
		switch (arg0.getKeyCode()) {
		case 37:
			leftHeld = true;
			break;
		case 38:
			upHeld = true;
			break;
		case 39:
			rightHeld = true;
			break;
		case 40:
			downHeld = true;
			break;
		case 32:
			action.clutch += increasingRate;
			break;
		}
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		displayInfo(arg0, "KeyReleased");
		switch (arg0.getKeyCode()) {
		case 37:
			leftHeld = false;
			break;
		case 38:
			upHeld = false;
			break;
		case 39:
			rightHeld = false;
			break;
		case 40:
			downHeld = false;
			break;
		case 32:
			action.clutch -= increasingRate;
			break;
		}
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		if (arg0.getKeyChar() == 'x') {
			action.gear = Math.min(action.gear + 1, 6);
		} else if (arg0.getKeyChar() == 'z') {
			action.gear = Math.max(action.gear - 1, -1);
		}
	}

	/* Clutching Constants */
	final float clutchMax = (float) 0.5;
	final float clutchDelta = (float) 0.05;
	final float clutchRange = (float) 0.82;
	final float clutchDeltaTime = (float) 0.02;
	final float clutchDeltaRaced = 10;
	final float clutchDec = (float) 0.01;
	final float clutchMaxModifier = (float) 1.3;
	final float clutchMaxTime = (float) 1.5;

	/* Gear Changing Constants */
	final static int[] gearUp = { 5000, 6000, 6000, 6500, 7000, 0 };
	final static int[] gearDown = { 0, 2500, 3000, 3000, 3500, 3500 };

	public static int getGear(SensorModel sensors) {
		int gear = sensors.getGear();
		double rpm = sensors.getRPM();

		// if gear is 0 (N) or -1 (R) just return 1
		if (gear < 1)
			return 1;
		// check if the RPM value of car is greater than the one suggested
		// to shift up the gear from the current one
		if (gear < 6 && rpm >= gearUp[gear - 1])
			return gear + 1;
		else
		// check if the RPM value of car is lower than the one suggested
		// to shift down the gear from the current one
		if (gear > 1 && rpm <= gearDown[gear - 1])
			return gear - 1;
		else
			// otherwise keep current gear
			return gear;
	}

	private float clutching(SensorModel sensors, float clutch) {

		float maxClutch = clutchMax;

		// Check if the current situation is the race start
		if (sensors.getCurrentLapTime() < clutchDeltaTime
				&& sensors.getDistanceRaced() < clutchDeltaRaced)
			clutch = maxClutch;

		// Adjust the current value of the clutch
		if (clutch > 0) {
			double delta = clutchDelta;
			if (sensors.getGear() < 2) {
				// Apply a stronger clutch output when the gear is one and the
				// race is just started
				delta /= 2;
				maxClutch *= clutchMaxModifier;
				if (sensors.getCurrentLapTime() < clutchMaxTime)
					clutch = maxClutch;
			}

			// check clutch is not bigger than maximum values
			clutch = Math.min(maxClutch, clutch);

			// if clutch is not at max value decrease it quite quickly
			if (clutch != maxClutch) {
				clutch -= delta;
				clutch = Math.max((float) 0.0, clutch);
			}
			// if clutch is at max value decrease it very slowly
			else
				clutch -= clutchDec;
		}
		return clutch;
	}

	public void shutdown() {
		frame.dispose();timerUp.cancel();timerDown.cancel();timerRight.cancel();timerLeft.cancel();
	}
}
