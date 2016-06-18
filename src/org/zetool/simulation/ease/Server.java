package org.zetool.simulation.ease;

import java.util.Objects;
import org.zetool.rndutils.distribution.continuous.ExponentialDistribution;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class Server extends Entity {
    private Client serving = null;
    private final EventQueue queue;
    private final Clock clock;
    private final WaitingSystem ws;
    private final ExponentialDistribution e = new ExponentialDistribution(1.0/120);

    public Server(EventQueue q, WaitingSystem ws, Clock clock) {
        this.queue = q;
        this.clock = clock;
        this.ws = ws;
        e.setMax(10000d);
    }

    boolean isFree() {
        return serving == null;
    }

    void serve(Client c) {
        serving = Objects.requireNonNull(c);
        int servingTime = e.getNextRandom().intValue();
        int now = clock.getTime();
        //System.out.println("Start serving at " + now + " for " + servingTime + " seconds");
        FinishedEvent e = new FinishedEvent(now + servingTime, ws, this);
        queue.addEvent(e);
    }
    
    void stopServing() {
        serving.setLeaveTime(clock.getTime());
        serving = null;
        //System.out.println("Stopped serving at" + clock.getTime());
    }

    Client getServed() {
        return serving;
    }
    
}
