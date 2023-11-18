import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class Main2 extends JFrame {
    // Your database connection details here
    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost/expenses";
    static final String USER = "root";
    static final String PASS = "2020btecs00081";

    public Main2() {
        setLayout(new FlowLayout());

        JButton previousDayButton = new JButton("Total Expenses - Previous Day");
        JButton currentDayButton = new JButton("Total Expenses - Current Day");
        JButton previousWeekButton = new JButton("Total Expenses - Previous Week");
        JButton currentWeekButton = new JButton("Total Expenses - Current Week");
        JButton previousMonthButton = new JButton("Total Expenses - Previous Month");
        JButton addExpenseButton = new JButton("Add Expense");

        previousDayButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                displayTotalExpense("SELECT SUM(amount) AS total_expense FROM expense_records WHERE date = CURDATE() - INTERVAL 1 DAY;");
            }
        });

        currentDayButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                displayTotalExpense("SELECT SUM(amount) AS total_expense FROM expense_records WHERE date = CURDATE();");
            }
        });

        previousWeekButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                displayTotalExpense("SELECT SUM(amount) AS total_expense \n" +
                        "FROM expense_records \n" +
                        "WHERE date >= CURDATE() - INTERVAL WEEKDAY(CURDATE()) + 1 DAY - INTERVAL 1 WEEK \n" +
                        "AND date < CURDATE() - INTERVAL WEEKDAY(CURDATE()) + 1 DAY;");
            }
        });

        currentWeekButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                displayTotalExpense("SELECT SUM(amount) AS total_expense FROM expense_records WHERE YEARWEEK(date) = YEARWEEK(CURDATE());");
            }
        });

        previousMonthButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                displayTotalExpense("SELECT SUM(amount) AS total_expense FROM expense_records WHERE YEAR(date) = YEAR(CURRENT_DATE - INTERVAL 1 MONTH) AND MONTH(date) = MONTH(CURRENT_DATE - INTERVAL 1 MONTH);");
            }
        });

        addExpenseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAddExpenseWindow();
            }
        });

        JButton givenDayExpenseButton = new JButton("Total Expense - Given Day");
        givenDayExpenseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String inputDate = JOptionPane.showInputDialog("Enter Date (YYYY-MM-DD):");
                if (inputDate != null && !inputDate.isEmpty()) {
                    displayTotalExpense("SELECT SUM(amount) AS total_expense FROM expense_records WHERE date = '" + inputDate + "';");
                }
            }
        });

        JButton givenDurationExpenseButton = new JButton("Total Expense - Given Duration");
        givenDurationExpenseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String startDate = JOptionPane.showInputDialog("Enter Start Date (YYYY-MM-DD):");
                String endDate = JOptionPane.showInputDialog("Enter End Date (YYYY-MM-DD):");
                if (startDate != null && endDate != null && !startDate.isEmpty() && !endDate.isEmpty()) {
                    displayTotalExpense("SELECT SUM(amount) AS total_expense FROM expense_records WHERE date BETWEEN '" + startDate + "' AND '" + endDate + "';");
                }
            }
        });

//        JButton givenDurationExpenseButton = new JButton("Total Expense - Given Duration");
//        givenDurationExpenseButton.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                JTextField startDateField = new JTextField(10);
//                JTextField endDateField = new JTextField(10);
//
//                JPanel myPanel = new JPanel();
//                myPanel.setLayout(new GridLayout(3, 2));
//                myPanel.add(new JLabel("Start Date (YYYY-MM-DD):"));
//                myPanel.add(startDateField);
//                myPanel.add(new JLabel("End Date (YYYY-MM-DD):"));
//                myPanel.add(endDateField);
//
//                int result = JOptionPane.showConfirmDialog(null, myPanel,
//                        "Enter Start and End Dates", JOptionPane.OK_CANCEL_OPTION);
//                if (result == JOptionPane.OK_OPTION) {
//                    String startDate = startDateField.getText();
//                    String endDate = endDateField.getText();
//
//                    if (!startDate.isEmpty() && !endDate.isEmpty()) {
//                        displayTotalExpense("SELECT SUM(amount) AS total_expense FROM expense_records WHERE date BETWEEN '" + startDate + "' AND '" + endDate + "';");
//                    }
//                }
//            }
//        });

        add(givenDayExpenseButton);
        add(givenDurationExpenseButton);

        add(previousDayButton);
        add(currentDayButton);
        add(previousWeekButton);
        add(currentWeekButton);
        add(previousMonthButton);
        add(addExpenseButton);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setVisible(true);
    }

    private void displayTotalExpense(String query) {
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            if (rs.next()) {
                double totalExpense = rs.getDouble("total_expense");
                JOptionPane.showMessageDialog(null, "Total Expense: " + totalExpense);
            } else {
                JOptionPane.showMessageDialog(null, "No data found.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void showAddExpenseWindow() {
        JTextField dateField = new JTextField(10);
        JTextField descriptionField = new JTextField(15);
        JTextField amountField = new JTextField(10);

        JPanel myPanel = new JPanel();
        myPanel.setLayout(new GridLayout(3, 2));
        myPanel.add(new JLabel("Date (YYYY-MM-DD):"));
        myPanel.add(dateField);
        myPanel.add(new JLabel("Description:"));
        myPanel.add(descriptionField);
        myPanel.add(new JLabel("Amount:"));
        myPanel.add(amountField);

        int result = JOptionPane.showConfirmDialog(null, myPanel,
                "Please Enter Expense Details", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String date = dateField.getText();
            String description = descriptionField.getText();
            double amount = Double.parseDouble(amountField.getText());

            try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
                 PreparedStatement pstmt = conn.prepareStatement("INSERT INTO expense_records (date, description, amount) VALUES (?, ?, ?)")) {

                pstmt.setString(1, date);
                pstmt.setString(2, description);
                pstmt.setDouble(3, amount);

                int rowsInserted = pstmt.executeUpdate();
                if (rowsInserted > 0) {
                    JOptionPane.showMessageDialog(null, "Expense added successfully!");
                } else {
                    JOptionPane.showMessageDialog(null, "Failed to add expense.");
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error: Unable to add expense.");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Error: Invalid amount format!");
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new Main2();
            }
        });
    }
}


