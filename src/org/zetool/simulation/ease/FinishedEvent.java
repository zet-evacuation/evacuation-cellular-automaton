package org.zetool.simulation.ease;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class FinishedEvent extends Event {
    private final WaitingSystem ws;
    private final Server server;

    public FinishedEvent(int time, WaitingSystem ws, Server s) {
        super(time);
        this.ws = ws;
        this.server = s;
    }

    @Override
    void handle() {
        Client c = server.getServed();
        server.stopServing();
        ws.servingComplete(c);
    }

}
