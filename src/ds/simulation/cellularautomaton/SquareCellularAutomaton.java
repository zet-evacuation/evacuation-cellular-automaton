/**
 * SquareCellularAutomaton.java
 * Created: 25.10.2012, 15:00:09
 */
package ds.simulation.cellularautomaton;


/**
 * @param <Ce> cell type
 * @param <St> status type
 * @author Jan-Philipp Kappmeier
 */
public abstract class SquareCellularAutomaton<Ce extends SquareCell<Ce,St>,St> implements CellularAutomaton<Ce, St> {
	protected MooreNeighborhoodSquare<Ce> neighborhood;

	public SquareCellularAutomaton() {
		neighborhood = new MooreNeighborhoodSquare<>();
	}

	@Override
	public int getDimension() {
		return 2;
	}
}
