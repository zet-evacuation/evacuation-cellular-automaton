package org.zetool.simulation.ease;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class SimulationClock implements Clock {
    private int time = 0;
    
    @Override
    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }
    
}
