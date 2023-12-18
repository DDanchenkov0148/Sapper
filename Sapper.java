import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;
import javax.swing.*;

public class Sapper extends JFrame {
    private JButton[][] buttons; // двумерный массив для создания кнопок
    private boolean[][] mine;  // двумерный массив логических значений для бомб
    private int[][] countMines; // числовой массив для хранения количества мин, окружащих каждую ячейку
    private int countCellsnomines; // число клеток на игровом поле, в которых не будет бомбы

    public Sapper() {
        setTitle("Sapper");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new GridLayout(9, 9)); // отрисовка поля 9х9

        buttons = new JButton[9][9]; // двумерный массив кнопок
        mine = new boolean[10][10];
        countMines = new int[10][10];
        countCellsnomines = 0; // изначальное количество кнопок без мин

        // добавление кнопок на игровое поле
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                buttons[i][j] = new JButton();
                buttons[i][j].addActionListener(new CellClickListener(i, j));
                add(buttons[i][j]);
            }
        }

        placeMines();
        countSurroundingMines();
        pack();
        setVisible(true);
    }

    // метод расположения мин
    private void placeMines() {
        Random random = new Random();
        int placedMines = 0;
        while (placedMines < 10) {
            int i = random.nextInt(9);
            int j = random.nextInt(9);
            if (!mine[i][j]) {
                mine[i][j] = true;
                placedMines++;
            }
        }
    }

    // проход по всему игровому полю для обнаружения мин и формирование значений для безопасных клеток
    private void countSurroundingMines() {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (!mine[i][j]) {
                    int count = 0;
                    if (i > 0 && mine[i - 1][j]) count++;
                    if (i < 8 && mine[i + 1][j]) count++;
                    if (j > 0 && mine[i][j - 1]) count++;
                    if (j < 8 && mine[i][j + 1]) count++;
                    if (i > 0 && j > 0 && mine[i - 1][j - 1]) count++;
                    if (i < 8 && j < 8 && mine[i + 1][j + 1]) count++;
                    if (i > 0 && j < 8 && mine[i - 1][j + 1]) count++;
                    if (i < 8 && j > 0 && mine[i + 1][j - 1]) count++;
                    countMines[i][j] = count;
                }
            }
        }
    }

    // заполнение пустых клеток и фиксация попадания на бомбу
    private void uncoverCell(int i, int j) {
        if (mine[i][j]) {
            loseGame();
        } else {
            buttons[i][j].setText(Integer.toString(countMines[i][j]));
            buttons[i][j].setEnabled(false);
            countCellsnomines++;
            if (countCellsnomines == 71) {
                winGame();
            }  // если будет выбрана 71 клетка и ты остаёшься в игре, автоматически ты победил, так как 81 (поле 9х9) - 10 (количество мин) = 71 безопасная клетка

            if (countMines[i][j] == 0) {
                uncoverSurroundingCells(i, j);
            }
        }
    }

    // заполнение пустых клеток числами, соответствующими количеству соседних бомб
    private void uncoverSurroundingCells(int i, int j) {
        if (i > 0 && buttons[i - 1][j].isEnabled()) uncoverCell(i - 1, j);
        if (i < 8 && buttons[i + 1][j].isEnabled()) uncoverCell(i + 1, j);
        if (j > 0 && buttons[i][j - 1].isEnabled()) uncoverCell(i, j - 1);
        if (j < 8 && buttons[i][j + 1].isEnabled()) uncoverCell(i, j + 1);
        if (i > 0 && j > 0 && buttons[i - 1][j - 1].isEnabled()) uncoverCell(
                i - 1,
                j - 1
        );
        if (i < 8 && j < 8 && buttons[i + 1][j + 1].isEnabled()) uncoverCell(
                i + 1,
                j + 1
        );
        if (i > 0 && j < 8 && buttons[i - 1][j + 1].isEnabled()) uncoverCell(
                i - 1,
                j + 1
        );
        if (i < 8 && j > 0 && buttons[i + 1][j - 1].isEnabled()) uncoverCell(
                i + 1,
                j - 1
        );
    }

    // появление сообщения о победе
    private void winGame() {
        JOptionPane.showMessageDialog(this, "You won!");
        System.exit(0);
    }

    // реализация случая поражения: клетка приобретает значение картинки с бомбой + сообщение "Ты проиграл"
    private void loseGame() {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (mine[i][j]) {
                    buttons[i][j].setIcon(new ImageIcon("320x0w.png")); // клетка с бомбой становится картинкой
                }
                buttons[i][j].setEnabled(false);
            }
        }
        JOptionPane.showMessageDialog(this, "You lose.");
        System.exit(0);
    }

    // метод фиксании нажатия мышкой и их отображения
    private class CellClickListener implements ActionListener {
        private int i;
        private int j;

        public CellClickListener(int i, int j) {
            this.i = i;
            this.j = j;
        }

        public void actionPerformed(ActionEvent e) {
            uncoverCell(i, j);
        }
    }
}