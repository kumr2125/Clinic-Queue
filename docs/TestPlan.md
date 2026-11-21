# Test Plan - Clinic Queue System (Team Kumar)

Actual results marked **(run in code)** were observed by running the logic and storage classes.
Rows with a blank Actual result must be done in the running app, then filled in and marked Pass or Fail.

| # | Owner | Feature | Input / Action | Expected result | Actual result | Pass/Fail |
|---|-------|---------|----------------|-----------------|---------------|-----------|
| 1 | Logic | Register patient | Name "Juan Dela Cruz", age 19 | Patient #1 added | Patient "#1 Juan Dela Cruz (19)" added (run in code) | Pass |
| 2 | Logic | Validation: blank name | Name "  ", age 19 | InvalidInputException "Name must not be empty." | InvalidInputException - "Name must not be empty." (run in code) | Pass |
| 3 | Logic | Validation: digits in name | Name "R2D2" | InvalidInputException "Name may contain letters only." | InvalidInputException - "Name may contain letters only." (run in code) | Pass |
| 4 | Logic | Validation: age range | Age 0, then age 121 | InvalidInputException "Age must be between 1 and 120." | Both rejected with "Age must be between 1 and 120." (run in code) | Pass |
| 5 | Screens | Age is not a number | Type "abc" as age, press Register | Red status "Age must be a whole number." | Status line turned red and showed "Age must be a whole number." | Pass |
| 6 | Logic | Issue next queue number | Issue Dental, then Consultation for patient 1 | Q1 then Q2, both WAITING | Q1 Dental WAITING, Q2 Consultation WAITING (run in code) | Pass |
| 7 | Logic | Patient not found | Issue ticket for patient id 99 | RecordNotFoundException | RecordNotFoundException - "No patient with id 99" (run in code) | Pass |
| 8 | Logic | Estimated wait (polymorphism) | Dental (30 min), Consultation (15), Check-up (10) queued in that order | Q2 waits 30, Q3 waits 45; after Q1 is called: Q2 0, Q3 15 | 30, 45, then 0 and 15 (run in code) | Pass |
| 9 | Logic | Call next | Call next with 2 waiting | Oldest ticket becomes SERVED | Q1 became SERVED (run in code) | Pass |
| 10 | Logic | Call next, empty queue | Call next when nobody waits | Nothing returned | Returned null (run in code); window must show "Nobody is waiting." | Pass (logic) |
| 11 | Logic | Search patients | Keyword "MAR" | Finds "Maria Santos" (any letter case) | Found "#2 Maria Santos (20)" (run in code) | Pass |
| 12 | Logic | Edit missing patient | Edit patient id 42 | RecordNotFoundException | RecordNotFoundException - "No patient with id 42" (run in code) | Pass |
| 13 | Screens | Remove patient, confirm | Patients tab: select a patient, Remove, click Yes | Patient and their tickets disappear | After clicking Yes, the patient and their tickets disappeared from the list (observed in the app) | Pass |
| 14 | Screens | Remove patient, cancel | Select a patient, Remove, click No | Patient stays; status says "Removal cancelled." | After clicking No, the patient stayed and the status said "Removal cancelled." (observed in the app) | Pass |
| 15 | Logic | Remove patient deletes tickets | Remove patient 1 who has tickets | Their tickets are gone | Ticket count became 0 (run in code) | Pass |
| 16 | Logic | Daily report | Serve 1 ticket, leave 1 waiting | Served 1, waiting 1, correct count per type | Served 1, still waiting 1; Consultation 1, Dental 1, Check-up 0 (run in code) | Pass |
| 17 | Storage | Save and load | Add records, close the app, reopen | Same patients and tickets, same statuses | 2 patients and 2 tickets saved and loaded, statuses kept (run in code) | Pass |
| 18 | Storage | Damaged file | Add a "garbage" line to tickets.txt, start | No crash; message about skipped lines | "Loaded 2 patients and 0 tickets. Skipped 2 damaged line(s)." (run in code) | Pass |
| 19 | Storage | Missing file | Delete the data folder, start | No crash; "No saved data" message | "No saved data found. Starting with an empty clinic." (run in code) | Pass |
| 20 | Screens | Button disabled until ready | Open Queue tab with empty fields | Register button is disabled until name and age are filled | Register button was disabled until name and age were filled in (observed in the app) | Pass |
| 21 | Screens | Exit confirmation | File > Exit, then close the window with X | Yes/No dialog; Yes saves and closes, No stays open | Yes/No dialog appeared; Yes saved and closed, No kept the window open (observed in the app) | Pass |
| 22 | Model | Unknown visit type | Visit.fromTypeName("Surgery") | InvalidInputException | InvalidInputException - "Unknown visit type: Surgery" (run in code) | Pass |
| 23 | Model | Visit minutes | Create ConsultationVisit, DentalVisit, CheckUpVisit | 15, 30 and 10 minutes | 15, 30 and 10 minutes (run in code) | Pass |
| 24 | Lead | Whole project compiles | javac -d out $(find src -name '*.java') | Compiles with no errors | Compiled with no errors (run in code) | Pass |

## Each member's own two tests

Rows 22-23 (Model), 2-3 and 8 (Logic), 18-19 (Storage), 13-14 and 20-21 (Screens), and 24 (Lead) cover the
two-per-member rule. Each owner should check that their rows are correct and add more if they like.
