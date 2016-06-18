package org.zetool.simulation.ease;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class Event {
    private final int time;

    public Event(int time) {
        this.time = time;
    }

    void handle() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public int getTime() {
        return time;
    }
    
    
    
}
