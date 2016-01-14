package org.zet.cellularautomaton.algorithm;

import org.zet.cellularautomaton.DeathCause;

/**
 * Stores the mutable {@link Individual} properties during a simulation run. Instances of the class are created in
 * the initialization phase of the cellular automaton.
 * 
 * @author Jan-Philipp Kappmeier
 */
public class IndividualProperty {
    private DeathCause deathCause = null;
    /** The time, when the individual has last entered an area, where it is safe ( = area of save- and exitcells). */
    private int safetyTime;

    public DeathCause getDeathCause() {
        if(!isDead()) {
            throw new IllegalStateException("Individual not dead");
        }
        return deathCause;
    }

    void setDeathCause(DeathCause deathCause) {
        this.deathCause = deathCause;
    }
    
    public boolean isDead() {
        return deathCause != null;
    }
    
    
}
