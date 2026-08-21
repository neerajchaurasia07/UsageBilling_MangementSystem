import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


class pricing {
    double base;
    double extra;

    public pricing(double base, double extra) {
        this.base = base;
        this.extra = extra;
    }
}


class facility {
    String name;
    int capacity;
    int users;
    pricing pricing;

    public facility(String name, int capacity, pricing pricing) {
        this.name = name;
        this.capacity = capacity;
        this.pricing = pricing;
        this.users = 0;
    }
}


class Session {
    String user;
    String facid;
    LocalDateTime time;

    public Session(String user, String facilityId, LocalDateTime startTime) {
        this.user = user;
        this.facid = facilityId;
        this.time = startTime;
    }
}


class BillingSystem {
    Map<String, facility> facilities = new HashMap<>();
    Map<String, Session> activeSessions = new HashMap<>();
    DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public void addFacility(String id, String name, int capacity, double base, double extra) {
        facilities.put(id, new facility(name, capacity, new pricing(base, extra)));
    }

    public void startUsage(String user, String facilityId, LocalDateTime time) {
        if (activeSessions.containsKey(user)) {
            System.out.println("Error: " + user + " already has an ongoing session.");
            return;
        }

        facility f = facilities.get(facilityId);
        if (f == null) {
            System.out.println("Error: Facility not found.");
            return;
        }

        if (f.users >= f.capacity) {
            System.out.println("Rejected: " + f.name + " is fully occupied (" + f.capacity + "/" + f.capacity + ")");
            return;
        }

        f.users++;
        activeSessions.put(user, new Session(user, facilityId, time));
        System.out.println("Started: " + user + " checked into " + f.name + " at " + time.format(fmt));
    }

    public void stopUsage(String user, LocalDateTime endTime) {
        Session session = activeSessions.get(user);
        if (session == null) {
            System.out.println("Error: No active session found for " + user);
            return;
        }

        facility f = facilities.get(session.facid);
        f.users--;
        activeSessions.remove(user);

        
        long minutes = Duration.between(session.time, endTime).toMinutes();
        if (minutes <= 0) minutes = 1;

    
        long billableHours = (minutes + 59) / 60;

        double bill = f.pricing.base;
        if (billableHours > 1) {
            bill += (billableHours - 1) * f.pricing.extra;
        }

        
        System.out.println("\n---------------- BILL INVOICE ----------------");
        System.out.println("User         : " + user);
        System.out.println("Facility     : " + f.name);
        System.out.println("Start Time   : " + session.time.format(fmt));
        System.out.println("End Time     : " + endTime.format(fmt));
        System.out.println("Duration     : " + minutes + " mins (Billed as " + billableHours + " hr)");
        System.out.println("Total Amount : Rs. " + bill);
        System.out.println("----------------------------------------------\n");
    }
}

public class Main {
    public static void main(String[] args) {
        BillingSystem app = new BillingSystem();

        
        app.addFacility("CONF_01", "Meeting Room A", 2, 30.0, 10.0);
        app.addFacility("GYM_01", "Treadmill 1", 1, 50.0, 20.0);

        System.out.println("=== TEST CASE 1: Assignment Example (1 hr 20 mins) ===");
        LocalDateTime t1 = LocalDateTime.of(2026, 8, 21, 10, 0, 0);
        LocalDateTime t2 = LocalDateTime.of(2026, 8, 21, 11, 20, 0);

        app.startUsage("Rahul", "CONF_01", t1);
        app.stopUsage("Rahul", t2);

        System.out.println("=== TEST CASE 2: Capacity Rejection Test ===");
        LocalDateTime now = LocalDateTime.now();
        app.startUsage("User1", "GYM_01", now);
        app.startUsage("User2", "GYM_01", now); // Should be rejected (capacity = 1)

        app.stopUsage("User1", now.plusMinutes(40)); // Bill for 1 hr
        app.startUsage("User2", "GYM_01", now.plusMinutes(45)); // Allowed now
    }
}