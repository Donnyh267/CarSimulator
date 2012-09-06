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
    private final Timer timerUp, timerDown, timerSteering, timerSpace;
    private final TimerTask taskUp, taskDown, taskSteering, taskSpace;
    private static final long delayms = 100; 
    
    /**
     * display a GUI for human
     * set up listener
     */
    public HumanDriver() {
        action = new Action();
        timerUp = new Timer(); timerDown = new Timer(); timerSteering = new Timer(); timerSpace = new Timer();
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
        taskSteering = new TimerTask() {
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
        switch (arg0.getKeyCode()) {
            case 38: action.accelerate = 1.0; timerUp.cancel(); break;
            case 37: action.steering = -1; timerSteering.cancel(); break;
            case 39: action.steering = 1; timerSteering.cancel(); break;
            case 40: action.brake = 1; timerDown.cancel(); break;
            case 32: action.clutch = 1; timerSpace.cancel(); break;
        }
    }

    @Override
    public void keyReleased(KeyEvent arg0) {
        displayInfo(arg0, "KeyReleased"); 
        switch (arg0.getKeyCode()) {
            case 38: timerUp.schedule(taskUp, delayms); break;
            case 37: timerSteering.schedule(taskSteering, delayms); break;
            case 39: timerSteering.schedule(taskSteering, delayms); break;
            case 40: timerDown.schedule(taskDown, delayms); break;
            case 32: timerSpace.schedule(taskSpace, delayms); break;
        }        
    }

    @Override
    public void keyTyped(KeyEvent arg0) {
        if (arg0.getKeyChar() == 'x') {
            action.gear = Math.max(action.gear+1, 6);
        } else
        if (arg0.getKeyChar() == 'z') {
            action.gear = Math.min(action.gear-1, -1);
        }
    }
}
