package org.zet.cellularautomaton.algorithm.parameter;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public abstract class ParameterSetAdapter extends AbstractParameterSet {

    protected final double panicToProbabilityOfPotentialChangeRatio;
    protected final double slacknessToIdleRatio;
    protected final double panicDecrease;
    protected final double panicIncrease;
    protected final double panicWeightOnSpeed;
    protected final double panicWeightOnPotentials;
    protected final double exhaustionWeightOnSpeed;
    protected final double panicThreshold;

    public ParameterSetAdapter(double panicToProbabilityOfPotentialChangeRatio, double slacknessToIdleRatio,
            double panicDecrease, double panicIncrease, double panicWeightOnSpeed, double panicWeightOnPotentials,
            double exhaustionWeightOnSpeed, double panicThreshold) {
        this.panicToProbabilityOfPotentialChangeRatio = panicToProbabilityOfPotentialChangeRatio;
        this.slacknessToIdleRatio = slacknessToIdleRatio;
        this.panicDecrease = panicDecrease;
        this.panicIncrease = panicIncrease;
        this.panicWeightOnSpeed = panicWeightOnSpeed;
        this.panicWeightOnPotentials = panicWeightOnPotentials;
        this.exhaustionWeightOnSpeed = exhaustionWeightOnSpeed;
        this.panicThreshold = panicThreshold;
    }
    
    @Override
    public double slacknessToIdleRatio() {
        return slacknessToIdleRatio;
    }

    @Override
    public double panicToProbOfPotentialChangeRatio() {
        return panicToProbabilityOfPotentialChangeRatio;
    }

    @Override
    public double getPanicIncrease() {
        return panicIncrease;
    }

    @Override
    public double getPanicDecrease() {
        return panicDecrease;
    }

    @Override
    public double panicWeightOnSpeed() {
        return panicWeightOnSpeed;
    }

    @Override
    public double exhaustionWeightOnSpeed() {
        return exhaustionWeightOnSpeed;
    }


    @Override
    public double getPanicWeightOnPotentials() {
        return panicWeightOnPotentials;
    }

    @Override
    public double getPanicThreshold() {
        return panicThreshold;
    }

    @Override
    public double getReactionTime() {
        return 1;
    }
}
