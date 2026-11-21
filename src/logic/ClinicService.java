package logic;

import model.InvalidInputException;
import model.Patient;
import model.RecordNotFoundException;
import model.QueueTicket;
import model.Visit;
import java.time.LocalDate;
import java.util.*;

public class ClinicService {
    private final List<Patient> patients = new ArrayList<>();
    private final List<QueueTicket> tickets = new ArrayList<>();
    private int nextPatientId = 1;
    private int nextQueueNumber = 1;

    // ---- Patients: add, search, edit, remove ----
    public Patient addPatient(String name, int age) {
        Validator.validateName(name);
        Validator.validateAge(age);
        Patient p = new Patient(nextPatientId++, name.trim(), age);
        patients.add(p);
        return p;
    }

    public List<Patient> searchPatients(String keyword) {
        if (keyword == null) throw new InvalidInputException("Search keyword must not be empty.");
        List<Patient> found = new ArrayList<>();
        for (Patient p : patients)
            if (p.getName().toLowerCase().contains(keyword.toLowerCase())) found.add(p);
        return found;
    }

    public void editPatient(int id, String name, int age) {
        Validator.validateName(name);
        Validator.validateAge(age);
        Patient p = findPatient(id);
        p.setName(name.trim());
        p.setAge(age);
    }

    public void removePatient(int id) {
        Patient p = findPatient(id);
        patients.remove(p);
        tickets.removeIf(t -> t.getPatient() == p);
    }

    private Patient findPatient(int id) {
        for (Patient p : patients) if (p.getId() == id) return p;
        throw new RecordNotFoundException("No patient with id " + id);
    }

    public List<Patient> getPatients() { return new ArrayList<>(patients); }
    public List<QueueTicket> getTickets() { return new ArrayList<>(tickets); }

    public Patient getPatient(int id) { return findPatient(id); }

    /** Replaces all data with records read from storage and continues numbering after them. */
    public void load(List<Patient> loadedPatients, List<QueueTicket> loadedTickets) {
        patients.clear();
        tickets.clear();
        patients.addAll(loadedPatients);
        tickets.addAll(loadedTickets);
        nextPatientId = 1;
        for (Patient p : patients) nextPatientId = Math.max(nextPatientId, p.getId() + 1);
        nextQueueNumber = 1;
        for (QueueTicket t : tickets) nextQueueNumber = Math.max(nextQueueNumber, t.getNumber() + 1);
    }

    // ---- Tickets: next number, call next, waiting time ----
    public QueueTicket issueTicket(int patientId, String visitType) {
        return issueTicket(patientId, visitType, "");
    }

    public QueueTicket issueTicket(int patientId, String visitType, String notes) {
        Validator.validateVisitType(visitType);
        QueueTicket t = new QueueTicket(nextQueueNumber++, findPatient(patientId), Visit.fromTypeName(visitType, notes));
        tickets.add(t);
        return t;
    }

    public QueueTicket findTicket(int number) {
        for (QueueTicket t : tickets) if (t.getNumber() == number) return t;
        throw new RecordNotFoundException("No ticket with number " + number);
    }

    public void cancelTicket(int number) { findTicket(number).cancel(); }

    public void removeTicket(int number) { tickets.remove(findTicket(number)); }

    /** Waiting tickets in the order they will be called. */
    public List<QueueTicket> getWaitingTickets() {
        List<QueueTicket> w = new ArrayList<>();
        for (QueueTicket t : tickets) if (t.isWaiting()) w.add(t);
        return w;
    }

    /** Calls the oldest waiting ticket and marks it served; null if nobody is waiting. */
    public QueueTicket callNext() {
        for (QueueTicket t : tickets) {
            if (t.isWaiting()) {
                t.markServed();
                return t;
            }
        }
        return null;
    }

    /**
     * Polymorphism: collects the Visit of every waiting ticket ahead of this one into a
     * List<Visit> and calls getEstimatedMinutes() on each. Each subclass (Consultation,
     * Dental, Check-up) answers with its own minutes.
     */
    public int estimatedWaitMinutes(QueueTicket ticket) {
        List<Visit> visitsAhead = new ArrayList<>();
        for (QueueTicket t : tickets) {
            if (t == ticket) break;
            if (t.isWaiting()) visitsAhead.add(t.getVisit());
        }
        int total = 0;
        for (Visit v : visitsAhead) total += v.getEstimatedMinutes();
        return total;
    }

    // ---- Report ----
    /** Builds the report for the given day from all tickets. */
    public DailyReport generateReport(LocalDate date) {
        return new DailyReport(date, new ArrayList<>(tickets));
    }

    public DailyReport generateReport() {
        return generateReport(LocalDate.now());
    }
}
