package org.zetool.simulation.ease;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class Client extends Entity {
    private final int id;
    
    private int arrivalTime;
    private int leaveTime;

    Client(int i) {
        this.id = i;
    }

    public void setArrivalTime(int arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public void setLeaveTime(int leaveTime) {
        this.leaveTime = leaveTime;
    }
    
    public int getTotalTime() {
        return leaveTime - arrivalTime;
    }
    
}
