package org.example;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.io.*;
import java.util.Random; //to generate random numbers for grid filling and generating magic square sum

public class MagicSquares {
    private JFrame frame;
    private JTable magicSquare;
    private JMenuBar menuBar;
    public DefaultTableModel model;
    public int score = 0;
    public int currentMagicSum = 0; // Track the magic sum for the current puzzle


    public MagicSquares() {
        frame = new JFrame("Magic Square Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 300);
        model = new DefaultTableModel(3, 3);
        magicSquare = new JTable(model);
        magicSquare.setRowHeight(50);
        JScrollPane scrollPane = new JScrollPane(magicSquare);
        frame.add(scrollPane);
        menuBar = new JMenuBar();
        //creating objects
        JMenu fileMenu = new JMenu("File");
        JMenuItem newPuzzle = new JMenuItem("New Puzzle");
        JMenuItem validatePuzzle = new JMenuItem("Validate Puzzle");
        JMenuItem exit = new JMenuItem("Exit");
        JMenuItem savePuzzle = new JMenuItem("Save Puzzle");
        JMenuItem loadPuzzle = new JMenuItem("Load Puzzle");

        //adding the menu items to the menu
        fileMenu.add(newPuzzle);
        fileMenu.add(validatePuzzle);
        fileMenu.add(savePuzzle);
        fileMenu.add(loadPuzzle);
        fileMenu.add(exit);
        menuBar.add(fileMenu);

        frame.setJMenuBar(menuBar);

        //action listeners calls the methods when they are clicked
        newPuzzle.addActionListener(e -> newPuzzle());
        validatePuzzle.addActionListener(e -> handleValidatePuzzle());
        savePuzzle.addActionListener(e -> savePuzzleState());
        loadPuzzle.addActionListener(e -> loadPuzzleState());
        exit.addActionListener(e -> System.exit(0));

        frame.setVisible(true);
    }

    private void newPuzzle() {
        String gridSizeString = JOptionPane.showInputDialog(frame, "Enter grid size (odd only, e.g., 3 for 3x3):");
        try {
            int gridSize = Integer.parseInt(gridSizeString);
            if (gridSize < 3 || gridSize % 2 == 0) {
                JOptionPane.showMessageDialog(frame, "Grid size must be an odd number greater than or equal to 3!");
                return;
            }

            model.setRowCount(gridSize);
            model.setColumnCount(gridSize);
            generateValidMagicSquare(gridSize);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(frame, "Invalid grid size! Please enter a valid number.");
        }
    }

    // Function to generate a valid magic square using the Siamese method
    public int[][] generateValidMagicSquare(int size) {

        if (size % 2 == 0) {
            JOptionPane.showMessageDialog(frame, "Magic square generation is currently supported for odd sizes only.");
            return null;
        }

        int[][] magicSquare = new int[size][size];
        int number = 1;
        int row = 0;
        int col = size / 2;

        while (number <= size * size) {
            magicSquare[row][col] = number;
            number++;

            // Calculate the next position
            int newRow = (row - 1 + size) % size;
            int newCol = (col + 1) % size;

            if (magicSquare[newRow][newCol] != 0) {
                // If the position is already filled, move down instead
                newRow = (row + 1) % size;
                newCol = col;
            }

            row = newRow;
            col = newCol;
        }

        Random random = new Random();
        int multiplier = random.nextInt(5) + 1; // using random multiplier between 1 and 5 to make magic sum varry every time
        currentMagicSum = size * (size * size + 1) / 2 * multiplier; //
        updateWindowTitle();


        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                magicSquare[i][j] = magicSquare[i][j] * multiplier;
            }
        }

        int magicSum = size * (size * size + 1) / 2 * multiplier; // Adjust the magic sum
        createPuzzleFromMagicSquare(magicSquare, size);