//import javax.swing.*;
//import java.awt.*;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
//import java.sql.*;
//
//public class TrackExpenseGui extends JFrame {
//    // Your database connection details here
//    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
//    static final String DB_URL = "jdbc:mysql://localhost/expenses";
//    static final String USER = "root";
//    static final String PASS = "2020btecs00081";
//
//    public TrackExpenseGui() {
//        setLayout(new FlowLayout());
//
//        JButton previousDayButton = new JButton("Total Expenses - Previous Day");
//        JButton currentDayButton = new JButton("Total Expenses - Current Day");
//        JButton previousWeekButton = new JButton("Total Expenses - Previous Week");
//        JButton currentWeekButton = new JButton("Total Expenses - Current Week");
//        JButton previousMonthButton = new JButton("Total Expenses - Previous Month");
//        //JButton addExpenseButton = new JButton("Add Expense");
//
//        setLayout(new FlowLayout());
//
//        JButton givenDayExpenseButton = new JButton("Total Expense - Given Day");
//        givenDayExpenseButton.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                String inputDate = JOptionPane.showInputDialog("Enter Date (YYYY-MM-DD):");
//                if (inputDate != null && !inputDate.isEmpty()) {
//                    displayTotalExpenseForGivenDay(inputDate);
//                }
//            }
//        });
//
//        JButton givenDurationExpenseButton = new JButton("Total Expense - Given Duration");
//        givenDurationExpenseButton.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                String startDate = JOptionPane.showInputDialog("Enter Start Date (YYYY-MM-DD):");
//                String endDate = JOptionPane.showInputDialog("Enter End Date (YYYY-MM-DD):");
//                if (startDate != null && endDate != null && !startDate.isEmpty() && !endDate.isEmpty()) {
//                    displayTotalExpenseForGivenDuration(startDate, endDate);
//                }
//            }
//        });
//
//        previousDayButton.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                displayTotalExpense("SELECT SUM(amount) AS total_expense FROM expense_records WHERE date = CURDATE() - INTERVAL 1 DAY;");
//            }
//        });
//
//        currentDayButton.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                displayTotalExpense("SELECT SUM(amount) AS total_expense FROM expense_records WHERE date = CURDATE();");
//            }
//        });
//
//        previousWeekButton.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                displayTotalExpense("SELECT SUM(amount) AS total_expense FROM expense_records WHERE date BETWEEN CURDATE() - INTERVAL WEEKDAY(CURDATE()) + 1 DAY - INTERVAL 1 WEEK AND CURDATE() - INTERVAL WEEKDAY(CURDATE()) + 1 DAY;");
//            }
//        });
//
//        currentWeekButton.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                displayTotalExpense("SELECT SUM(amount) AS total_expense FROM expense_records WHERE YEARWEEK(date) = YEARWEEK(CURDATE());");
//            }
//        });
//
//        previousMonthButton.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                displayTotalExpense("SELECT SUM(amount) AS total_expense FROM expense_records WHERE YEAR(date) = YEAR(CURRENT_DATE - INTERVAL 1 MONTH) AND MONTH(date) = MONTH(CURRENT_DATE - INTERVAL 1 MONTH);");
//            }
//        });
//
////        addExpenseButton.addActionListener(new ActionListener() {
////            @Override
////            public void actionPerformed(ActionEvent e) {
////                showAddExpenseWindow();
////            }
////        });
//
//
//        add(previousDayButton);
//        add(currentDayButton);
//        add(previousWeekButton);
//        add(currentWeekButton);
//        add(previousMonthButton);
//        //add(addExpenseButton);
//        add(givenDayExpenseButton);
//        add(givenDurationExpenseButton);
//
//        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//        setSize(400, 300);
//        setLocationRelativeTo(null);
//        setTitle("Track Expense");
//    }
//
//    private void displayTotalExpense(String query) {
//        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
//             Statement stmt = conn.createStatement();
//             ResultSet rs = stmt.executeQuery(query)) {
//
//            if (rs.next()) {
//                double totalExpense = rs.getDouble("total_expense");
//                JOptionPane.showMessageDialog(null, "Total Expense: " + totalExpense);
//            } else {
//                JOptionPane.showMessageDialog(null, "No data found.");
//            }
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void showAddExpenseWindow() {
//        JTextField dateField = new JTextField(10);
//        JTextField descriptionField = new JTextField(15);
//        JTextField amountField = new JTextField(10);
//
//        JPanel myPanel = new JPanel();
//        myPanel.setLayout(new GridLayout(3, 2));
//        myPanel.add(new JLabel("Date (YYYY-MM-DD):"));
//        myPanel.add(dateField);
//        myPanel.add(new JLabel("Description:"));
//        myPanel.add(descriptionField);
//        myPanel.add(new JLabel("Amount:"));
//        myPanel.add(amountField);
//
//        int result = JOptionPane.showConfirmDialog(null, myPanel,
//                "Please Enter Expense Details", JOptionPane.OK_CANCEL_OPTION);
//        if (result == JOptionPane.OK_OPTION) {
//            String date = dateField.getText();
//            String description = descriptionField.getText();
//            double amount = Double.parseDouble(amountField.getText());
//
//            try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
//                 PreparedStatement pstmt = conn.prepareStatement("INSERT INTO expense_records (date, description, amount) VALUES (?, ?, ?)")) {
//
//                pstmt.setString(1, date);
//                pstmt.setString(2, description);
//                pstmt.setDouble(3, amount);
//
//                int rowsInserted = pstmt.executeUpdate();
//                if (rowsInserted > 0) {
//                    JOptionPane.showMessageDialog(null, "Expense added successfully!");
//                } else {
//                    JOptionPane.showMessageDialog(null, "Failed to add expense.");
//                }
//            } catch (SQLException ex) {
//                ex.printStackTrace();
//                JOptionPane.showMessageDialog(null, "Error: Unable to add expense.");
//            } catch (NumberFormatException ex) {
//                JOptionPane.showMessageDialog(null, "Error: Invalid amount format!");
//            }
//        }
//    }
//
//    private void displayTotalExpenseForGivenDay(String inputDate) {
//        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
//             Statement stmt = conn.createStatement()) {
//
//            String query = "SELECT SUM(amount) AS total_expense FROM expense_records WHERE date = '" + inputDate + "'";
//            ResultSet rs = stmt.executeQuery(query);
//
//            if (rs.next()) {
//                double totalExpense = rs.getDouble("total_expense");
//                JOptionPane.showMessageDialog(null, "Total Expense for " + inputDate + ": " + totalExpense);
//            } else {
//                JOptionPane.showMessageDialog(null, "No data found for " + inputDate);
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//            JOptionPane.showMessageDialog(null, "Error: Unable to fetch data.");
//        }
//    }
//
//    private void displayTotalExpenseForGivenDuration(String startDate, String endDate) {
//        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
//             Statement stmt = conn.createStatement()) {
//
//            String query = "SELECT SUM(amount) AS total_expense FROM expense_records WHERE date BETWEEN '" + startDate + "' AND '" + endDate + "'";
//            ResultSet rs = stmt.executeQuery(query);
//
//            if (rs.next()) {
//                double totalExpense = rs.getDouble("total_expense");
//                JOptionPane.showMessageDialog(null, "Total Expense between " + startDate + " and " + endDate + ": " + totalExpense);
//            } else {
//                JOptionPane.showMessageDialog(null, "No data found for the given duration.");
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//            JOptionPane.showMessageDialog(null, "Error: Unable to fetch data.");
//        }
//    }
//
//    public static void main(String[] args) {
//        SwingUtilities.invokeLater(new Runnable() {
//            public void run() {
//                new TrackExpenseGui();
//            }
//        });
//    }
//}