package org.example.juegofinalsupremo.model;

public class Room {
    private final int rows;
    private final int columns;
    private final Cell[][] cells;

    public Room(int rows, int columns) {
        if (rows <= 0 || columns <= 0) {
            throw new IllegalArgumentException("La habitacion debe tener dimensiones positivas");
        }
        this.rows = rows;
        this.columns = columns;
        this.cells = new Cell[rows][columns];
        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                cells[row][column] = new Cell(new Position(row, column));
            }
        }
    }

    public int getRows() {
        return rows;
    }

    public int getColumns() {
        return columns;
    }

    public boolean isValid(Position position) {
        return position != null
                && position.getRow() >= 0
                && position.getRow() < rows
                && position.getColumn() >= 0
                && position.getColumn() < columns;
    }

    public Cell getCell(Position position) {
        if (!isValid(position)) {
            throw new IllegalArgumentException("Celda invalida: " + position);
        }
        return cells[position.getRow()][position.getColumn()];
    }
}
