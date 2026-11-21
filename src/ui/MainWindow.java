package ui;

import logic.ClinicService;
import storage.FileStorage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/** Main window: menu bar (File, View, Help), tabs and a colored status line. */
public class MainWindow extends JFrame implements StatusListener {
    private final ClinicService service;
    private final FileStorage storage;
    private final JLabel statusLine = new JLabel(" ");
    private final JTabbedPane tabs = new JTabbedPane();
    private final QueuePanel queuePanel;
    private final PatientsPanel patientsPanel;
    private final ReportPanel reportPanel;

    public MainWindow(ClinicService service, FileStorage storage, String startupMessage) {
        super("Clinic Queue System");
        this.service = service;
        this.storage = storage;
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent e) { exit(); }
        });

        queuePanel = new QueuePanel(service, this, this::dataChanged);
        patientsPanel = new PatientsPanel(service, this, this::dataChanged);
        reportPanel = new ReportPanel(service);
        tabs.setFont(DesignConstants.LABEL_FONT);
        tabs.addTab("Queue", queuePanel);
        tabs.addTab("Patients", patientsPanel);
        tabs.addTab("Report", reportPanel);
        tabs.addChangeListener(e -> refreshAll());

        statusLine.setFont(DesignConstants.STATUS_FONT);
        statusLine.setOpaque(true);
        statusLine.setBorder(DesignConstants.padded());

        setJMenuBar(buildMenu());
        getContentPane().setBackground(DesignConstants.BACKGROUND);
        add(tabs, BorderLayout.CENTER);
        add(statusLine, BorderLayout.SOUTH);
        setSize(820, 560);
        setLocationRelativeTo(null);
        show(startupMessage, true);
    }

    private JMenuBar buildMenu() {
        JMenuBar bar = new JMenuBar();
        JMenu file = new JMenu("File");
        JMenuItem save = new JMenuItem("Save");
        save.addActionListener(e -> show(storage.save(service), true));
        JMenuItem exit = new JMenuItem("Exit");
        exit.addActionListener(e -> exit());
        file.add(save);
        file.addSeparator();
        file.add(exit);

        JMenu view = new JMenu("View");
        String[] names = {"Queue", "Patients", "Report"};
        for (int i = 0; i < names.length; i++) {
            final int index = i;
            JMenuItem item = new JMenuItem(names[i]);
            item.addActionListener(e -> tabs.setSelectedIndex(index));
            view.add(item);
        }

        JMenu help = new JMenu("Help");
        JMenuItem about = new JMenuItem("About");
        about.addActionListener(e -> Dialogs.info(this,
                "Clinic Queue System\nTeam Kumar\n\nRegister patients, issue queue numbers,\n"
                + "call the next patient and view the daily report.", "About"));
        help.add(about);

        bar.add(file);
        bar.add(view);
        bar.add(help);
        return bar;
    }

    /** Called by panels after any change: refresh every tab and auto-save. */
    private void dataChanged() {
        storage.save(service);
        refreshAll();
    }

    private void refreshAll() {
        queuePanel.refresh();
        patientsPanel.refresh();
        reportPanel.refresh();
    }

    private void exit() {
        if (Dialogs.confirmExit(this)) {
            storage.save(service);
            dispose();
            System.exit(0);
        }
    }

    @Override public void show(String message, boolean ok) {
        statusLine.setText(message);
        statusLine.setForeground(DesignConstants.STATUS_TEXT);
        statusLine.setBackground(ok ? DesignConstants.SUCCESS : DesignConstants.ERROR);
    }
}
