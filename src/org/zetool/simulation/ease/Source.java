package org.zetool.simulation.ease;

import org.zetool.rndutils.distribution.continuous.ErlangDistribution;
import org.zetool.rndutils.distribution.continuous.ExponentialDistribution;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class Source {
    private int time = 0;
    private final EventQueue wq;
    private final WaitingSystem ws;
    private int id = 0;
    private final ExponentialDistribution e = new ExponentialDistribution(1./30);
    private int arrivals = 0;

    public Source(EventQueue wq, WaitingSystem ws) {
        e.setMax(100000d);
        this.wq = wq;
        this.ws = ws;
        wq.addEvent(new SourceEvent(0, new Client(id++)));
    }

    public int getArrivals() {
        return arrivals;
    }
    
    
    
    private void addNewEvent() {
        int interArrivalTime = e.getNextRandom().intValue();
        time += interArrivalTime;
        //System.out.println("Next arrival after " + interArrivalTime + " seconds");
        wq.addEvent(new SourceEvent(Source.this.time, new Client(id++)));
    }
    
    private class SourceEvent extends Event {
        private final Client c;
        public SourceEvent(int time, Client c) {
            super(time);
            this.c = c;
        }

        @Override
        void handle() {
            // Place a new arrival event with the same time
            wq.addEvent(new ArrivalEvent(getTime(), ws, c));
            arrivals++;
            addNewEvent();
        }
    }
    
}
