import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MainUI extends JFrame {
    private JPanel panel1;
    private JTextField firstName_textField;
    private JTextField lastName_textField;
    private JButton add_button;
    private JPanel formPanel;
    private JTextField position_textField;
    private JTable table;
    private DefaultTableModel modelTabeli;

    private Database database;

    public MainUI() {
        setTitle("INF.04 - System Obsługi Pracowników");
        setMinimumSize(new Dimension(600, 450));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {}

        try {
            database = new Database();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Nie można połączyć się z bazą danych:\n" + e.getMessage(),
                    "Błąd bazy danych",
                    JOptionPane.ERROR_MESSAGE
            );
            dispose();
            return;
        }

        String[] kolumny = {"ID", "Imię", "Nazwisko", "Stanowisko"};
        modelTabeli = new DefaultTableModel(kolumny, 0);
        table.setModel(modelTabeli);

        add_button.addActionListener(e -> addEmployee());
        updateTable();

        add(panel1);
        setVisible(true);
    }

    private void addEmployee() {
        String firstName = firstName_textField.getText().trim();
        String lastName = lastName_textField.getText().trim();
        String position = position_textField.getText().trim();

        if (firstName.isEmpty() || lastName.isEmpty() || position.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Wszystkie pola muszą być wypełnione.",
                    "Błąd walidacji",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        try {
            database.addEmployee(firstName, lastName, position);
            JOptionPane.showMessageDialog(
                    this,
                    "Pracownik został dodany pomyślnie.",
                    "Sukces",
                    JOptionPane.INFORMATION_MESSAGE
            );

            firstName_textField.setText("");
            lastName_textField.setText("");
            position_textField.setText("");

            updateTable();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Nie udało się dodać pracownika:\n" + e.getMessage(),
                    "Błąd bazy danych",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void updateTable() {
        modelTabeli.setRowCount(0);

        try (Connection conn = database.getConnection();
            ResultSet rs = database.getEmployees(conn)) {

            while (rs.next()) {
                modelTabeli.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("imie"),
                        rs.getString("nazwisko"),
                        rs.getString("stanowisko")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Nie udało się pobrać pracowników:\n" + e.getMessage(),
                    "Błąd odczytu",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    static void main() {
        SwingUtilities.invokeLater(MainUI::new);
    }
}
