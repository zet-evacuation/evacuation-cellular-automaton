package org.zet.cellularautomaton.algorithm.state;

import org.zet.cellularautomaton.DeathCause;
import org.zet.cellularautomaton.EvacCell;
import org.zet.cellularautomaton.Individual;
import org.zet.cellularautomaton.potential.StaticPotential;
import org.zetool.common.util.Direction8;
import org.zetool.common.util.Helper;

/**
 * Stores the mutable {@link Individual} properties during a simulation run. Instances of the class are created in
 * the initialization phase of the cellular automaton.
 * 
 * @author Jan-Philipp Kappmeier
 */
public class IndividualProperty {
    private DeathCause deathCause = null;
    /** The time, when the individual has last entered an area, where it is safe ( = area of save- and exitcells). */
    private int safetyTime = -1;
    private int evacuationTime = -1;

    // General properties
    private double panic = 0.0001;
    private double exhaustion = 0;
    private double relativeSpeed;
    private boolean alarmed;

    private StaticPotential staticPotential;
    /**
     * The (accurate) time when the moving of the individual is over. Initializes with 0 as step 0 is the first cellular
     * automaton step.
     */
    private double stepEndTime = 0;
    /** The (accurate) time when the first moving of the individual starts. Initializes invalid. */
    private double stepStartTime = -1;
    Direction8 dir = Direction8.Top;

    // outdated and to delete?
    private EvacCell cell;

    public IndividualProperty(Individual i) {
        // Simulation state variables
        this.relativeSpeed = i.getMaxSpeed();
        this.alarmed = false;
        this.cell = null;
        this.staticPotential = null;
    }

    public DeathCause getDeathCause() {
        if(!isDead()) {
            throw new IllegalStateException("Individual not dead");
        }
        return deathCause;
    }

    void setDeathCause(DeathCause deathCause) {
        if( isDead()) {
            throw new IllegalStateException("Cannot set death cause twice. Is: " + this.deathCause);
        }
        this.deathCause = deathCause;
    }
    
    public boolean isDead() {
        return deathCause != null;
    }
    
    /**
     * Returns the time when the individual is safe.
     *
     * @return The time when the individual is safe.
     */
    public int getSafetyTime() {
        if(!isSafe()) {
            throw new IllegalStateException("Individual is not safe.");
        }
        return safetyTime;
    }

    /**
     * Sets the time when the individual is evacuated.
     *
     * @param time The time when the individual is evacuated.
     */
    void setSafetyTime(int time) {
        if(isSafe() && time != safetyTime) {
            throw new IllegalStateException("Individual already safe: " + safetyTime);
        }
        safetyTime = Helper.requireNonNegative(time);
    }
    
    public boolean isSafe() {
        return safetyTime >= 0;
    }

    /**
     * Returns the time when the individual is safe.
     *
     * @return The time when the individual is safe.
     */
    public int getEvacuationTime() {
        if(!isEvacuated()) {
            throw new IllegalStateException("Individual is not evacuated.");
        }
        return evacuationTime;
    }

    /**
     * Sets the time when the individual is evacuated.
     *
     * @param time The time when the individual is evacuated.
     */
    void setEvacuationTime(int time) {
        if(isEvacuated()) {
            throw new IllegalStateException("Individual already evacuated at " + evacuationTime);
        }
        if( isSafe() && getSafetyTime() > time ) {
            throw new IllegalArgumentException("Individual safe at time: " + getSafetyTime());
        }
        evacuationTime = Helper.requireNonNegative(time);
        if( !isSafe()) {
            setSafetyTime(time);
        }
    }
    
    public boolean isEvacuated() {
        return evacuationTime >= 0;
    }

    
    
    
    
    
    
    
    
    public double getStepEndTime() {
        return stepEndTime;
    }

    public void setStepEndTime(double stepEndTime) {
        this.stepEndTime = stepEndTime;
    }

    public double getStepStartTime() {
        return stepStartTime;
    }

    public void setStepStartTime(double stepStartTime) {
        this.stepStartTime = stepStartTime;
    }


    /**
     * Alarms the Individual and also alarms the room of the cell of the individual.
     *
     * @param alarmed decides wheather the individual is alarmed, or if it is stopped being alarmed
     */
    public void setAlarmed(boolean alarmed) {
        this.alarmed = alarmed;
    }

    /**
     * Get the setAlarmed status of the individual.
     *
     * @return true if the individual is alarmed, false otherwise
     */
    public boolean isAlarmed() {
        return alarmed;
    }

    /**
     * Get the exhaustion of the individual.
     *
     * @return The exhaustion
     */
    public double getExhaustion() {
        return exhaustion;
    }

    /**
     * Set the exhaustion of the Individual to a specified value.
     *
     * @param val the exhaustion
     */
    public void setExhaustion(double val) {
        this.exhaustion = val;
    }

    public Direction8 getDirection() {
        return dir;
    }

    public void setDirection(Direction8 dir) {
        this.dir = dir;
    }

    /**
     * Get the panic of the individual.
     *
     * @return The panic
     */
    public double getPanic() {
        return panic;
    }

    public void setPanic(double val) {
        this.panic = val;
    }
    /**
     * Set the current relative speed of the individual. The relative speed is a percentage of the maximum speed .
     *
     * @param relativeSpeed the new speed
     */
    public void setRelativeSpeed(double relativeSpeed) {
        this.relativeSpeed = relativeSpeed;
    }

    /**
     * Returns the current relative speed of the individual. The relativity is with respect to the individuals max
     * speed.
     *
     * @return the current speed
     */
    public double getRelativeSpeed() {
        return relativeSpeed;
    }

    /**
     * Set the {@link ds.ca.EvacCell} on which the {@code Individual} stands.
     *
     * @param c the cell
     */
    public void setCell(EvacCell c) {
        this.cell = c;
    }

    /**
     * Returns the {@link ds.ca.EvacCell} on which the {@code Individual} stands.
     *
     * @return The EvacCell
     */
    public EvacCell getCell() {
        return cell;
    }

    /**
     * Set the staticPotential of the individual.
     *
     * @param sp
     */
    public void setStaticPotential(StaticPotential sp) {
        this.staticPotential = sp;
    }

    /**
     * Get the staticPotential of the individual.
     *
     * @return The staticPotential
     */
    public StaticPotential getStaticPotential() {
        return staticPotential;
    }

}
