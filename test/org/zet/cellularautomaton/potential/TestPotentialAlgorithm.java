package org.zet.cellularautomaton.potential;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.closeTo;
import static org.junit.Assert.assertThat;

import java.util.LinkedList;
import java.util.List;
import org.junit.Test;

import org.zet.cellularautomaton.EvacCell;
import org.zet.cellularautomaton.ExitCell;
import org.zet.cellularautomaton.RoomCell;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class TestPotentialAlgorithm {

    private static class MockExitCell extends ExitCell {

        List<EvacCell> neighbors = new LinkedList<>();

        public MockExitCell(int x, int y) {
            super(x, y);
        }

        @Override
        public List<EvacCell> getNeighbours() {
            return neighbors;
        }

        void addNeighbor(EvacCell cell) {
            neighbors.add(cell);
        }
    }

    private static class MockCell extends RoomCell {

        List<EvacCell> neighbors = new LinkedList<>();

        public MockCell(int x, int y) {
            super(x, y);
        }

        @Override
        public List<EvacCell> getNeighbours() {
            return neighbors;
        }

        void addNeighbor(EvacCell cell) {
            neighbors.add(cell);
        }

    }

    @Test
    public void testSingleExit() {
        PotentialAlgorithm algorithm = new PotentialAlgorithm();

        MockExitCell c = new MockExitCell(1, 1);

        List<ExitCell> exitBlock = new LinkedList<>();
        exitBlock.add(c);

        algorithm.setProblem(exitBlock);

        StaticPotential sp = algorithm.createStaticPotential(exitBlock);

        assertThat(sp.getPotential(c), is(equalTo(0)));
        assertThat(sp.getDistance(c), is(closeTo(0.0, 10e-8)));
    }

    @Test
    public void testNeighborCells() {
        PotentialAlgorithm algorithm = new PotentialAlgorithm();

        MockExitCell c = new MockExitCell(1, 1);
        MockCell neighbour = new MockCell(0, 1);

        c.addNeighbor(neighbour);
        neighbour.addNeighbor(c);

        List<ExitCell> exitBlock = new LinkedList<>();
        exitBlock.add(c);

        algorithm.setProblem(exitBlock);

        StaticPotential sp = algorithm.createStaticPotential(exitBlock);

        assertThat(sp.getPotential(c), is(equalTo(0)));
        assertThat(sp.getDistance(c), is(closeTo(0.0, 10e-8)));

        assertThat(sp.getPotential(neighbour), is(equalTo(8)));
        assertThat(sp.getDistance(neighbour), is(closeTo(0.4, 10e-8)));
    }

    @Test
    public void testTwoNeighborCells() {
        PotentialAlgorithm algorithm = new PotentialAlgorithm();

        MockExitCell c = new MockExitCell(1, 1);
        MockCell diagonalNeighbour = new MockCell(0, 0);
        MockCell leftNeighbour = new MockCell(0, 1);

        c.addNeighbor(diagonalNeighbour);
        c.addNeighbor(leftNeighbour);

        diagonalNeighbour.addNeighbor(c);
        diagonalNeighbour.addNeighbor(leftNeighbour);

        leftNeighbour.addNeighbor(c);
        leftNeighbour.addNeighbor(diagonalNeighbour);

        List<ExitCell> exitBlock = new LinkedList<>();
        exitBlock.add(c);

        algorithm.setProblem(exitBlock);

        StaticPotential sp = algorithm.createStaticPotential(exitBlock);

        assertThat(sp.getPotential(c), is(equalTo(0)));
        assertThat(sp.getDistance(c), is(closeTo(0.0, 10e-8)));

        assertThat(sp.getPotential(leftNeighbour), is(equalTo(8)));
        assertThat(sp.getDistance(leftNeighbour), is(closeTo(0.4, 10e-8)));

        assertThat(sp.getPotential(diagonalNeighbour), is(equalTo(11)));
        assertThat(sp.getDistance(diagonalNeighbour), is(closeTo(0.565685425, 10e-8)));
    }

    @Test
    public void testNeighborExitCells() {
        PotentialAlgorithm algorithm = new PotentialAlgorithm();

        MockExitCell c = new MockExitCell(1, 1);
        MockExitCell neighbourExit = new MockExitCell(0, 1);

        c.addNeighbor(neighbourExit);
        neighbourExit.addNeighbor(c);

        List<ExitCell> exitBlock = new LinkedList<>();
        exitBlock.add(c);

        algorithm.setProblem(exitBlock);

        StaticPotential sp = algorithm.createStaticPotential(exitBlock);

        assertThat(sp.getPotential(c), is(equalTo(0)));
        assertThat(sp.getDistance(c), is(closeTo(0.0, 10e-8)));

        assertThat(sp.hasValidPotential(neighbourExit), is(false));
    }
}
