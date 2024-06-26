package main.start;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.awt.*;
import java.awt.event.*;

public class easyLevels4 implements ActionListener {

    private JTextField answerField1, answerField2, answerField3, answerField4;
    private JLabel timerLabel;
    private Timer timer;
    private int secondsLeft = 30; 

    private JLabel hintImageLabel;
    private int hintClickCount = 0; 
    
    
    private String[] hintMessages = {
        "T",
        "R" };

    private int hintMessageIndex = 0;

    public easyLevels4() {
        openGameWindow(); 
    }

    private void openGameWindow() {
        JFrame gameFrame = new JFrame("Picture Who");
        gameFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        gameFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        gameFrame.addWindowListener(new WindowAdapter() {
            @Override 
            public void windowClosing(WindowEvent e) {
                if (timer != null){
                    timer.stop();
                }
            }
        });
        gameFrame.setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(94, 69, 128));
        gameFrame.getContentPane().add(mainPanel); 

        JPanel imagePanel = new JPanel(new GridLayout(1, 1, 20, 20));
        imagePanel.setBackground(new Color(94, 69, 128));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
        mainPanel.add(imagePanel, BorderLayout.CENTER);

        
        ImageIcon imageIcon1 = new ImageIcon("Picture Who/project/main/img/4.png");
        Image image1 = imageIcon1.getImage().getScaledInstance(700,  750, Image.SCALE_SMOOTH);
        ImageIcon scaledImageIcon1 = new ImageIcon(image1);
        JLabel imageLabel1 = new JLabel(scaledImageIcon1);
        imagePanel.add(imageLabel1);

        // Create text fields for answer 
        answerField1 = createSingleLetterTextField(gameFrame, answerField2);
        answerField2 = createSingleLetterTextField(gameFrame, answerField3);
        answerField3 = createSingleLetterTextField(gameFrame, answerField4);
        answerField4 = createSingleLetterTextField(gameFrame, null);
        
        // Panel to hold answer text fields 
        JPanel answerPanel = new JPanel(new GridLayout(1, 4, 20, 20));
        answerPanel.setBackground(new Color(94, 69, 128));
        answerPanel.setBorder(BorderFactory.createEmptyBorder(20, 790, 10, 790));
        mainPanel.add(answerPanel, BorderLayout.SOUTH);
        
        answerPanel.add(answerField1);
        answerPanel.add(answerField2);
        answerPanel.add(answerField3);
        answerPanel.add(answerField4);
        
        // Label to display remaining time 
        Border timeBorder = BorderFactory.createEmptyBorder(0, 430, 0, 100); 
        timerLabel = new JLabel("Time Left: " + secondsLeft);
        timerLabel.setHorizontalAlignment(SwingConstants.LEFT);
        timerLabel.setFont(new Font("Arial", Font.BOLD, 16));
        timerLabel.setForeground(Color.WHITE);
        timerLabel.setBorder(timeBorder);
        
        // Set the size of the timer label explicitly
        timerLabel.setPreferredSize(new Dimension(10, 20));
        
        mainPanel.add(timerLabel, BorderLayout.NORTH);

