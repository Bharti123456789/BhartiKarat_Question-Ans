package SinariobasedexapKarat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.*;

public class Solution {
    // ----------------------------
    // LogEntry
    // ----------------------------
    static class LogEntry {
        /**
         * Represents an entry from a single log line:
         * 34400.409 SXY288 210E ENTRY
         * tokens:
         * 0 -> timestamp (seconds)
         * 1 -> license plate
         * 2 -> location+direction (e.g., 210E or 260W)
         * 3 -> boothType (ENTRY/EXIT/MAINROAD)
         */
        private final float timestamp;
        private final String licensePlate;
        private final String boothType;
        private final int location;
        private final String direction; // "EAST" or "WEST"

        public LogEntry(String logLine) {
            String[] tokens = logLine.trim().split("\\s+");
            if (tokens.length != 4) {
                throw new IllegalArgumentException("Invalid log line: " + logLine);
            }

             this.timestamp = Float.parseFloat(tokens[0]);
            this.licensePlate = tokens[1];
            this.boothType = tokens[3];

            String locDir = tokens[2];
            this.location = Integer.parseInt(locDir.substring(0, locDir.length() - 1));

            String dirLetter = locDir.substring(locDir.length() - 1);
            if ("E".equals(dirLetter)) {
                this.direction = "EAST";
            } else if ("W".equals(dirLetter)) {
                this.direction = "WEST";
            } else {
                throw new IllegalArgumentException("Invalid direction in: " + logLine);
            }
        }

        public float getTimestamp() {
            return timestamp;
        }

        public String getLicensePlate() {
            return licensePlate;
        }

        public String getBoothType() {
            return boothType;
        }

        public int getLocation() {
            return location;
        }

        public String getDirection() {
            return direction;
        }

        @Override
        public String toString() {
            return String.format(
                    "<LogEntry timestamp: %.3f license: %s location: %d direction: %s booth: %s>",
                    timestamp, licensePlate, location, direction, boothType
            );
        }
    }
    // ----------------------------
    // LogFile (Problem 2 + Problem 3)
    // ----------------------------
    static class LogFile {
        /**
         Represents a file containing log lines converted to LogEntry objects.
         */
        private final List<LogEntry> logEntries;

        public LogFile(BufferedReader reader) throws IOException {
            this.logEntries = new ArrayList<>();
            String line = reader.readLine();
            while (line != null) {
                line = line.strip();
                if (!line.isEmpty()) {
                    logEntries.add(new LogEntry(line));
                }
                line = reader.readLine();
            }
        }

        public LogEntry get(int index) {
            return logEntries.get(index);
        }

        public int size() {
            return logEntries.size();
        }
        // ------------------------------------
        // ✅ Problem 2: countJourneys()
        // ------------------------------------
        /**
         * Returns how many complete journeys there are in the log.
         *
         * Assumption from prompt: log only contains complete journeys; no missing entries.
         * A journey ends at an EXIT, so #journeys == #EXIT lines.
         */
        public int countJourneys() {
            int count = 0;
            for (LogEntry e : logEntries) {
                if ("EXIT".equals(e.getBoothType())) {
                    count++;
                }
            }
            return count;
        }
//It counts an EXIT only if there was a matching ENTRY before it (for the same license plate).
//Uses a Set<String> onHighway to track cars that have entered but not yet exited.
//When an EXIT happens;
//if plate is present in onHighway → journey counted + removed
//if not present → ignored (EXIT without ENTRY)
//Use a Set to track cars currently “on the highway
// When a car does ENTRY, add it into the set → meaning: car is currently traveling.
//When a car does EXIT, we count a journey only if that car is in the set, i.e., it had a matching ENTRY.
//After counting, remove it from the set → meaning: journey completed.
        public int countJourneys() {
            int journeys = 0;

            // Tracks which cars are currently "on the highway" (after ENTRY, before EXIT)
            Set<String> onHighway = new HashSet<>();

            for (LogEntry e : logEntries) {
                String plate = e.getLicensePlate();
                String type = e.getBoothType();

                if ("ENTRY".equals(type)) {
                    // Start journey for this plate
                    onHighway.add(plate);

                } else if ("EXIT".equals(type)) {
                    // Count journey only if this EXIT has a matching ENTRY
                    if (onHighway.remove(plate)) {
                        journeys++;
                    }
                }// MAINROAD ignored for journey counting
            }
            return journeys;}


