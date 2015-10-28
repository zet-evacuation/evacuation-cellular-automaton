package org.zetool.simulation.cellularautomaton.gameoflife;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class UnboundedUniverse extends Universe {

    public UnboundedUniverse(int width, int height) {
        super(width, height);
    }

    @Override
    public boolean existsCellAt(int x, int y) {
        if ((x < -1) || (x > (this.getWidth() ))) {
            return false;
        } else if ((y < -1) || (y > (this.getHeight() ))) {
            return false;
        } else if (this.getCell(x, y) == null) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public SimpleCell getCell(int x, int y) throws IllegalArgumentException {
        return super.getCell(getNewX(x), getNewY(y));
    }

    private int getNewX(int x) {
        if ((x < -1) || (x > this.getWidth())) {
            throw new IllegalArgumentException("Invalid x-value!");
        } else if (x == -1) {
            return this.getWidth() - 1;
        } else if (x == this.getWidth()) {
            return 0;
        }
        return x;
    }

    private int getNewY(int y) {
        if ((y < -1) || (y > this.getHeight())) {
            throw new IllegalArgumentException("Invalid y-value!");
        } else if (y == -1) {
            return this.getHeight() - 1;
        } else if (y == this.getHeight()) {
            return 0;
        }
        return y;
    }

}
