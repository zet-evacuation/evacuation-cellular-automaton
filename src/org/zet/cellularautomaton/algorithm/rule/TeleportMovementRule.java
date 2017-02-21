package org.zet.cellularautomaton.algorithm.rule;

import java.util.List;
import java.util.Optional;
import org.zet.cellularautomaton.TeleportCell;
import org.zet.cellularautomaton.EvacCellInterface;
import org.zet.cellularautomaton.algorithm.EvacuationSimulationSpeed;
import org.zet.cellularautomaton.algorithm.computation.Computation;
import org.zet.cellularautomaton.algorithm.state.EvacuationState;
import org.zet.cellularautomaton.algorithm.state.EvacuationStateControllerInterface;
import org.zet.cellularautomaton.results.MoveAction;
import org.zet.cellularautomaton.results.SwapAction;

/**
 * A special implementation of a {@link MovementRule} that supports teleportation. When the rule is
 * executed on an instance of {@link TeleportCell} it cannot be executed when the teleport failed.
 * On other {@link EvacCellInterface} instances the rule is executeable as always. When the rule is
 * executeable, its calls are delegated to another movement rule instance. This instance may not be
 * aware of teleportation.
 *
 * @author Jan-Philipp Kappmeier
 */
public class TeleportMovementRule implements MovementRule {

    private final MovementRule movementRule;

    public TeleportMovementRule() {
        this.movementRule = new WaitingMovementRule();
    }

    TeleportMovementRule(MovementRule movementRule) {
        this.movementRule = movementRule;
    }

    /**
     * When the cell is an instanceo of {@link TeleportCell}, the rule is only executeable when the
     * teleport succeeded. Also, it is only executable, if the wrapped rule is executable.
     *
     * @param cell the cell
     * @return whether the teleport movement rule is executeable
     */
    @Override
    public boolean executableOn(EvacCellInterface cell) {
        boolean wrappedExecuteable = movementRule.executableOn(cell);
        return cell instanceof TeleportCell ? !((TeleportCell) cell).isTeleportFailed() && wrappedExecuteable : wrappedExecuteable;
    }

    @Override
    public Optional<MoveAction> execute(EvacCellInterface cell) {
        return movementRule.execute(cell);
    }

    @Override
    public List<EvacCellInterface> getPossibleTargets() {
        return movementRule.getPossibleTargets();
    }

    @Override
    public EvacCellInterface selectTargetCell(EvacCellInterface cell, List<EvacCellInterface> targets) {
        return movementRule.selectTargetCell(cell, targets);
    }

    @Override
    public boolean isDirectExecute() {
        return movementRule.isDirectExecute();
    }

    @Override
    public void setDirectExecute(boolean directExecute) {
        movementRule.setDirectExecute(directExecute);
    }

    @Override
    public boolean isMoveCompleted() {
        return movementRule.isMoveCompleted();
    }

    @Override
    public SwapAction swap(EvacCellInterface cell1, EvacCellInterface cell2) {
        return movementRule.swap(cell1, cell2);
    }

    @Override
    public MoveAction move(EvacCellInterface from, EvacCellInterface target) {
        return movementRule.move(from, target);
    }

    @Override
    public void setEvacuationState(EvacuationState es) {
        movementRule.setEvacuationState(es);
    }

    @Override
    public void setComputation(Computation c) {
        movementRule.setComputation(c);
    }

    @Override
    public void setEvacuationSimulationSpeed(EvacuationSimulationSpeed sp) {
        movementRule.setEvacuationSimulationSpeed(sp);
    }

}
