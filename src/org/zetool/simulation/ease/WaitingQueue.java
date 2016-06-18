package org.zetool.simulation.ease;

import java.util.ArrayDeque;
import java.util.Queue;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class WaitingQueue {
   Queue<Client> queue = new ArrayDeque<>();
   int limit = 100000;
   int rejected = 0;
   
   public void add(Client c) {
       if( size() >= limit ) {
           rejected++;
           return;
       }
       queue.add(c);
   }
   
   public boolean isEmpty() {
       return queue.isEmpty();
   }
   
   public Client extractNext() {
       return queue.poll();
   }

    int size() {
        return queue.size();
    }

    public int getRejected() {
        return rejected;
    }
}
