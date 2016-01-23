package org.zet.cellularautomaton.algorithm.state;

import org.zet.cellularautomaton.DeathCause;
import org.zet.cellularautomaton.EvacCell;
import org.zet.cellularautomaton.Individual;
import org.zet.cellularautomaton.potential.Potential;
import org.zet.cellularautomaton.potential.StaticPotential;
import org.zetool.common.util.Direction8;
import org.zetool.common.util.Helper;

/**
 * Stores the mutable {@link Individual} properties during a simulation run. Instances of the class
 * are created in the initialization phase of the cellular automaton.
 *
 * @author Jan-Philipp Kappmeier
 */
public class IndividualProperty {

    /** The alarmstatus of the individual. */
    private boolean alarmed;
    /** The current relative speed of the individual. */
    private double relativeSpeed;
    /** The current exhaustion of the individual. */
    private double exhaustion = 0;
    /** The current panic of the individual. */
    private double panic = 0.0000;

    /** The potential the individual is following. */
    private StaticPotential staticPotential;
    /**
     * The (accurate) time when the moving of the individual is over. Initializes with 0 as step 0
     * is the first cellular automaton step.
     */
    private double stepEndTime = 0;
    /** The (accurate) time when the first moving of the individual starts. Initializes invalid. */
    private double stepStartTime = -1;
    /** The current direction the individual is heading to. */
    private Direction8 dir = Direction8.Top;

    /** The reason why the individual dies, if it is dead. */
    private DeathCause deathCause = null;
    /** The time, when the individual has last entered an area, where it is safe ( = area of save- and exitcells). */
    private int safetyTime = -1;
    /** The evacuation time of the individual. Strictly larger than the {@link #safetyTime}. */
    private int evacuationTime = -1;

    // outdated and to delete?
    private EvacCell cell;

    public IndividualProperty(Individual i) {
        this.relativeSpeed = i.getMaxSpeed();
        this.alarmed = false;
        this.cell = null;
        this.staticPotential = null;
    }

    /**
     * Alarms the Individual and also alarms the room of the cell of the individual.
     */
    public void setAlarmed() {
        this.alarmed = true;
    }

    /**
     * Get the setAlarmed status of the individual.
     *
     * @return {@code true} if the individual is alarmed, {@code false} otherwise
     */
    public boolean isAlarmed() {
        return alarmed;
    }

    /**
     * Set the current relative speed of the individual. The relative speed is a percentage of the
     * maximum speed .
     *
     * @param relativeSpeed the new speed
     */
    public void setRelativeSpeed(double relativeSpeed) {
        this.relativeSpeed = Helper.requireInRange(0, 1, relativeSpeed);
    }

    /**
     * Returns the current relative speed of the individual. The relativity is with respect to the
     * individuals max speed.
     *
     * @return the current speed
     */
    public double getRelativeSpeed() {
        return relativeSpeed;
    }

    /**
     * Get the exhaustion of the individual.
     *
     * @return the exhaustion
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
        this.exhaustion = Helper.requireNonNegative(val);
    }

    /**
     * Get the panic of the individual.
     *
     * @return the panic
     */
    public double getPanic() {
        return panic;
    }

    /**
     * Sets a new panic value.
     *
     * @param val the panic in range [0,1]
     */
    public void setPanic(double val) {
        this.panic = Helper.requireInRange(0, 1, val);
    }

    /**
     * Set the {@link Potential} which the individual is following.
     *
     * @param sp
     */
    public void setStaticPotential(StaticPotential sp) {
        this.staticPotential = sp;
    }

    /**
     * Get the {@link Potential} the individual is following.
     *
     * @return the static potential the individual is following
     */
    public StaticPotential getStaticPotential() {
        return staticPotential;
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

    public Direction8 getDirection() {
        return dir;
    }

    public void setDirection(Direction8 dir) {
        this.dir = dir;
    }

    public DeathCause getDeathCause() {
        if (!isDead()) {
            throw new IllegalStateException("Individual not dead");
        }
        return deathCause;
    }

    void setDeathCause(DeathCause deathCause) {
        if (isDead()) {
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
        if (!isSafe()) {
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
        if (isSafe() && time != safetyTime) {
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
        if (!isEvacuated()) {
            throw new IllegalStateException("Individual is not evacuated.");
        }
        return evacuationTime;
    }

    /**
     * Sets the time when the individual is evacuated.
     *
     * @param time The time when the individual is evacuated.
     */
    public void setEvacuationTime(int time) {
        if (isEvacuated()) {
            throw new IllegalStateException("Individual already evacuated at " + evacuationTime);
        }
        if (isSafe() && getSafetyTime() > time) {
            throw new IllegalArgumentException("Individual safe at time: " + getSafetyTime());
        }
        evacuationTime = Helper.requireNonNegative(time);
        if (!isSafe()) {
            setSafetyTime(time);
        }
    }

    public boolean isEvacuated() {
        return evacuationTime >= 0;
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
}
