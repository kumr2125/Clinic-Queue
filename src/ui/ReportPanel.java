package ui;

import logic.ClinicService;

import javax.swing.*;
import java.awt.*;

/** Report tab: daily report of served, waiting and visits per type. */
public class ReportPanel extends JPanel {
    private final ClinicService service;
    private final JTextArea area = new JTextArea();

    public ReportPanel(ClinicService service) {
        this.service = service;
        setLayout(new BorderLayout(DesignConstants.PAD, DesignConstants.PAD));
        setBackground(DesignConstants.BACKGROUND);
        setBorder(DesignConstants.padded());
        area.setEditable(false);
        area.setFont(DesignConstants.REPORT_FONT);
        area.setBorder(DesignConstants.padded());
        add(new JScrollPane(area), BorderLayout.CENTER);
        JButton refresh = QueuePanel.styledButton("Refresh report");
        refresh.addActionListener(e -> refresh());
        add(refresh, BorderLayout.SOUTH);
        refresh();
    }

    public void refresh() { area.setText(service.generateReport().toString()); }
}
