package org.zetool.simulation.ease;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class ArrivalEvent extends Event {
    private final WaitingSystem ws;
    private final Client c;

    public ArrivalEvent(int time, WaitingSystem ws, Client c) {
        super(time);
        this.ws = ws;
        this.c = c;
    }

    @Override
    void handle() {
        //System.out.println("Handling arrival event at time " + getTime());
        c.setArrivalTime(getTime());
        ws.arrive(c);
    }
    
    
    
}
