package org.zet.cellularautomaton;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class TestTeleportCell {
  @Rule
  public ExpectedException exception = ExpectedException.none();
  
  @Test
    public void onlyOneTargetPossible() {
        TeleportCell cell = new TeleportCell(0, 0);
        
        TeleportCell target1 = new TeleportCell(0, 1);
        TeleportCell target2 = new TeleportCell(1, 1);
        
        cell.addTarget(target1);
        exception.expect(IllegalStateException.class);
        cell.addTarget(target2);
    }
}