        // ------------------------------------
        // ✅ Problem 3: catchSpeeders()
        // ------------------------------------
        /**
         * For each active journey (ENTRY -> ... -> EXIT) track:
         * - last timestamp/location
         * - segments >=120 count
         * - whether already flagged as speeding
         */
        private static class JourneyState {
            double lastTimestamp;
            int lastLocation;
            int segmentsAtLeast120;
            boolean speeding;

            JourneyState(double ts, int loc) {
                this.lastTimestamp = ts;
                this.lastLocation = loc;
                this.segmentsAtLeast120 = 0;
                this.speeding = false;
            }
        }

        /**
         * Compute speed in km/h between two booths.
         * distance in km = abs(currLoc - prevLoc) (normally 10)
         * time in seconds = currTs - prevTs
         * speed = distance * 3600 / time
         */
        private static double speedKmh(int prevLoc, int currLoc, double prevTs, double currTs) {
            double deltaSec = currTs - prevTs;
            if (deltaSec <= 0) return 0.0; // defensive
            int distanceKm = Math.abs(currLoc - prevLoc);
            return (distanceKm * 3600.0) / deltaSec;
        }

        /**
         * Returns a collection (List) of license plates that drove at unsafe speeds.
         * Duplicate plates are allowed if they sped in multiple journeys.
         *
         * Unsafe journey conditions:
         * 1) any segment >= 130 km/h
         * 2) any two segments >= 120 km/h
         *
         * Only consider segments while on highway: between ENTRY and EXIT.
         * Count at most once per journey.
         */
        public List<String> catchSpeeders() {
            List<String> speeders = new ArrayList<>();

            // plate -> journey state (because entries for different plates can interleave)
            Map<String, JourneyState> active = new HashMap<>();

            for (LogEntry e : logEntries) {
                String plate = e.getLicensePlate();
                String type = e.getBoothType();

                if ("ENTRY".equals(type)) {
                    // Start a new journey
                    active.put(plate, new JourneyState(e.getTimestamp(), e.getLocation()));
                    continue;
                }

                if ("MAINROAD".equals(type)) {
                    JourneyState st = active.get(plate);
                    if (st == null) {
                        // Not on highway (no ENTRY seen), ignore as per prompt note
                        continue;
                    }

                    // Evaluate segment from last point to this MAINROAD
                    double speed = speedKmh(st.lastLocation, e.getLocation(), st.lastTimestamp, e.getTimestamp());

                    if (!st.speeding) {
                        if (speed >= 130.0) {
                            st.speeding = true;
                        } else if (speed >= 120.0) {
                            st.segmentsAtLeast120++;
                            if (st.segmentsAtLeast120 >= 2) {
                                st.speeding = true;
                            }
                        }
                    }

                    // Update last seen booth for this journey
                    st.lastTimestamp = e.getTimestamp();
                    st.lastLocation = e.getLocation();
                    continue;
                }

                if ("EXIT".equals(type)) {
                    JourneyState st = active.get(plate);
                    if (st == null) {
                        // Should not happen under "complete journeys" assumption, ignore safely
                        continue;
                    }

                    // Evaluate final segment from last point to EXIT
                    double speed = speedKmh(st.lastLocation, e.getLocation(), st.lastTimestamp, e.getTimestamp());

                    if (!st.speeding) {
                        if (speed >= 130.0) {
                            st.speeding = true;
                        } else if (speed >= 120.0) {
                            st.segmentsAtLeast120++;
                            if (st.segmentsAtLeast120 >= 2) {
                                st.speeding = true;
                            }
                        }
                    }

                    // If journey is speeding, add plate once for this journey
                    if (st.speeding) {
                        speeders.add(plate);
                    }

                    // Journey ends
                    active.remove(plate);
                }
            }

            return speeders;
        }
    }

    // ----------------------------
    // Minimal assert helpers (no JUnit needed)
    // ----------------------------
    private static void assertEqualsInt(int expected, int actual, String msg) {
        if (expected != actual) {
            throw new AssertionError(msg + " expected=" + expected + " actual=" + actual);
        }
    }

