package org;
import org.example.MagicSquares;
import org.example.SavingMagicSquares;
import org.junit.jupiter.api.Test;
import javax.swing.table.DefaultTableModel;
import java.io.*;

import static org.junit.jupiter.api.Assertions.*;

class MagicSquaresTest {

    @Test
    void testValidateMagicSquare() {
        MagicSquares magicGame = new MagicSquares();
        int size = 3;
        magicGame.currentMagicSum = 15; // sets the expected magic sum

        DefaultTableModel model = new DefaultTableModel(size, size); //creates a 3x3 magic squares board
        // setting value one by one
        model.setValueAt(8, 0, 0);
        model.setValueAt(1, 0, 1);
        model.setValueAt(6, 0, 2);
        model.setValueAt(3, 1, 0);
        model.setValueAt(5, 1, 1);
        model.setValueAt(7, 1, 2);
        model.setValueAt(4, 2, 0);
        model.setValueAt(9, 2, 1);
        model.setValueAt(2, 2, 2);

        magicGame.model = model;

        assertTrue(magicGame.validateMagicSquare(size, magicGame.currentMagicSum), "Valid magic square");

        // Modify one cell to make it invalid
        model.setValueAt(99, 0, 0);

        assertFalse(magicGame.validateMagicSquare(size, magicGame.currentMagicSum), "Invalid magic square");
    }

    @Test
    void testSerializationAndDeserialization() throws IOException, ClassNotFoundException {

        MagicSquares magicGame = new MagicSquares();
        int size = 3;
        magicGame.currentMagicSum = 30;
        magicGame.score = 10;

        //fills values in 3x3 magic square in attempt to store it
        DefaultTableModel model = new DefaultTableModel(size, size);
        model.setValueAt(8, 0, 0);
        model.setValueAt(1, 0, 1);
        model.setValueAt(6, 0, 2);
        model.setValueAt(3, 1, 0);
        model.setValueAt(5, 1, 1);
        model.setValueAt(7, 1, 2);
        model.setValueAt(4, 2, 0);
        model.setValueAt(9, 2, 1);
        model.setValueAt(2, 2, 2);
        magicGame.model = model;

        File file = new File("testMagicSquare.ser"); //saving in binary file format
        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file));
        SavingMagicSquares state = new SavingMagicSquares(model, magicGame.score, magicGame.currentMagicSum);
        out.writeObject(state);
        out.close();

        // deserialize the saved magic square
        ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
        SavingMagicSquares loadedState = (SavingMagicSquares) in.readObject();
        in.close();

        // Validate loaded state
        assertEquals(magicGame.score, loadedState.getScore(), "Score mismatch after deserialization");
        assertEquals(magicGame.currentMagicSum, loadedState.getMagicSum(), "Magic sum mismatch after deserialization");

        //filling up the grid
        Object[][] loadedGrid = loadedState.getGridData();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                assertEquals(model.getValueAt(i, j), loadedGrid[i][j], "Grid data mismatch at (" + i + ", " + j + ")");
            }
        }

        file.delete(); //throws an IO exception when the file cannot be deleted
    }

}


