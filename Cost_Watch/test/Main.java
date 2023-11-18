import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Main {
    private static Connection conn;

    public static void main(String[] args) {
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost/expenses", "root", "2020btecs00081");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        JFrame mainFrame = new JFrame("CostWatch");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(700, 250);
        mainFrame.setLayout(new GridLayout(1, 4));

        JButton addButton = new JButton("Add Expense");
        JButton trackButton = new JButton("Track Expenses");
        JButton searchDurationButton = new JButton("Track Duration");
        JButton lastMonthButton = new JButton("Last Month");

        mainFrame.add(addButton);
        mainFrame.add(trackButton);
        mainFrame.add(searchDurationButton);
        mainFrame.add(lastMonthButton);

        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showAddExpenseWindow();
            }
        });

        trackButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showTrackExpensesWindow();
            }
        });

        searchDurationButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showTotalCostBetweenDatesWindow();
            }
        });

        lastMonthButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                trackExpensesLastMonth();
            }
        });

        mainFrame.setVisible(true);
    }


    public static void showAddExpenseWindow() {
        JFrame addFrame = new JFrame("Add Expense");
        addFrame.setSize(300, 150);
        addFrame.setLayout(new GridLayout(4, 2));

        JTextField dateField = new JTextField();
        JTextField descriptionField = new JTextField();
        JTextField amountField = new JTextField();
        JButton addButton = new JButton("Add");
        JButton closeButton = new JButton("Close");

        addFrame.add(new JLabel("Date (dd-mm-yyyy):"));
        addFrame.add(dateField);
        addFrame.add(new JLabel("Description:"));
        addFrame.add(descriptionField);
        addFrame.add(new JLabel("Amount:"));
        addFrame.add(amountField);
        addFrame.add(addButton);
        addFrame.add(closeButton);

        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    String dateStr = dateField.getText();
                    SimpleDateFormat sdf = new SimpleDateFormat("dd-mm-yyyy");
                    java.util.Date date = sdf.parse(dateStr);
                    java.sql.Date sqlDate = new java.sql.Date(date.getTime());
                    String description = descriptionField.getText();
                    double amount = Double.parseDouble(amountField.getText());

                    PreparedStatement stmt = conn.prepareStatement("INSERT INTO expense_records (date, description, amount) VALUES (?, ?, ?)");
                    stmt.setDate(1, sqlDate);
                    stmt.setString(2, description);
                    stmt.setDouble(3, amount);
                    stmt.executeUpdate();

                    JOptionPane.showMessageDialog(null, "Expense added successfully.");
                    addFrame.dispose();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Invalid input. Please enter a valid date and amount.");
                }
            }
        });

        closeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addFrame.dispose();
            }
        });

        addFrame.setVisible(true);
    }

    public static void showTrackExpensesWindow() {
        JFrame trackFrame = new JFrame("Track Expenses");
        trackFrame.setSize(400, 200);
        trackFrame.setLayout(new GridLayout(3, 2));

        JTextField dateField = new JTextField();
        JButton searchButton = new JButton("Search");
        JButton lastDaysButton = new JButton("Last 7 Days");
        JButton lastWeekButton = new JButton("Last Week");

        JTextField totalExpenseField = new JTextField();
        totalExpenseField.setEditable(false);

        trackFrame.add(new JLabel("Date (dd-mm-yyyy):"));
        trackFrame.add(dateField);
        trackFrame.add(searchButton);
        trackFrame.add(lastDaysButton);
        trackFrame.add(lastWeekButton);
        trackFrame.add(new JLabel("Total Expense:"));
        trackFrame.add(totalExpenseField);

        searchButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    String dateStr = dateField.getText();
                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                    java.util.Date date = sdf.parse(dateStr);
                    java.sql.Date sqlDate = new java.sql.Date(date.getTime());

                    double totalExpense = getTotalExpenseForDate(sqlDate);
                    totalExpenseField.setText("$" + totalExpense);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error searching expenses.");
                }
            }
        });

        lastDaysButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                trackExpenses(7);
            }
        });

        lastWeekButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                trackExpenses(7);
            }
        });

        trackFrame.setVisible(true);
    }

    public static void showTotalCostBetweenDatesWindow() {
        JFrame durationFrame = new JFrame("Track Duration");
        durationFrame.setSize(350, 150);
        durationFrame.setLayout(new GridLayout(3, 2));

        JTextField startDateField = new JTextField();
        JTextField endDateField = new JTextField();
        JButton searchButton = new JButton("Search");

        JTextField totalCostField = new JTextField();
        totalCostField.setEditable(false);

        durationFrame.add(new JLabel("Start Date (dd-mm-yyyy):"));
        durationFrame.add(startDateField);
        durationFrame.add(new JLabel("End Date (dd-mm-yyyy):"));
        durationFrame.add(endDateField);
        durationFrame.add(searchButton);
        durationFrame.add(totalCostField);

        searchButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    String startDateStr = startDateField.getText();
                    String endDateStr = endDateField.getText();

                    SimpleDateFormat sdf = new SimpleDateFormat("dd-mm-yyyy");
                    java.util.Date startDate = sdf.parse(startDateStr);
                    java.util.Date endDate = sdf.parse(endDateStr);

                    java.sql.Date sqlStartDate = new java.sql.Date(startDate.getTime());
                    java.sql.Date sqlEndDate = new java.sql.Date(endDate.getTime());

                    double totalCost = getTotalCostBetweenDates(sqlStartDate, sqlEndDate);
                    totalCostField.setText("$" + totalCost);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error calculating total cost between dates.");
                }
            }
        });

        durationFrame.setVisible(true);
    }

    public static double getTotalExpenseForDate(Date date) throws SQLException {
        String query = "SELECT SUM(amount) FROM expense_records WHERE date = ?";
        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setDate(1, date);
        ResultSet rs = stmt.executeQuery();
        rs.next();
        return rs.getDouble(1);
    }

    public static void trackExpenses(int days) {
        try {
            java.sql.Date date = new java.sql.Date(System.currentTimeMillis() - (days * 24L * 60 * 60 * 1000));
            String query = "SELECT SUM(amount) FROM expense_records WHERE date >= ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setDate(1, date);
            ResultSet rs = stmt.executeQuery();
            rs.next();
            double totalExpense = rs.getDouble(1);

            JOptionPane.showMessageDialog(null, "Total Expense for the last " + days + " days: $" + totalExpense);
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error tracking expenses.");
        }
    }

    public static void trackExpensesLastMonth() {
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MONTH, -1);
            java.sql.Date date = new java.sql.Date(calendar.getTimeInMillis());

            String query = "SELECT SUM(amount) FROM expense_records WHERE date >= ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setDate(1, date);
            ResultSet rs = stmt.executeQuery();
            rs.next();
            double totalExpense = rs.getDouble(1);

            JOptionPane.showMessageDialog(null, "Total Expense for the last month: $" + totalExpense);
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error tracking expenses for the last month.");
        }
    }

    public static double getTotalCostBetweenDates(Date startDate, Date endDate) {
        try {
            String query = "SELECT SUM(amount) FROM expense_records WHERE date BETWEEN ? AND ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setDate(1, startDate);
            stmt.setDate(2, endDate);
            ResultSet rs = stmt.executeQuery();
            rs.next();
            return rs.getDouble(1);
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error fetching total cost between dates.");
            return 0.0;
        }
    }
}