        // timer to count donwn the time 
        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                secondsLeft--;
                if (secondsLeft >= 0) {
                    timerLabel.setText("Time Left: " + secondsLeft);
                } else {
                    timer.stop();
                    int choice = JOptionPane.showConfirmDialog(gameFrame, "Uh-oh! Time's up! What's your next move? Do you want to restart the level?", "Restart Level", JOptionPane.YES_NO_OPTION);
                    if (choice == JOptionPane.YES_OPTION) {
                        restartLevel();
                        gameFrame.dispose();
                    } else {
                        gameFrame.dispose();
                        App mainScreen = new App();
                        mainScreen.setVisible(true);
                    }
                }
                if (secondsLeft == 10) {
                    play10SecondsLeftSound();
                }
            }
        });              
        timer.start();

        addBackPanel(gameFrame); // Add back button panel here

        gameFrame.setLocationRelativeTo(null);
        gameFrame.setVisible(true);
        // add the hint panel 
        addHintPanel(gameFrame);
    }

    // Method to create a single-letter input text field with specific styling and behavior
    private JTextField createSingleLetterTextField(JFrame gameFrame, JTextField nextField) {
        // Create text field for single letter input
        JTextField textField = new JTextField(4);
        textField.setHorizontalAlignment(JTextField.CENTER);
        textField.setFont(new Font("Arial", Font.BOLD, 30));
        textField.setForeground(new Color(94, 69, 128));
        textField.setBackground(new Color(211, 211, 211));
        Border lineBorder = BorderFactory.createLineBorder(new Color(94, 69, 128), 5, true);
        Border shadowBorder = BorderFactory.createLineBorder(new Color(0, 0, 0, 50), 10);
        Border compoundBorder = new CompoundBorder(lineBorder, shadowBorder);
        Dimension preferredSize = new Dimension(0, 50); // Adjust width and height as needed the box of the word
        textField.setPreferredSize(preferredSize);
    
    
        textField.setBorder(compoundBorder);
        Border border = BorderFactory.createLineBorder(new Color(0,0,0), 2, true);
        textField.setBorder(border);
        textField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char inputChar = e.getKeyChar();
                if (Character.isLetter(inputChar) && textField.getText().length() == 0) {
                    if (nextField != null) {
                        nextField.requestFocusInWindow();
                    }
                }
            }
        });
    
        textField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                checkAnswers(gameFrame);
            }
        });
    
        ((AbstractDocument) textField.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                String newText = textField.getText(0, offset) + text.toUpperCase() + textField.getText(offset + length, textField.getDocument().getLength() - offset - length);
                if (newText.length() <= 1) {
                    super.replace(fb, offset, length, text.toUpperCase(), attrs);
                    if (nextField != null && text.matches("[a-zA-Z]")) {
                        nextField.requestFocusInWindow();
                    }
                }
            }
        });
        
        return textField;
    }
    
    // Method to check player answers and handle game progression based on correctness 
    private void checkAnswers(JFrame gameFrame) {
        String enteredAnswer1 = answerField1.getText().trim().toLowerCase();
        String enteredAnswer2 = answerField2.getText().trim().toLowerCase();
        String enteredAnswer3 = answerField3.getText().trim().toLowerCase();
        String enteredAnswer4 = answerField4.getText().trim().toLowerCase();
    
        String correctAnswer1 = "t";
        String correctAnswer2 = "r";
        String correctAnswer3 = "e";
        String correctAnswer4 = "e";
    
        if (enteredAnswer1.equals(correctAnswer1) &&
                enteredAnswer2.equals(correctAnswer2) &&
                enteredAnswer3.equals(correctAnswer3) &&
                enteredAnswer4.equals(correctAnswer4)) {
            playCorrectAnswerSound();
            timer.stop();
            String message = ("<html><div style='text-align: center; margin-left: 60px; margin-right: 60px;'><span style='font-family: Paytone One; font-size: 20px; color: #443C3C;'>TREE!</span><br><span style='font-size: 27px; color: #5E4580;'>Brilliant</span></div></html>");
            JOptionPane optionPane = new JOptionPane(message, JOptionPane.PLAIN_MESSAGE, JOptionPane.DEFAULT_OPTION, null, new Object[]{}, null);
            optionPane.setOptions(new Object[]{"NEXT"});
            JDialog dialog = optionPane.createDialog(gameFrame, "Next Level Unlocked!");
            dialog.setVisible(true);   
            gameFrame.dispose();
            openNextLevel();
        } else {
            playWrongAnswerSound();
            JOptionPane.showMessageDialog(gameFrame, "Incorrect!");
        }
        answerField1.setText("");
        answerField2.setText("");
        answerField3.setText("");
        answerField4.setText("");
        answerField1.requestFocusInWindow();
    }

    private void playCorrectAnswerSound() {
        try {
            File audioFile = new File("Picture Who/project/main/se/correct answer.wav");
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(audioFile);
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream); 
            clip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException ex) {
            ex.printStackTrace();
        }
    }

    private void play10SecondsLeftSound() {
        try {
            File audioFile = new File("Picture Who/project/main/se/timer.wav");
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(audioFile);
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException ex) {
            ex.printStackTrace();
        }
    }
    
    private void playWrongAnswerSound() {
        try {
            File audioFile = new File("Picture Who/project/main/se/wrong answer.wav");
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(audioFile);
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException ex) {
            ex.printStackTrace();
        }
    }

    private void playHintSound() {
        try {
            File audioFile = new File("Picture Who/project/main/se/hint.wav");
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(audioFile);
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException ex) {
            ex.printStackTrace();
        }
    }

    private void addBackPanel(JFrame gameFrame) {
        ImageIcon backIcon = new ImageIcon("Picture Who/project/main/img/red.png");
        Image backImage = backIcon.getImage().getScaledInstance(150, 80, Image.SCALE_SMOOTH);    
        ImageIcon scaledIcon = new ImageIcon(backImage);
        JLabel backImageLabel = new JLabel(scaledIcon);
        backImageLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backImageLabel.setToolTipText("Click to go back");
        backImageLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int choice = JOptionPane.showConfirmDialog(gameFrame, "Are you sure you want to go back to the main screen?", "Confirmation", JOptionPane.YES_NO_OPTION);
                if (choice == JOptionPane.YES_OPTION) {
                    // Dispose the current frame
                    gameFrame.dispose();
                    // Create a new instance of the main screen frame
                    App mainScreen = new App();
                    mainScreen.setVisible(true);
                }
            }
        });
    
        // Removed setPreferredSize line
    
        JPanel backPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT)); // Align the back button to the right
        backPanel.setBackground(new Color(94, 69, 128));
        backPanel.add(backImageLabel);
        gameFrame.getContentPane().add(backPanel, BorderLayout.NORTH); // Align the backPanel to the top of the gameFrame;
    }
    
    
    private void addHintPanel(JFrame gameFrame) {
        ImageIcon hintIcon = new ImageIcon("Picture Who/project/main/img/not.png");
        Image hintImage = hintIcon.getImage().getScaledInstance(300, 150, Image.SCALE_SMOOTH);
        ImageIcon scaledHintIcon = new ImageIcon(hintImage);
        hintImageLabel = new JLabel(scaledHintIcon);
        hintImageLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        hintImageLabel.setToolTipText("Click for hint");
        hintImageLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (hintClickCount < hintMessages.length) {
                    playHintSound();
                    timer.stop();
                    int choice = JOptionPane.showConfirmDialog(gameFrame, "Do you want to use a hint?", "Hint System", JOptionPane.YES_NO_OPTION);
                    if (choice == JOptionPane.YES_OPTION) {
                        String hintMessage;
                        if (hintClickCount == 0) {
                            hintMessage = "Need a hint? Use your wisdom wisely! You have 2 hint trials remaining. Each hint deducts 5 seconds from your remaining time. Choose and think wisely to uncover the hidden word!";
                        } else {
                            hintMessage = "Need a hint? Use your wisdom wisely! You have 1 hint trial remaining. Each hint deducts 5 seconds from your remaining time. Choose and think wisely to uncover the hidden word!";
                        }
                        JOptionPane.showMessageDialog(gameFrame, hintMessage);
                        JOptionPane.showMessageDialog(gameFrame, "Hint Letters: " + hintMessages[hintMessageIndex].charAt(0));
                        hintClickCount++;
                        if (hintClickCount == hintMessages.length) {
                            hintImageLabel.setEnabled(false);
                            hintImageLabel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                        }
                        // Deduct 5 seconds for each hint click
                        secondsLeft = Math.max(0, secondsLeft - 5);
                        timerLabel.setText("Time Left: " + secondsLeft);
                        hintMessageIndex = (hintMessageIndex + 1) % hintMessages.length;
                    }
                    timer.start();
                } else {
                    JOptionPane.showMessageDialog(gameFrame, "Uh-oh! You have no more hint trials.");
                }
            }
        });
    
    
            JPanel hintPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            hintPanel.setBackground(new Color(94, 69, 128));
            hintPanel.add(hintImageLabel);
            gameFrame.getContentPane().add(hintPanel, BorderLayout.SOUTH);
        }
    
    private void openNextLevel() {
        new easyLevels5();
    }
    
    private void restartLevel() {
        new easyLevels4();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        openGameWindow();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new easyLevels4();
            }
        });
    }
}
