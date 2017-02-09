package org.zet.cellularautomaton;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.jmock.AbstractExpectations.returnValue;
import static org.zet.cellularautomaton.EvacuationCellularAutomatonBuilderTest.roomWithTwoExits;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Test;
import org.zet.cellularautomaton.MultiFloorEvacuationCellularAutomaton.EvacuationCellularAutomatonBuilder;
import org.zet.cellularautomaton.potential.StaticPotential;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class MultiFloorEvacuationCellularAutomatonTest {

    private final Mockery context = new Mockery();

    @Test
    public void testInitialization() {
        MultiFloorEvacuationCellularAutomaton ca = new MultiFloorEvacuationCellularAutomaton();
        assertThat(ca.getCellCount(), is(equalTo(0)));
        assertThat(ca.getDimension(), is(equalTo(2)));
        assertThat(ca.getFloors(), is(empty()));
        assertThat(ca.getRooms(), is(empty()));
        assertThat(ca.graphicalToString(), is(equalTo("")));
    }

    @Test
    public void testSingleRoom() {
        EvacuationCellularAutomatonBuilder builder = new EvacuationCellularAutomatonBuilder();

        builder.addFloor(0, "floor1");

        Room room = context.mock(Room.class, "room1");
        roomExpectations(room, 1, 3);
        Room room2 = context.mock(Room.class, "room2");
        roomExpectations(room2, 2, 2);

        builder.addRoom(0, room);
        MultiFloorEvacuationCellularAutomaton ca = builder.build();
        assertThat(ca.getCellCount(), is(equalTo(3)));

        builder.addRoom(0, room2);
        ca = builder.build();
        assertThat(ca.getCellCount(), is(equalTo(5)));
    }

    private void roomExpectations(Room room, int id, int cells) {
        context.checking(new Expectations() {
            {
                atLeast(1).of(room).getCellCount(false);
                will(returnValue(cells));
                allowing(room).getXOffset();
                allowing(room).getYOffset();
                allowing(room).getWidth();
                allowing(room).getHeight();
                allowing(room).getID();
                will(returnValue(id));
                allowing(room).getFloorID();
                will(returnValue(0));
                allowing(room).getAllCells();
                will(returnValue(Collections.EMPTY_LIST));
            }
        });
    }

    @Test
    public void minPotential() {
        EvacuationCellularAutomatonBuilder builder = new EvacuationCellularAutomatonBuilder();

        builder.addFloor(0, "floor1");
        Room r = roomWithTwoExits();
        Collection<Exit> newExits = builder.addRoom(0, r);

        StaticPotential p = new StaticPotential();

        p.setPotential(r.getCell(0, 0), 0);
        p.setPotential(r.getCell(0, 1), 1);
        p.setPotential(r.getCell(0, 2), 2);
        p.setPotential(r.getCell(0, 3), 3);
        p.setPotential(r.getCell(1, 0), 1);
        p.setPotential(r.getCell(1, 1), 1);
        p.setPotential(r.getCell(1, 2), 2);
        p.setPotential(r.getCell(1, 3), 3);
        p.setPotential(r.getCell(2, 0), 2);
        p.setPotential(r.getCell(2, 1), 2);
        p.setPotential(r.getCell(2, 2), 2);
        p.setPotential(r.getCell(2, 3), 3);
        p.setPotential(r.getCell(3, 0), 3);
        p.setPotential(r.getCell(3, 1), 3);
        p.setPotential(r.getCell(3, 2), 3);
        p.setPotential(r.getCell(3, 3), 3);

        StaticPotential p1 = new StaticPotential();
        p1.setPotential(r.getCell(0, 0), 2);
        p1.setPotential(r.getCell(0, 1), 2);
        p1.setPotential(r.getCell(0, 2), 2);
        p1.setPotential(r.getCell(0, 3), 2);
        p1.setPotential(r.getCell(1, 0), 2);
        p1.setPotential(r.getCell(1, 1), 1);
        p1.setPotential(r.getCell(1, 2), 1);
        p1.setPotential(r.getCell(1, 3), 1);
        p1.setPotential(r.getCell(2, 0), 2);
        p1.setPotential(r.getCell(2, 1), 1);
        p1.setPotential(r.getCell(2, 2), 0);
        p1.setPotential(r.getCell(2, 3), 0);
        p1.setPotential(r.getCell(3, 0), 2);
        p1.setPotential(r.getCell(3, 1), 1);
        p1.setPotential(r.getCell(3, 2), 0);
        p1.setPotential(r.getCell(3, 3), 0);

        Iterator<Exit> ei = newExits.iterator();
        Exit e1 = ei.next();
        Exit smallExit;
        Exit bigExit;
        if (e1.getExitCluster().size() == 1) {
            smallExit = e1;
            bigExit = ei.next();
        } else {
            smallExit = ei.next();
            bigExit = e1;
        }

        builder.setPotentialFor(smallExit, p);
        builder.setPotentialFor(bigExit, p1);

        MultiFloorEvacuationCellularAutomaton ca = builder.build();
        assertThat(ca.minPotentialFor(r.getCell(0, 0)), is(sameInstance(p)));

    }

    @Test(expected = IllegalArgumentException.class)
    public void noPotentialFails() {
        EvacuationCellularAutomatonBuilder builder = new EvacuationCellularAutomatonBuilder();

        builder.addFloor(0, "floor1");
        Room r = roomWithTwoExits();
        Collection<Exit> newExits = builder.addRoom(0, r);

        newExits.forEach(exit -> builder.setPotentialFor(exit, new StaticPotential()));

        MultiFloorEvacuationCellularAutomaton ca = builder.build();
        assertThat(ca.minPotentialFor(r.getCell(0, 0)), is(equalTo(1)));
    }

    @Test
    public void assertGraphicalRepresentation() {
        EvacuationCellularAutomatonBuilder builder = new EvacuationCellularAutomatonBuilder();

        builder.addFloor(0, "floor1");
        Room r = roomWithTwoExits();

        builder.addRoom(0, r);

        MultiFloorEvacuationCellularAutomaton ca = builder.build();
        String result = ca.graphicalToString();

        int roomHeight = 4;
        List<String> lines = Arrays.asList(result.split("\n"));
        assertThat(lines.size(), is(equalTo(roomHeight * 2 + 1)));
        String emptyExit = "# #";
        assertThat(lines, hasItem("┌───┬───┬───┬───┐"));
        assertThat(lines, hasItem("│" + emptyExit + "│   │   │   │"));
        assertThat(lines, hasItem("├───┼───┼───┼───┤"));
        assertThat(lines, hasItem("│   │   │   │   │"));
        assertThat(lines, hasItem("├───┼───┼───┼───┤"));
        assertThat(lines, hasItem("│   │   │" + emptyExit + "│" + emptyExit + "│"));
        assertThat(lines, hasItem("├───┼───┼───┼───┤"));
        assertThat(lines, hasItem("│   │   │" + emptyExit + "│" + emptyExit + "│"));
        assertThat(lines, hasItem("└───┴───┴───┴───┘"));
    }
}
