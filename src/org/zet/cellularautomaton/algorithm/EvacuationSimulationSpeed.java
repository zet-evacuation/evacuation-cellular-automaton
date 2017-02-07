package org.zet.cellularautomaton.algorithm;

/**
 * Stores properties for an evacuation run that are constant for that run. That is for example the discretized speed or
 * the maximum number of steps.
 *
 * @author Jan-Philipp Kappmeier
 */
public class EvacuationSimulationSpeed {

    private static final double CELL_SIZE = 0.4;

    private final double absoluteMaxSpeed;
    private final double secondsPerStep;
    private final double stepsPerSecond;

    public EvacuationSimulationSpeed(double absoluteMaxSpeed) {
        if (absoluteMaxSpeed <= 0) {
            throw new IllegalArgumentException("Maximal speed must be greater than zero, was " + absoluteMaxSpeed);
        }
        this.absoluteMaxSpeed = absoluteMaxSpeed;
        this.secondsPerStep = computeSecondsPerStep(absoluteMaxSpeed);
        this.stepsPerSecond = computeStepsPerSecond(absoluteMaxSpeed);
    }
    
    
    public EvacuationSimulationSpeed(int stepsPerSecond) {
        if (stepsPerSecond <= 0) {
            throw new IllegalArgumentException("Steps per second must be positive, was " + stepsPerSecond);
        }
        this.stepsPerSecond = stepsPerSecond;
        secondsPerStep = 1.0/stepsPerSecond;
        absoluteMaxSpeed = stepsPerSecond * CELL_SIZE;
    }
    
    /**
     * Returns the absolute speed any individual in the cellular automaton can walk. The speed of the cellular automaton is
     * defined such that an individual that moves in each step will have this speed.
     * @return 
     */
    public double getAbsoluteMaxSpeed() {
        return absoluteMaxSpeed;
    }

    /**
     * Sets the maximal speed that any individual can walk. That means an individual with speed = 1 moves with 100
     * percent of the absolute max speed.
     *
     * @param absoluteMaxSpeed
     * @throws java.lang.IllegalArgumentException if absoluteMaxSpeed is less or equal to zero
     */
    private double computeSecondsPerStep(double absoluteMaxSpeed) {
        return CELL_SIZE / absoluteMaxSpeed;
    }

    private double computeStepsPerSecond(double absoluteMaxSpeed) {
        return absoluteMaxSpeed / CELL_SIZE;
    }

    /**
     * Returns the time passing during each discrete step of the cellular automaton.
     *
     * @return the seconds one step needs
     */
    public double getSecondsPerStep() {
        return secondsPerStep;
    }

    /**
     * Returns the number of steps performed by the cellular automaton within one second. The time depends of the
     * absolute max speed.
     *
     * @return the number of steps performed by the cellular automaton within one second.
     */
    public double getStepsPerSecond() {
        return stepsPerSecond;
    }

    /**
     * Returns the absolute speed of an individual in meter per second depending on its relative speed which is a
     * fraction between zero and one of the absolute max speed.
     *
     * @param relativeSpeed
     * @return the absolute speed in meter per seconds for a given relative speed.
     */
    public double absoluteSpeed(double relativeSpeed) {
        return absoluteMaxSpeed * relativeSpeed;
    }

}
