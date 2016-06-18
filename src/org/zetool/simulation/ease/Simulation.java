package org.zetool.simulation.ease;

import org.zetool.common.util.Formatter;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class Simulation {
    public static void main(String args[]) {
        System.out.println("Setting up System");
        
        WaitingSystem system = new WaitingSystem();
        
        WaitingQueue wq = new WaitingQueue();
        system.setQueue(wq);

        SimulationClock clock = new SimulationClock();
        
        EventQueue q = new EventQueue();

        
        Server s = new Server(q, system, clock);
        system.addServer(s);
        system.addServer(new Server(q, system, clock));
        system.addServer(new Server(q, system, clock));
        system.addServer(new Server(q, system, clock));
        system.addServer(new Server(q, system, clock));
        system.addServer(new Server(q, system, clock));
        
        // First event
        Source source = new Source(q, system);
        
        int maxTime = 200000000;

        int lastNumberInSystem = 0;
        int maxNumberInSystem = 0;
        int avgInSystem = 0;
        int lastTime = -1;
        
        while(!q.isEmpty() && q.nextEventTime() <= maxTime) {
            Event e = q.extractNextEvent();
            clock.setTime(e.getTime());
            //System.out.println(e.getTime() + ":");
            e.handle();
            if( e.getTime() > lastTime) {
                int diff = e.getTime() - lastTime - 1;
                avgInSystem += diff * lastNumberInSystem;
                lastNumberInSystem = wq.size() + system.busyServers();
                lastTime = e.getTime();
                maxNumberInSystem = Math.max(maxNumberInSystem, lastNumberInSystem);
            }
        }
        
        System.out.println("Total arrivals: " + source.getArrivals());
        System.out.println("Average number in system: " + (double)avgInSystem / maxTime);
        System.out.println("Max number in system: " + maxNumberInSystem);
        
        System.out.println("Rejected: " + wq.getRejected() + " (" + Formatter.formatPercent((double)wq.getRejected()/source.getArrivals()) + ")");
        
        double W = system.getAverageTotalTime();
        System.out.println("Average total time: " + W);
        System.out.println("L=" + 2 * (W / 60));
        
        
    }
    
    
}
