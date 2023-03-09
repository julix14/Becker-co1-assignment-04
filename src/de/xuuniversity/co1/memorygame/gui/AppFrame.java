package de.xuuniversity.co1.memorygame.gui;

import de.xuuniversity.co1.memorygame.dataservices.HighscoreService;
import de.xuuniversity.co1.memorygame.helper.GameTimer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class AppFrame extends JFrame {
    private final Set<JButton> selectedButtons = new HashSet<>();
    private final Set<JButton> revealedButtons = new HashSet<>();
    private GameTimer gameTimer = null;
    private int score = 0;
    private JLabel scoreLabel = null;
    private final JPanel rootPanel = new JPanel(new BorderLayout());
    private final int currentHighscore = HighscoreService.getCurrentHighscore();

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
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

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

        JLabel scoreLabel = new JLabel("Score: " + score);
        scoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.scoreLabel = scoreLabel;

        JLabel highscoreLabel = new JLabel("Highscore: " + currentHighscore);
        highscoreLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);

        topPanel.add(timeLabel);
        topPanel.add(scoreLabel);
        topPanel.add(highscoreLabel);

        return topPanel;
    }

    private JPanel createButtonPanel(){
        JPanel buttonPanel = new JPanel(new GridLayout(2, 4));

        //Create the random numbers and assign them to the buttons
        Set<Integer> randomNumbers = createRandomNumbers();
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
        while (buttons.size() > 0) {
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
        clickedButton.setText(clickedButton.getName());

        //Check if the clicked button is the same as the first selected button
        if(selectedButtons.size() == 1 && !selectedButtons.contains(clickedButton)){
            JButton firstButton = selectedButtons.iterator().next();
            if(firstButton.getName().equals(clickedButton.getName())){
                firstButton.setEnabled(false);
                clickedButton.setEnabled(false);
                revealedButtons.add(firstButton);
                revealedButtons.add(clickedButton);
                score += 10;
                scoreLabel.setText("Score: " + score);
            } else {
                firstButton.setText("");
                clickedButton.setText("");
                if(score > 0){
                    score -= 5;
                    scoreLabel.setText("Score: " + score);
                }
            }
            selectedButtons.clear();
        } else {
            selectedButtons.add(clickedButton);
        }

        if(revealedButtons.size() == 8){
            gameTimer.stop();
            String message = score > currentHighscore ?
                    "You won and beat the Highscore! \nYour score is " + score + " points. \nDo you want to play again?":
                    "You won! Your score is " + score + " points. \nDo you want to play again?";
            int result = JOptionPane.showConfirmDialog(
                    this, message, "You won!",
                    JOptionPane.YES_NO_OPTION);

            if(result == JOptionPane.YES_OPTION){
                resetGame();
            }else if(result == JOptionPane.NO_OPTION){
                if(score > currentHighscore){
                    HighscoreService.saveHighscore(score);
                }
                System.exit(0);
            }

        }
    }

    private void resetGame(){
        revealedButtons.clear();
        selectedButtons.clear();
        score = 0;
        rootPanel.remove(1);
        rootPanel.add(createButtonPanel(), BorderLayout.CENTER);
        scoreLabel.setText("Score: " + score);
        gameTimer.stop();
        gameTimer.reset();
    }




}
