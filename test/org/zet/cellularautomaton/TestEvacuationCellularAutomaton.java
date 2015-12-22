package org.zet.cellularautomaton;

import java.util.Collections;
import java.util.List;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.jmock.AbstractExpectations.returnValue;
import org.jmock.Expectations;
import org.jmock.Mockery;
import static org.junit.Assert.assertThat;
import org.junit.Test;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class TestEvacuationCellularAutomaton {
    private final Mockery context = new Mockery();

    @Test
    public void testInitialization() {
        EvacuationCellularAutomaton ca = new EvacuationCellularAutomaton();
        assertThat(ca.getCellCount(), is(equalTo(0)));
        assertThat(ca.getDimension(), is(equalTo(2)));
        assertThat(ca.getFloors(), is(empty()));
        assertThat(ca.getIndividualCount(), is(equalTo(0)));
        assertThat(ca.getIndividuals(), is(empty()));
        assertThat(ca.getInitialIndividualCount(), is(equalTo(0)));
        assertThat(ca.getRooms(), is(empty()));
        assertThat(ca.getState(), is(equalTo(EvacuationCellularAutomaton.State.READY)));
        //assertThat(ca.getTimeStep(), is(equalTo(0)));
        assertThat(ca.graphicalToString(), is(equalTo("")));
    }
    
    @Test
    public void testSingleRoom() {
        EvacuationCellularAutomaton ca = new EvacuationCellularAutomaton();
        ca.addFloor("floor1");

        Room room = context.mock(Room.class, "room1");
        roomExpectations(room, 1, 3);
        Room room2 = context.mock(Room.class, "room2");
        roomExpectations(room2, 2, 2);

        ca.addRoom(room);
        assertThat(ca.getCellCount(), is(equalTo(3)));
        
        ca.addRoom(room2);
        assertThat(ca.getCellCount(), is(equalTo(5)));
    }
    
    private void roomExpectations(Room room, int id, int cells) {
        context.checking(new Expectations() {{
                //debugger throws error on the line below.
                atLeast(1).of(room).getCellCount(false);
                will(returnValue(cells));
                allowing(room).getID();
                will(returnValue(id));
                allowing(room).getFloorID();
                will(returnValue(0));
                allowing(room).getAllCells();
                will(returnValue(Collections.EMPTY_LIST));
        }});
    }
    
    @Test
    public void testRemoveIndividuals() {
        EvacuationCellularAutomaton eca = new EvacuationCellularAutomaton();
        Individual toEvacuate = new Individual();
        Individual notToEvacuate = new Individual();
        notToEvacuate.setNumber(1);
        
        eca.addFloor("floor1");
        Room room = context.mock(Room.class, "room1");
        roomExpectations(room, 1, 3);
        eca.addRoom(room);

        ExitCell cell = new ExitCell(0, 0);
        cell.setRoom(room);
        ExitCell cell2 = new ExitCell(1,1);
        cell2.setRoom(room);
        context.checking(new Expectations() {{
            allowing(room).addIndividual(cell, toEvacuate);
            allowing(room).addIndividual(cell2, notToEvacuate);
            allowing(room).removeIndividual(toEvacuate);
        }});
        
        eca.addIndividual(cell, toEvacuate);
        toEvacuate.setCell(cell);

        eca.addIndividual(cell2, notToEvacuate);

        eca.markIndividualForRemoval(toEvacuate);
        eca.removeMarkedIndividuals();
        assertThat(eca.getIndividuals(), not(hasItem(toEvacuate)));
        assertThat(eca.getIndividuals(), hasItem(notToEvacuate));
    }
    
    @Test
    public void remainingIndividuals() {
        EvacuationCellularAutomaton eca = new EvacuationCellularAutomaton();
        Individual activeIndividual = new Individual();
        Individual evacuatedIndividual = new Individual();
        evacuatedIndividual.setNumber(1);
        Individual saveIndividual = new Individual();
        saveIndividual.setNumber(2);
        Individual deadIndividual = new Individual();
        deadIndividual.setNumber(3);

        Room room = context.mock(Room.class, "room1");
        context.checking(new Expectations() {{
            allowing(room).addIndividual(with(any(RoomCell.class)), with(any(Individual.class)));
            allowing(room).addIndividual(with(any(ExitCell.class)), with(any(Individual.class)));
            allowing(room).getID();
            will(returnValue(1));
            allowing(room).removeIndividual(with(evacuatedIndividual));
            allowing(room).removeIndividual(with(deadIndividual));
        }});
        RoomCell cell1 = new RoomCell(1, 0, 1, room);
        eca.addIndividual(cell1, activeIndividual);
        ExitCell cell2 = new ExitCell(1, 0, 2, room);
        eca.addIndividual(cell2, evacuatedIndividual);
        RoomCell cell3 = new RoomCell(1, 0, 3, room);
        eca.addIndividual(cell3, saveIndividual);
        RoomCell cell4 = new RoomCell(1, 0, 4, room);
        eca.addIndividual(cell4, deadIndividual);
        
        eca.setIndividualSave(saveIndividual);
        evacuatedIndividual.setCell(cell2);
        eca.setIndividualEvacuated(evacuatedIndividual);
        deadIndividual.setCell(cell4);
        eca.setIndividualDead(deadIndividual, DeathCause.NOT_ENOUGH_TIME);
        
        assertThat(eca.getRemainingIndividuals(), contains(activeIndividual, saveIndividual));
        assertThat(eca.getEvacuatedIndividuals(), contains(evacuatedIndividual));
        assertThat(eca.getDeadIndividuals(), contains(deadIndividual));
    }
}
