import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AddExpenseGui extends JFrame {
    // Your database connection details here
    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost/expenses";
    static final String USER = "root";
    static final String PASS = "2020btecs00081";

    public AddExpenseGui() {
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
                new AddExpenseGui();
            }
        });
    }
}
