package ui;

import logic.ClinicService;
import model.Patient;
import model.QueueTicket;
import model.Visit;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/** Queue tab: register patient, issue ticket, call next. */
public class QueuePanel extends JPanel {
    private final ClinicService service;
    private final StatusListener status;
    private final Runnable onChange;

    private final JTextField nameField = new JTextField(15);
    private final JTextField ageField = new JTextField(5);
    private final JButton registerButton = styledButton("Register patient");

    private final JComboBox<Patient> patientBox = new JComboBox<>();
    private final JComboBox<String> typeBox = new JComboBox<>(Visit.getTypeNames());
    private final JTextField notesField = new JTextField(15);
    private final JLabel hint = new JLabel(" ");
    private final JButton issueButton = styledButton("Issue ticket");

    private final JButton callButton = styledButton("Call next");
    private final JLabel nowServing = new JLabel("Now serving: -");
    private final DefaultTableModel tableModel = new DefaultTableModel(
            new String[] {"Queue #", "Patient", "Visit", "Est. wait (min)"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };

    public QueuePanel(ClinicService service, StatusListener status, Runnable onChange) {
        this.service = service;
        this.status = status;
        this.onChange = onChange;
        setLayout(new BorderLayout(DesignConstants.PAD, DesignConstants.PAD));
        setBackground(DesignConstants.BACKGROUND);
        setBorder(DesignConstants.padded());

        JPanel forms = new JPanel(new GridLayout(1, 2, DesignConstants.PAD, 0));
        forms.setOpaque(false);
        forms.add(registerBox());
        forms.add(ticketBox());
        add(forms, BorderLayout.NORTH);

        JTable table = new JTable(tableModel);
        table.setFont(DesignConstants.LABEL_FONT);
        table.setRowHeight(24);
        JPanel center = new JPanel(new BorderLayout(0, DesignConstants.PAD / 2));
        center.setBorder(DesignConstants.box("Waiting queue"));
        center.setOpaque(false);
        center.add(new JScrollPane(table), BorderLayout.CENTER);
        add(center, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT, DesignConstants.PAD, 0));
        bottom.setOpaque(false);
        nowServing.setFont(DesignConstants.TITLE_FONT);
        bottom.add(callButton);
        bottom.add(nowServing);
        add(bottom, BorderLayout.SOUTH);

        DocumentListener dl = new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { updateButtons(); }
            public void removeUpdate(DocumentEvent e) { updateButtons(); }
            public void changedUpdate(DocumentEvent e) { updateButtons(); }
        };
        nameField.getDocument().addDocumentListener(dl);
        ageField.getDocument().addDocumentListener(dl);

        registerButton.addActionListener(e -> register());
        issueButton.addActionListener(e -> issue());
        callButton.addActionListener(e -> callNext());
        refresh();
    }

    private JPanel registerBox() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(DesignConstants.box("1. Register patient"));
        p.setOpaque(false);
        addRow(p, 0, "Name:", nameField);
        addRow(p, 1, "Age:", ageField);
        addRow(p, 2, "", registerButton);
        return p;
    }

    private JPanel ticketBox() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(DesignConstants.box("2. Issue ticket"));
        p.setOpaque(false);
        addRow(p, 0, "Patient:", patientBox);
        addRow(p, 1, "Visit type:", typeBox);
        addRow(p, 2, "Notes:", notesField);
        hint.setForeground(DesignConstants.ERROR);
        addRow(p, 3, "", hint);
        addRow(p, 4, "", issueButton);
        return p;
    }

    private void addRow(JPanel p, int row, String label, JComponent field) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridy = row; c.insets = new Insets(3, 3, 3, 3); c.anchor = GridBagConstraints.WEST;
        c.gridx = 0;
        JLabel l = new JLabel(label);
        l.setFont(DesignConstants.LABEL_FONT);
        p.add(l, c);
        c.gridx = 1; c.fill = GridBagConstraints.HORIZONTAL; c.weightx = 1;
        p.add(field, c);
    }

    static JButton styledButton(String text) {
        JButton b = new JButton(text);
        b.setFont(DesignConstants.BUTTON_FONT);
        b.setBackground(DesignConstants.PRIMARY);
        b.setForeground(DesignConstants.PRIMARY_TEXT);
        b.setOpaque(true);
        b.setBorderPainted(false);
        b.setMargin(new Insets(6, 14, 6, 14));
        return b;
    }

    /** Main buttons stay disabled until their input is complete. */
    private void updateButtons() {
        registerButton.setEnabled(!nameField.getText().trim().isEmpty() && !ageField.getText().trim().isEmpty());
        issueButton.setEnabled(patientBox.getSelectedItem() != null);
        callButton.setEnabled(!service.getWaitingTickets().isEmpty());
    }

    private void register() {
        try {
            int age = Integer.parseInt(ageField.getText().trim());
            Patient p = service.addPatient(nameField.getText(), age);
            nameField.setText("");
            ageField.setText("");
            onChange.run();
            patientBox.setSelectedItem(p);
            status.show("Registered " + p + ". Now issue a ticket.", true);
        } catch (NumberFormatException e) {
            status.show("Age must be a whole number.", false);
        } catch (RuntimeException e) {
            status.show(e.getMessage(), false);
        }
    }

    private void issue() {
        try {
            Patient p = (Patient) patientBox.getSelectedItem();
            QueueTicket t = service.issueTicket(p.getId(), (String) typeBox.getSelectedItem(), notesField.getText());
            notesField.setText("");
            onChange.run();
            status.show("Issued Q" + t.getNumber() + " to " + p.getName()
                    + ". Estimated wait: " + service.estimatedWaitMinutes(t) + " min.", true);
        } catch (RuntimeException e) {
            status.show(e.getMessage(), false);
        }
    }

    private void callNext() {
        QueueTicket t = service.callNext();
        if (t == null) {
            status.show("Nobody is waiting.", false);
        } else {
            onChange.run();
            status.show("Called Q" + t.getNumber() + " - " + t.getPatient().getName() + ".", true);
            nowServing.setText("Now serving: Q" + t.getNumber() + " " + t.getPatient().getName());
        }
    }

    public void refresh() {
        Object selected = patientBox.getSelectedItem();
        patientBox.removeAllItems();
        for (Patient p : service.getPatients()) patientBox.addItem(p);
        patientBox.setEnabled(patientBox.getItemCount() > 0);
        hint.setText(patientBox.getItemCount() > 0 ? " " : "No patients yet - register one in box 1 first.");
        if (selected != null) patientBox.setSelectedItem(selected);
        tableModel.setRowCount(0);
        for (QueueTicket t : service.getWaitingTickets()) {
            tableModel.addRow(new Object[] {"Q" + t.getNumber(), t.getPatient().getName(),
                    t.getVisitType(), service.estimatedWaitMinutes(t)});
        }
        updateButtons();
    }
}
