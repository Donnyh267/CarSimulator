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
 * this class will help a human to race the car
 * by using an actual GUI
 * @author geruk
 *
 */
public class HumanDriver implements KeyListener {
    
    private final Action action;
    private final Timer timerUp, timerDown, timerLeft, timerRight, timerSpace;
    private final TimerTask taskUp, taskDown, taskLeft, taskRight, taskSpace;
    private static final long delayms = 10; 
    
    /**
     * display a GUI for human
     * set up listener
     */
    public HumanDriver() {
        action = new Action();
        timerUp = new Timer(); timerDown = new Timer(); timerLeft = new Timer(); timerRight = new Timer(); timerSpace = new Timer();
        taskUp = new TimerTask(){
            public void run() {
                action.accelerate = Math.max(0, action.accelerate-1);
            }
        };
        taskDown = new TimerTask() {
            public void run() {
                action.brake = Math.max(0, action.brake-1);
            }
        };
        taskLeft = new TimerTask() {
            public void run() {
                action.steering = 0;
            }
        };
        taskRight = new TimerTask() {
            public void run() {
                action.steering = 0;
            }
        };
        taskSpace = new TimerTask() {
            public void run() {
                action.clutch = 0;
            }
        };
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                displayGUI();
            }
        });
    }
    
    protected void displayGUI() {
        JFrame frame = new JFrame("Controller");
        JPanel panel = new JPanel(new BorderLayout());
        frame.setContentPane(panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        JLabel label = new JLabel();
        label.setText("<html><ul><li>Up,Down,Left,Right</li><li>Space = Clutch</li><li>Z = gear down, X = gear up</li></ul>");
        panel.add(label, BorderLayout.CENTER);
        panel.addKeyListener(this);
        panel.setFocusable(true);
        panel.requestFocus(); panel.requestFocusInWindow();
        
        frame.pack();
        frame.setVisible(true);
    }

    public Action getAction(SensorModel sensors){
        action.limitValues();        
        return ActionUtilities.deepCopy(action);
    }
    
    private void displayInfo(KeyEvent arg0, String keyStatus) {
        System.out.println(keyStatus + ": " + arg0.getKeyCode());
    }

    @Override
    public void keyPressed(KeyEvent arg0) {
        displayInfo(arg0, "KeyPressed"); 
        try {
	        switch (arg0.getKeyCode()) {
	            case 38: action.accelerate = 1.0;  break;
	            case 37: action.steering = 1;  break;
	            case 39: action.steering = -1; break;
	            case 40: action.brake = 1;  break;
	            case 32: action.clutch = 1;  break;
	        }
        } catch  (java.lang.IllegalStateException e) {}
    }

    @Override
    public void keyReleased(KeyEvent arg0) {
        displayInfo(arg0, "KeyReleased"); 
        switch (arg0.getKeyCode()) {
        case 38: action.accelerate = 0.0;  break;
        case 37: action.steering = 0;  break;
        case 39: action.steering = 0; break;
        case 40: action.brake = 0;  break;
        case 32: action.clutch = 0;  break;
        }        
    }

    @Override
    public void keyTyped(KeyEvent arg0) {
        if (arg0.getKeyChar() == 'x') {
            action.gear = Math.min(action.gear+1, 6);
        } else
        if (arg0.getKeyChar() == 'z') {
            action.gear = Math.max(action.gear-1, -1);
        }
    }
}
