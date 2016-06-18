package org.zetool.simulation.ease;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class WaitingSystem {

    private WaitingQueue waitingQueue;
    private final List<Server> servers = new LinkedList<>();
    private final List<Integer> totalTimes = new LinkedList<>();

    public void arrive(Client c) {
        Server freeServer = getFreeServer();
        if (freeServer != null) {
            freeServer.serve(c);
        } else {
            waitingQueue.add(c);
            //System.out.println("Queue size: " + waitingQueue.size());
        }
    }
    
    public void servingComplete(Client c) {
        totalTimes.add(c.getTotalTime());
        if( waitingQueue.isEmpty()) {
            return;
        }
        Server freeServer = getFreeServer();
        freeServer.serve(waitingQueue.extractNext());
    }

    void setQueue(WaitingQueue wq) {
        this.waitingQueue = wq;
    }

    void addServer(Server s) {
        servers.add(s);
    }

    private Server getFreeServer() {
        for (Server s : servers) {
            if (s.isFree()) {
                return s;
            }
        }
        return null;
    }

    int busyServers() {
        int i = 0;
        for( Server s : servers) {
            if( !s.isFree()) {
                i++;
            }
        }
        return i;
    }
    
    double getAverageTotalTime() {
        int tt = totalTimes.stream().collect(Collectors.summingInt(Integer::intValue));
        return (double)tt/totalTimes.size();
    }
}
