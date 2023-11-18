import javax.swing.*;
import java.awt.*;
import java.sql.*;
public class TrackExpenseGui extends JFrame {
    // Your database connection details here
    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost/expenses";
    static final String USER = "root";
    static final String PASS = "2020btecs00081";

    public TrackExpenseGui() {
        //setLayout(new FlowLayout());

        setLayout(new GridLayout(3, 3, 10, 10));

        JButton previousDayButton = new JButton("Previous Day");
        JButton currentDayButton = new JButton("Current Day");
        JButton previousWeekButton = new JButton("Previous Week");
        JButton currentWeekButton = new JButton("Current Week");
        JButton previousMonthButton = new JButton("Previous Month");
        JButton givenDayExpenseButton = new JButton("Given Day");
        JButton givenDurationExpenseButton = new JButton("Given Duration");

        previousDayButton.addActionListener(e -> displayTotalExpense("SELECT SUM(amount) AS total_expense, GROUP_CONCAT(description SEPARATOR ', ') AS descriptions FROM expense_records WHERE date = CURDATE() - INTERVAL 1 DAY;"));

        currentDayButton.addActionListener(e -> displayTotalExpense("SELECT SUM(amount) AS total_expense, GROUP_CONCAT(description SEPARATOR ', ') AS descriptions FROM expense_records WHERE date = CURDATE();"));

        previousWeekButton.addActionListener(e -> displayTotalExpense("SELECT SUM(amount) AS total_expense, GROUP_CONCAT(description SEPARATOR ', ') AS descriptions FROM expense_records WHERE date >= CURDATE() - INTERVAL WEEKDAY(CURDATE()) + 1 DAY - INTERVAL 1 WEEK AND date < CURDATE() - INTERVAL WEEKDAY(CURDATE()) + 1 DAY;"));

        currentWeekButton.addActionListener(e -> displayTotalExpense("SELECT SUM(amount) AS total_expense, GROUP_CONCAT(description SEPARATOR ', ') AS descriptions FROM expense_records WHERE YEARWEEK(date) = YEARWEEK(CURDATE());"));

        previousMonthButton.addActionListener(e -> displayTotalExpense("SELECT SUM(amount) AS total_expense, GROUP_CONCAT(description SEPARATOR ', ') AS descriptions FROM expense_records WHERE YEAR(date) = YEAR(CURRENT_DATE - INTERVAL 1 MONTH) AND MONTH(date) = MONTH(CURRENT_DATE - INTERVAL 1 MONTH);"));

        givenDayExpenseButton.addActionListener(e -> {
            String inputDate = JOptionPane.showInputDialog("Enter Date (YYYY-MM-DD):");
            if (inputDate != null && !inputDate.isEmpty()) {
                displayTotalExpense("SELECT SUM(amount) AS total_expense, GROUP_CONCAT(description SEPARATOR ', ') AS descriptions FROM expense_records WHERE date = '" + inputDate + "';");
            }
        });

        givenDurationExpenseButton.addActionListener(e -> {
            String startDate = JOptionPane.showInputDialog("Enter Start Date (YYYY-MM-DD):");
            String endDate = JOptionPane.showInputDialog("Enter End Date (YYYY-MM-DD):");
            if (startDate != null && endDate != null && !startDate.isEmpty() && !endDate.isEmpty()) {
                displayTotalExpense("SELECT SUM(amount) AS total_expense, GROUP_CONCAT(description SEPARATOR ', ') AS descriptions FROM expense_records WHERE date BETWEEN '" + startDate + "' AND '" + endDate + "';");
            }
        });

        Dimension buttonSize = new Dimension(200, 50);
        previousDayButton.setPreferredSize(buttonSize);
        currentDayButton.setPreferredSize(buttonSize);
        previousWeekButton.setPreferredSize(buttonSize);
        currentWeekButton.setPreferredSize(buttonSize);
        previousMonthButton.setPreferredSize(buttonSize);
        givenDayExpenseButton.setPreferredSize(buttonSize);
        givenDurationExpenseButton.setPreferredSize(buttonSize);

        add(previousDayButton);
        add(currentDayButton);
        add(previousWeekButton);
        add(currentWeekButton);
        add(previousMonthButton);
        add(givenDayExpenseButton);
        add(givenDurationExpenseButton);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);
        setTitle("Track Expense");
    }

    private void displayTotalExpense(String query) {
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            if (rs.next()) {
                double totalExpense = rs.getDouble("total_expense");
                String descriptions = rs.getString("descriptions");
                JOptionPane.showMessageDialog(null, "Total Expense: Rs " + totalExpense + "\nDescriptions: " + descriptions);
            } else {
                JOptionPane.showMessageDialog(null, "No data found.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(TrackExpenseGui::new);
    }
}