        JOptionPane.showMessageDialog(frame, "New puzzle generated! The required Magic Sum is: " + magicSum);
        return magicSquare;
    }

    private void createPuzzleFromMagicSquare(int[][] magicSquare, int size) {
        Random random = new Random();

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (random.nextBoolean()) {
                    model.setValueAt(null, i, j);
                } else {
                    model.setValueAt(magicSquare[i][j], i, j); //had to do this because values were disappearing
                }
            }
        }
    }

    public boolean validateMagicSquare(int size, int magicSum) {
        for (int i = 0; i < size; i++) {
            int rowSum = 0;
            for (int j = 0; j < size; j++) {
                Object value = model.getValueAt(i, j);
                if (value == null || !isValidInteger(value)) {
                    return false;
                }
                rowSum = rowSum + Integer.parseInt(value.toString());
            }
            if (rowSum != magicSum) {
                return false;
            }
        }

        for (int j = 0; j < size; j++) {
            int colSum = 0;
            for (int i = 0; i < size; i++) {
                Object value = model.getValueAt(i, j);
                if (value == null || !isValidInteger(value)) {
                    return false;
                }
                colSum = colSum + Integer.parseInt(value.toString());
            }
            if (colSum != magicSum) return false;
        }

        int primaryDiagonalSum = 0;
        for (int i = 0; i < size; i++) {
            Object value = model.getValueAt(i, i);
            if (value == null || !isValidInteger(value)) {
                return false;
            }
            primaryDiagonalSum = primaryDiagonalSum + Integer.parseInt(value.toString());
        }
        if (primaryDiagonalSum != magicSum) return false;

        int secondaryDiagonalSum = 0;
        for (int i = 0; i < size; i++) {
            Object value = model.getValueAt(i, size - i - 1);
            if (value == null || !isValidInteger(value)) {
                return false;
            }
            secondaryDiagonalSum = secondaryDiagonalSum + Integer.parseInt(value.toString());
        }
        if (secondaryDiagonalSum != magicSum){
            return false;
        }

        return true;
    }

    private void handleValidatePuzzle() {
        int size = model.getRowCount();

        if (validateMagicSquare(size, currentMagicSum)) { // Use the dynamic magic sum
            score = score + 5;
            updateWindowTitle();
            JOptionPane.showMessageDialog(frame, "Congratulations! You solved the magic square!");
        } else {
            JOptionPane.showMessageDialog(frame, "Incorrect solution. Try again!");
        }
    }

    private boolean isValidInteger(Object value) {
        try {
            Integer.parseInt(value.toString());
            return true;
        } catch (NumberFormatException e) { //the string should be a number
            return false;
        }
    }

    private void updateWindowTitle() {
        frame.setTitle("Magic Square Game - Score: " + score + " | Magic Sum: " + currentMagicSum);
    }

    private void savePuzzleState() {
        try {
            JFileChooser fileChooser = new JFileChooser();
            int option = fileChooser.showSaveDialog(frame);
            if (option == JFileChooser.APPROVE_OPTION) {
                String filePath = fileChooser.getSelectedFile().getAbsolutePath();

                SavingMagicSquares state = new SavingMagicSquares(model, score,currentMagicSum); // creating an object to store puzzle data

                // serialize the object
                ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filePath));
                out.writeObject(state);
                out.close();

                JOptionPane.showMessageDialog(frame, "Puzzle saved successfully!");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "Error saving puzzle");
        }
    }

    private void loadPuzzleState() {
        try {
            JFileChooser fileChooser = new JFileChooser();
            int option = fileChooser.showOpenDialog(frame);
            if (option == JFileChooser.APPROVE_OPTION) {
                String filePath = fileChooser.getSelectedFile().getAbsolutePath();

                // deserialize the object
                ObjectInputStream in = new ObjectInputStream(new FileInputStream(filePath));
                SavingMagicSquares state = (SavingMagicSquares) in.readObject();
                in.close();

                // putting values back to the grid column by column
                Object[][] gridData = state.getGridData();
                model.setRowCount(gridData.length);
                model.setColumnCount(gridData[0].length);
                for (int i = 0; i < gridData.length; i++) {
                    for (int j = 0; j < gridData[i].length; j++) {
                        model.setValueAt(gridData[i][j], i, j);
                    }
                }

                // restoring the score and magic sum
                score = state.getScore();
                currentMagicSum = state.getMagicSum();
                updateWindowTitle();

                JOptionPane.showMessageDialog(frame, "Puzzle loaded successfully!");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "Error loading puzzle");
        }
    }

}

