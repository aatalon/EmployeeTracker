

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class employee extends JFrame {

    // text fields for data entry
    private final JTextField idField = new JTextField(10);
    private final JTextField firstNameField = new JTextField(12);
    private final JTextField lastNameField = new JTextField(12);
    private final JTextField salaryField = new JTextField(10);
    private final JTextField startDateField = new JTextField(10);

    // buttons
    private final JButton addBtn = new JButton("Add");
    private final JButton removeBtn = new JButton("Remove");
    private final JButton listBtn = new JButton("List");

    // output area
    private final JTextArea outputArea = new JTextArea(16, 26);

    // our storage: an ArrayList of associative arrays (HashMap)
    // each HashMap is one employee record
    private final java.util.List<HashMap<String, String>> employees = new ArrayList<>();

    public employee() {
        super("Employee Records");

        // basic window setup
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(430, 520);
        setLocationRelativeTo(null);

        // layout similar to the sample (left inputs, right buttons, big output)
        JPanel main = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 8, 6, 8);
        c.anchor = GridBagConstraints.WEST;

        // title
        JLabel title = new JLabel("Employee Records");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 18f));
        c.gridx = 0; c.gridy = 0; c.gridwidth = 3;
        c.anchor = GridBagConstraints.CENTER;
        main.add(title, c);

        // reset defaults
        c.gridwidth = 1;
        c.anchor = GridBagConstraints.WEST;

        // labels + fields (left column)
        int row = 1;
        addRow(main, c, row++, "ID #:", idField);
        addRow(main, c, row++, "First Name:", firstNameField);
        addRow(main, c, row++, "Last Name:", lastNameField);
        addRow(main, c, row++, "Annual Salary:", salaryField);
        addRow(main, c, row++, "Start Date:", startDateField);

        // right side: buttons stacked
        JPanel buttonCol = new JPanel(new GridLayout(3, 1, 0, 10));
        buttonCol.add(addBtn);
        buttonCol.add(removeBtn);
        buttonCol.add(listBtn);

        c.gridx = 2; c.gridy = 2; c.gridheight = 3; // roughly centered next to fields
        c.anchor = GridBagConstraints.NORTH;
        main.add(buttonCol, c);

        // output area in a scroll pane (big white box)
        outputArea.setEditable(false);
        JScrollPane scroll = new JScrollPane(outputArea);
        c.gridx = 0; c.gridy = row; c.gridwidth = 3; c.gridheight = 1;
        c.fill = GridBagConstraints.BOTH; c.weightx = 1; c.weighty = 1;
        main.add(scroll, c);

        // add main panel
        setContentPane(main);

        // hook up button clicks
        addBtn.addActionListener(e -> addEmployee());
        removeBtn.addActionListener(e -> removeEmployee());
        listBtn.addActionListener(e -> listEmployees());
    }

    // helper to place a label + field on one row
    private void addRow(JPanel panel, GridBagConstraints c, int y, String label, JTextField field) {
        c.gridx = 0; c.gridy = y; c.weightx = 0; c.fill = GridBagConstraints.NONE;
        panel.add(new JLabel(label), c);

        c.gridx = 1; c.gridy = y; c.weightx = 1; c.fill = GridBagConstraints.HORIZONTAL;
        panel.add(field, c);
    }

    // ADD: take data from fields, make a HashMap (associative array), add to ArrayList
    private void addEmployee() {
        String id = idField.getText().trim();
        String first = firstNameField.getText().trim();
        String last = lastNameField.getText().trim();
        String salary = salaryField.getText().trim();
        String start = startDateField.getText().trim();

        // quick validation: fields must not be empty
        if (id.isEmpty() || first.isEmpty() || last.isEmpty() || salary.isEmpty() || start.isEmpty()) {
            showMsg("Please fill in ALL fields before pressing Add.");
            return;
        }

        // check salary is a number (simple check)
        try {
            Double.parseDouble(salary);
        } catch (NumberFormatException nfe) {
            showMsg("Salary must be a valid number (e.g., 52000 or 52000.50).");
            return;
        }

        // ID must be unique (we'll block duplicates)
        if (findIndexById(id) != -1) {
            showMsg("An employee with ID " + id + " already exists.");
            return;
        }

        // build the associative array (one record)
        HashMap<String, String> record = new HashMap<>();
        record.put("id", id);
        record.put("firstName", first);
        record.put("lastName", last);
        record.put("annualSalary", salary);
        record.put("startDate", start);

        // add to our ArrayList
        employees.add(record);

        showMsg("Added employee: " + first + " " + last + " (ID " + id + ")");
        clearInputs();
    }

    // REMOVE: user enters ID only, we delete that record
    private void removeEmployee() {
        String id = idField.getText().trim();
        if (id.isEmpty()) {
            showMsg("Enter an ID in the ID field, then press Remove.");
            return;
        }

        int idx = findIndexById(id);
        if (idx == -1) {
            showMsg("No employee found with ID " + id + ".");
            return;
        }

        HashMap<String, String> removed = employees.remove(idx);
        showMsg("Removed employee ID " + id + " (" +
                removed.get("firstName") + " " + removed.get("lastName") + ").");
        // keep ID in the box so the student can see what they removed (optional)
    }

    // LIST: print every record to the output area
    private void listEmployees() {
        if (employees.isEmpty()) {
            outputArea.setText("No employee records yet.\n");
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-8s %-12s %-12s %-12s %-12s%n",
                "ID", "First", "Last", "Salary", "Start"));
        sb.append("------------------------------------------------------------\n");

        for (HashMap<String, String> rec : employees) {
            sb.append(String.format("%-8s %-12s %-12s %-12s %-12s%n",
                    rec.get("id"),
                    rec.get("firstName"),
                    rec.get("lastName"),
                    rec.get("annualSalary"),
                    rec.get("startDate")));
        }

        outputArea.setText(sb.toString());
    }

    // find an employee index by ID (returns -1 if not found)
    private int findIndexById(String id) {
        for (int i = 0; i < employees.size(); i++) {
            if (id.equals(employees.get(i).get("id"))) {
                return i;
            }
        }
        return -1;
    }

    // small helper for messages to the output box
    private void showMsg(String msg) {
        outputArea.append(msg + "\n");
    }

    // clear input fields after adding
    private void clearInputs() {
        idField.setText("");
        firstNameField.setText("");
        lastNameField.setText("");
        salaryField.setText("");
        startDateField.setText("");
    }

    public static void main(String[] args) {
        // make UI look a bit nicer if possible
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> new employee().setVisible(true));
    }
}
