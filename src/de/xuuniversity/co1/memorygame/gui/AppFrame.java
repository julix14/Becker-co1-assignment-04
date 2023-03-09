package de.xuuniversity.co1.memorygame.gui;

import de.xuuniversity.co1.memorygame.dataservices.HighscoreService;
import de.xuuniversity.co1.memorygame.helper.GameTimer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AppFrame extends JFrame {
    private final Set<JButton> selectedButtons = new HashSet<>();
    private final Set<JButton> revealedButtons = new HashSet<>();
    private final List<JButton> buttonsToCover = new ArrayList<>();
    private GameTimer gameTimer = null;
    private int score = 0;
    private JLabel scoreLabel = null;
    private JLabel highscoreLabel = null;
    private final JPanel rootPanel = new JPanel(new BorderLayout());
    private int currentHighscore = HighscoreService.getCurrentHighscore();
    private boolean highscoreBeaten = false;

    public AppFrame() {
        super("Memory Game");

        int width = 600;
        int height = 450;
        this.setSize(width, height);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        int x = (int) ((screenSize.getWidth() - width) / 2);
        int y = (int) ((screenSize.getHeight() - height) / 2);
        this.setLocation(x, y);

        this.setResizable(false);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.windowClosing();

        rootPanel.add(createTopPanel(), BorderLayout.NORTH);
        rootPanel.add(createButtonPanel(), BorderLayout.CENTER);
        this.add(rootPanel);
        this.setVisible(true);
    }

    //Methods to create the different layout panels
    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel(new GridLayout(0,3));

        JLabel timeLabel = new JLabel("Time: 00:00");
        timeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        gameTimer = new GameTimer(timeLabel);

        JLabel currentScore = new JLabel("Score: " + this.score);
        currentScore.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.scoreLabel = currentScore;

        JLabel highscore = new JLabel("Highscore: " + currentHighscore);
        highscore.setAlignmentX(Component.RIGHT_ALIGNMENT);
        this.highscoreLabel = highscore;

        topPanel.add(timeLabel);
        topPanel.add(currentScore);
        topPanel.add(highscore);

        return topPanel;
    }

    private JPanel createButtonPanel(){
        JPanel buttonPanel = new JPanel(new GridLayout(2, 4));

        //Create the random numbers and assign them to the buttons
        Set<Integer> randomNumbers = this.createRandomNumbers();
        List<JButton> buttons = new ArrayList<>();

        for(Integer number : randomNumbers){
            for (int i = 0; i < 2; i++) {
                JButton button = new JButton();
                button.setName(number.toString());
                button.addActionListener(this::revealNumber);
                buttons.add(button);
            }
        }

        //Add the buttons to the panel in a random order
        while (!buttons.isEmpty()) {
            int randomIndex = (int) (Math.random() * buttons.size());
            buttonPanel.add(buttons.get(randomIndex));
            buttons.remove(randomIndex);
        }

        return buttonPanel;
    }


    //Helper methods
    private Set<Integer> createRandomNumbers() {
        Set<Integer> randomNumbers = new HashSet<>();

        while (randomNumbers.size() < 4) {
            int randomNumber = (int) (Math.random() * 100);
            randomNumbers.add(randomNumber);
        }

        return randomNumbers;
    }

    private void revealNumber(ActionEvent e){
        gameTimer.start();
        JButton clickedButton = (JButton) e.getSource();

        //If no button is selected, cover the previously selected buttons and select the clicked button
        if(selectedButtons.isEmpty()){
            for (JButton button : buttonsToCover) {
                button.setText("");
            }

            buttonsToCover.clear();
            clickedButton.setText(clickedButton.getName());
            selectedButtons.add(clickedButton);
            return;
        }

        //If one button is selected, check if the clicked button is the same as the selected button
        //After this check if the name of the clicked button is the same as the name of the selected button
        if(selectedButtons.size() == 1 && !selectedButtons.contains(clickedButton)){
            clickedButton.setText(clickedButton.getName());
            JButton firstButton = selectedButtons.iterator().next();

            if(firstButton.getName().equals(clickedButton.getName())){
                firstButton.setEnabled(false);
                clickedButton.setEnabled(false);
                revealedButtons.add(firstButton);
                revealedButtons.add(clickedButton);
                score += 10;
                scoreLabel.setText("Score: " + score);
                selectedButtons.clear();

            }else{
                buttonsToCover.add(firstButton);
                buttonsToCover.add(clickedButton);
                selectedButtons.clear();

                if(score > 0){
                    score -= 5;
                    scoreLabel.setText("Score: " + score);
                }
            }
        }


        if(revealedButtons.size() == 8){
            gameFinished();
        }
    }

    private void gameFinished(){
        gameTimer.stop();
        String message;

        if(score > currentHighscore) {
            message = "You won and beat the Highscore! \nYour score is " + score + " points. \nDo you want to play again?";
            currentHighscore = score;
            highscoreBeaten = true;

        }else{
            message = "You won! Your score is " + score + " points. \nDo you want to play again?";
        }

        int result = JOptionPane.showConfirmDialog(
                this, message, "You won!",
                JOptionPane.YES_NO_OPTION);

        if(result == JOptionPane.YES_OPTION){
            resetGame();

        }else if(result == JOptionPane.NO_OPTION){
            this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
        }
    }

    private void resetGame(){
        revealedButtons.clear();
        selectedButtons.clear();
        score = 0;
        rootPanel.remove(1);
        rootPanel.add(createButtonPanel(), BorderLayout.CENTER);
        scoreLabel.setText("Score: " + score);
        highscoreLabel.setText("Highscore: " + currentHighscore);
        gameTimer.stop();
        gameTimer.reset();
    }

    private void windowClosing(){
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if(highscoreBeaten) {
                    int result = JOptionPane.showConfirmDialog(
                            null, "Do you want to save your Highscore?", "Exit",
                            JOptionPane.YES_NO_OPTION);

                    if(result == JOptionPane.YES_OPTION){
                        System.out.println("Highscore was saved - Goodbye!");
                        HighscoreService.saveHighscore(currentHighscore);
                    }else{
                        System.out.println("No Highscore was saved - Goodbye!");
                    }

                    System.exit(0);

                }
            }
        });
    }






}
