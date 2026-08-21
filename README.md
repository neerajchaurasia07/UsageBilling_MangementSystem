# UsageBilling_MangementSystem
# Resource Usage and Billing System

A clean and simple Java console application to track resource allocation, limit capacity, and compute hourly rounded bills.

---

## a. Explanation of the Data Structures Used
* **`HashMap<String, Facility>`**: Maps `facilityId` to its object for $O(1)$ fast lookup of pricing and capacity details.
* **`HashMap<String, UserSession>`**: Stores active sessions by `user` key to prevent multiple concurrent sessions and enable quick session checkout.
* **`LocalDateTime` & `Duration`**: Used to record accurate start/end timestamps and calculate session length in minutes.

---

## b. Overview of the Logic and Approach
1. **Capacity Management**:
   * Each facility keeps count of `currentUsers`.
   * When `startUsage` is triggered, it verifies `currentUsers < capacity`. If space exists, it increments the count; otherwise, the request is rejected.
   * On `stopUsage`, `currentUsers` is decremented.

2. **Rounding & Pricing Logic**:
   * Elapsed time is measured in minutes via `Duration.between()`.
   * Round-up math: `(minutes + 59) / 60` ensures any non-zero minutes count toward the next full hour.
   * Billing: `Total = baseRate + ((billableHours - 1) * extraRate)`.

---

## c. Steps on How to Run and Test the Code
1. Compile:
   ```bash
   javac Main.java
