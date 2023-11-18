import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainGui extends JFrame {
    public MainGui() {
        setTitle("CostWatch");
        setLayout(new FlowLayout());


        JButton addExpenseButton = new JButton("Add Expense");
        addExpenseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AddExpenseGui addExpenseWindow = new AddExpenseGui();
                addExpenseWindow.setVisible(true);
            }
        });

        JButton trackExpenseButton = new JButton("Track Expense");
        trackExpenseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TrackExpenseGui trackExpenseWindow = new TrackExpenseGui();
                trackExpenseWindow.setVisible(true);
            }
        });

        add(addExpenseButton);
        add(trackExpenseButton);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(370, 200);
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new MainGui();
            }
        });
    }
}
