package de.xuuniversity.co1.memorygame.helper;

import javax.swing.*;


public class GameTimer {
    private final Timer timer;
    private int seconds = 0;

    public GameTimer(JLabel timerLabel) {
        this.timer = new Timer(1000, e -> {
            seconds++;
            int minutes = seconds / 60;
            int remainingSeconds = seconds % 60;
            timerLabel.setText("Time: " + String.format("%02d:%02d", minutes, remainingSeconds));
        });
    }

    public void start() {
        timer.start();
    }

    public void stop() {
        timer.stop();
    }
    public void reset() {
        seconds = 0;
    }
}