package org.zet.cellularautomaton.algorithm.state;

import java.util.Optional;
import java.util.function.Consumer;
import org.zet.cellularautomaton.DeathCause;
import org.zet.cellularautomaton.Individual;
import org.zetool.common.util.Direction8;

/**
 * Collection of multiple updates for an {@link Individual}'s properties.
 * 
 * @author Jan-Philipp Kappmeier
 */
public class PropertyUpdate {

    /** The new alarmstatus. */
    private Boolean alarmed = null;
    /** The new relative speed. */
    private Double relativeSpeed = null;
    /** The new exhaustion. */
    private Double exhaustion = null;
    /** The new panic. */
    private Double panic = null;
    /** The (accurate) time when the move is over. */
    private Double stepEndTime = null;
    /** The (accurate) time when the move starts. */
    private Double stepStartTime = null;
    /** The new direction of view. */
    private Direction8 dir = Direction8.Top;
    /** The reason why the individual dies. */
    private DeathCause deathCause = null;
    /** The new time when an individual is saved. */
    private Integer safetyTime = null;
    /** The evacuation time */
    private Integer evacuationTime = null;

    private PropertyUpdate() {
    }

    private PropertyUpdate(PropertyUpdate update) {
        alarmed = update.alarmed;
        relativeSpeed = update.relativeSpeed;
        exhaustion = update.exhaustion;
        panic = update.panic;
        stepEndTime = update.stepEndTime;
        stepStartTime = update.stepStartTime;
        dir = update.dir;
        deathCause = update.deathCause;
        safetyTime = update.safetyTime;
        evacuationTime = update.evacuationTime;
    }

    public void apply(IndividualProperty ip) {
        if (alarmed != null) {
            ip.setAlarmed();
        }
        applyIfPresent(ip::setRelativeSpeed, relativeSpeed);
        applyIfPresent(ip::setExhaustion, exhaustion);
        applyIfPresent(ip::setPanic, panic);
        applyIfPresent(ip::setStepEndTime, stepEndTime);
        applyIfPresent(ip::setStepStartTime, stepStartTime);
        applyIfPresent(ip::setDirection, dir);
        applyIfPresent(ip::setDeathCause, deathCause);
        applyIfPresent(ip::setSafetyTime, safetyTime);
        applyIfPresent(ip::setEvacuationTime, evacuationTime);
    }

    private <T> void applyIfPresent(Consumer<T> c, T value) {
        if (value != null) {
            c.accept(value);
        }
    }

    public Optional<Boolean> isAlarmed() {
        return Optional.ofNullable(alarmed);
    }

    public Optional<Double> getRelativeSpeed() {
        return Optional.ofNullable(relativeSpeed);
    }

    public Optional<Double> getExhaustion() {
        return Optional.ofNullable(exhaustion);
    }

    public Optional<Double> getPanic() {
        return Optional.ofNullable(panic);
    }

    public Optional<Double> getStepEndTime() {
        return Optional.ofNullable(stepEndTime);
    }

    public Optional<Double> getStepStartTime() {
        return Optional.ofNullable(stepStartTime);
    }

    public Optional<Direction8> getDir() {
        return Optional.ofNullable(dir);
    }

    public Optional<DeathCause> getDeathCause() {
        return Optional.ofNullable(deathCause);
    }

    public Optional<Integer> getSafetyTime() {
        return Optional.ofNullable(safetyTime);
    }

    public Optional<Integer> getEvacuationTime() {
        return Optional.ofNullable(evacuationTime);
    }

    public static PropertyUpdateBuilder forMove(double stepStartTime, double stepEndTime) {
        return new PropertyUpdateBuilder().withStepStartTime(stepStartTime).withStepEndTime(stepEndTime);
    }

    public static PropertyUpdateBuilder extend(PropertyUpdate update) {
        return new PropertyUpdateBuilder(update);
    }

    public static class PropertyUpdateBuilder {

        private PropertyUpdate update;

        public PropertyUpdateBuilder() {
            update = new PropertyUpdate();
        }

        private PropertyUpdateBuilder(PropertyUpdate update) {
            this.update = new PropertyUpdate(update);
        }

        public PropertyUpdateBuilder alarmed(boolean alarmed) {
            update.alarmed = alarmed;
            return this;
        }

        public PropertyUpdateBuilder withRelativeSpeed(double relativeSpeed) {
            update.relativeSpeed = relativeSpeed;
            return this;
        }

        public PropertyUpdateBuilder withExhaustion(double exhaustion) {
            update.exhaustion = exhaustion;
            return this;
        }

        public PropertyUpdateBuilder withPanic(double panic) {
            update.panic = panic;
            return this;
        }

        public PropertyUpdateBuilder withStepEndTime(double stepEndTime) {
            update.stepEndTime = stepEndTime;
            return this;
        }

        public PropertyUpdateBuilder withStepStartTime(double stepStartTime) {
            update.stepStartTime = stepStartTime;
            return this;
        }

        public PropertyUpdateBuilder withDirection(Direction8 dir) {
            update.dir = dir;
            return this;
        }

        public PropertyUpdateBuilder withDeathCause(DeathCause deathCause) {
            update.deathCause = deathCause;
            return this;
        }

        public PropertyUpdateBuilder withSafetyTime(int safetyTime) {
            update.safetyTime = safetyTime;
            return this;
        }

        public PropertyUpdateBuilder withEvacuationTime(int evacuationTime) {
            update.evacuationTime = evacuationTime;
            return this;
        }

        public PropertyUpdate createUpdate() {
            PropertyUpdate createdUpdate = update;
            update = null;
            return createdUpdate;
        }
    }

}