    private static void assertEqualsDouble(double expected, double actual, double eps, String msg) {
        if (Math.abs(expected - actual) > eps) {
            throw new AssertionError(msg + " expected=" + expected + " actual=" + actual);
        }
    }

    private static void assertEqualsString(String expected, String actual, String msg) {
        if (!Objects.equals(expected, actual)) {
            throw new AssertionError(msg + " expected=" + expected + " actual=" + actual);
        }
    }

    // ----------------------------
    // Tests / Demo
    // ----------------------------
    public static void main(String[] args) throws IOException {
        testLogEntry();
        testCountJourneys();
        testCatchSpeeders();
        System.out.println("✅ All tests passed.");
    }

    private static void testLogEntry() {
        System.out.println("Running testLogEntry");

        String logLine = "44776.619 KTB918 310E MAINROAD";
        LogEntry e = new LogEntry(logLine);

        assertEqualsDouble(44776.619, e.getTimestamp(), 0.0001, "timestamp mismatch");
        assertEqualsString("KTB918", e.getLicensePlate(), "license mismatch");
        assertEqualsInt(310, e.getLocation(), "location mismatch");
        assertEqualsString("EAST", e.getDirection(), "direction mismatch");
        assertEqualsString("MAINROAD", e.getBoothType(), "boothType mismatch");

        logLine = "52160.132 ABC123 400W ENTRY";
        e = new LogEntry(logLine);

        assertEqualsDouble(52160.132, e.getTimestamp(), 0.0001, "timestamp mismatch");
        assertEqualsString("ABC123", e.getLicensePlate(), "license mismatch");
        assertEqualsInt(400, e.getLocation(), "location mismatch");
        assertEqualsString("WEST", e.getDirection(), "direction mismatch");
        assertEqualsString("ENTRY", e.getBoothType(), "boothType mismatch");
    }

    private static void testCountJourneys() throws IOException {
        System.out.println("Running testCountJourneys");

        // 3 complete journeys:
        // JOX304: 1 journey
        // THX138: 2 journeys
        String data =
                "0.000 JOX304 100E ENTRY\n" +
                        "400.000 JOX304 110E MAINROAD\n" +
                        "800.000 JOX304 120E EXIT\n" +       // Journey 1

                        "1000.000 THX138 270W ENTRY\n" +
                        "1275.000 THX138 260W EXIT\n" +      // Journey 2

                        "2000.000 THX138 300E ENTRY\n" +
                        "2300.000 THX138 310E MAINROAD\n" +
                        "2600.000 THX138 320E EXIT\n";       // Journey 3

        LogFile logFile = new LogFile(new BufferedReader(new StringReader(data)));
        assertEqualsInt(8, logFile.size(), "log size mismatch");
        assertEqualsInt(3, logFile.countJourneys(), "countJourneys mismatch");
    }

    private static void testCatchSpeeders() throws IOException {
        System.out.println("Running testCatchSpeeders");

        // JOX304 journey: not speeding (90 km/h segments)
        // THX138 journey #1: 10km in 275s => ~130.91 km/h => speeding
        // THX138 journey #2: two segments >=120 => speeding
        String data =
                "0.000 JOX304 100E ENTRY\n" +
                        "400.000 JOX304 110E MAINROAD\n" +  // 10km/400s => 90 km/h
                        "800.000 JOX304 120E EXIT\n" +      // 10km/400s => 90 km/h

                        "1000.000 THX138 270W ENTRY\n" +
                        "1275.000 THX138 260W EXIT\n" +     // 10km/275s => 130.91 => speeding

                        "2000.000 THX138 300E ENTRY\n" +
                        "2300.000 THX138 310E MAINROAD\n" + // 10km/300s => 120.00 => segment #1
                        "2599.000 THX138 320E EXIT\n";      // 10km/299s => 120.40 => segment #2 => speeding

        LogFile logFile = new LogFile(new BufferedReader(new StringReader(data)));
        List<String> speeders = logFile.catchSpeeders();

        // THX138 should appear twice (two journeys)
        assertEqualsInt(2, speeders.size(), "speeders size mismatch");
        assertEqualsString("THX138", speeders.get(0), "first speeder mismatch");
        assertEqualsString("THX138", speeders.get(1), "second speeder mismatch");
    }
}