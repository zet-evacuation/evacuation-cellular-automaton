package org.zetool.simulation.ease;

import java.util.PriorityQueue;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class EventQueue {

    private final PriorityQueue<Event> queue = new PriorityQueue<>((e1, e2) -> e1.getTime() - e2.getTime());
    
    void addEvent(Event a) {
        queue.add(a);
    }

    boolean isEmpty() {
        return queue.isEmpty();
    }

    int nextEventTime() {
        return queue.peek().getTime();
    }

    Event extractNextEvent() {
        return queue.poll();
    }
    
}
