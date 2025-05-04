package org.example;
import javax.swing.table.DefaultTableModel;
import java.io.Serializable;

public class SavingMagicSquares implements Serializable {
    private Object[][] gridData;
    private int score;
    private int magicSum; // Add magic sum field

    public SavingMagicSquares(DefaultTableModel model, int score, int magicSum) {
        int rows = model.getRowCount();
        int cols = model.getColumnCount();
        gridData = new Object[rows][cols];

        // Copy grid data from JTable
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                gridData[i][j] = model.getValueAt(i, j);
            }
        }

        this.score = score;
        this.magicSum = magicSum; // Save the magic sum
    }

    //added getter methods for seralizing
    public Object[][] getGridData() {
        return gridData;
    }

    public int getScore() {
        return score;
    }

    public int getMagicSum() {
        return magicSum; // Getter for the magic sum
    }
}
