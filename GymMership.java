package SinariobasedexapKarat;

import java.util.*;
import org.junit.Test;
import static org.junit.Assert.*;

class Workout {

    /**

     * This class represents a single workout session for a member.

     * Each object of the Workout class has a unique ID, as well as

     * a start time and end time that are represented in the number

     * of minutes spent from the start of the day.

     */

    private int id;

    private int startTime;

    private int endTime;

    public Workout(int id, int startTime, int endTime) {

        this.id = id;

        this.startTime = startTime;

        this.endTime = endTime;

    }

    public int getId() {

        return id;

    }

    public int getStartTime() {

        return startTime;

    }

    public int getEndTime() {

        return endTime;

    }

    public int getDuration() {

        return endTime - startTime;

    }

}


enum MembershipStatus {

    /*

        Membership Status is of three types: BRONZE, SILVER and GOLD.

        BRONZE is the default membership a new member gets.

        SILVER and GOLD are paid memberships for the gym.

    */

    BRONZE,

    SILVER,

    GOLD

}

class Member {

    /* Data about a gym member.*/

    public int memberId;

    public String name;

    public MembershipStatus membershipStatus;
 //In Member you declared this, Issue- wrong type + wrong constructor
    public List<Workout> workout;//issue fixed


//In your tests we call:But don’t have that 3‑argument constructor, so it won’t compile.
// Provide a 3‑arg constructor, and initialize the list.
    public Member(int memberId, String name, MembershipStatus membershipStatus) {

        this.memberId = memberId;

        this.name = name;

        this.membershipStatus = membershipStatus;

        //fix issue here
        //Field is List, but constructor parameter is Workout (single).
        // Member should store a list of workouts, not a single workout.
        //this.workout = workout;
        this.workout =new ArrayList<>();

    }

    @Override

    public String toString() {

        return "Member ID: " + memberId + ", Name: " + name + ", Membership Status: " + membershipStatus;

    }

}

class Membership {
    /*
        Data for managing a gym membership, and methods which staff can

        use to perform any queries or updates.
    */

    public List<Member> members;
    public Membership() {

        members = new ArrayList<>();
    }

    public void addMember(Member member) {
        members.add(member);

    }
    public void updateMembership(int memberId, MembershipStatus membershipStatus) {

        for (Member member : members) {

            if (member.memberId == memberId) {

                member.membershipStatus = membershipStatus;

                break;

        }}}

    public void addWorkout(int id,Workout workout){ 
        // added this method to add workout time. Also explained how to calculate average workout time

        for(Member member : members){

            if(member.memberId == id){
                //fix issue here
     //overwrites workout instead of adding, Even if types matched, this replaces the previous workout instead of adding it.
               // member.workout = workout;

                member.workout.add(workout);
                return;

                //break;

            }}}

    public MembershipStatistics getMembershipStatistics() {

        int totalMembers = members.size();

        int totalPaidMembers = 0;

        for (Member member : members) {

            if (member.membershipStatus == MembershipStatus.GOLD || /*added this check to work failing test case */

                    member.membershipStatus == MembershipStatus.SILVER) {

                totalPaidMembers++;

            }

        }

        double conversionRate = (totalPaidMembers / (double) totalMembers) * 100.0;

        return new MembershipStatistics(totalMembers, totalPaidMembers, conversionRate);

    }

    // We create an empty Map to store memberId → average workout duration.
//We loop through each member in the membership list.
//If a member has no workouts, we skip that member (because average can’t be calculated).
//For members with workouts, we calculate the total duration by adding endTime - startTime for every workout.
//Then we compute the average as: totalDuration / numberOfWorkouts.
//Finally, we put the result into the map using the member’s id as the key.
//After processing all members, we return the map containing average workout durations.
//✅ In short: For each member, sum all workout durations, divide by count, store average in a map.
    public Map<Integer, Double> getAverageWorkoutDurations() {
            Map<Integer, Double> result = new HashMap<>();

            for (Member member : members) {
                if (member.workout.isEmpty()) {
                    continue; // optional: don't include members with no workouts
                }

                int total = 0;
                for (Workout w : member.workout) {
                    total += w.getDuration();
                }

                double avg = total / (double) member.workout.size();
                result.put(member.memberId, avg);
            }

            return result;
        }


}

class MembershipStatistics {

    /*

        Class for returning the getMembershipStatistics result

    */

    public int totalMembers;

    public int totalPaidMembers;

    public double conversionRate;

    public MembershipStatistics(int totalMembers, int totalPaidMembers, double conversionRate) {

        this.totalMembers = totalMembers;

        this.totalPaidMembers = totalPaidMembers;

        this.conversionRate = conversionRate;

    }

}

public class Solutionss {

    /*

        This is not a complete test suite, but tests some basic functionality of

        the code and shows how to use it.

    */

    public static void main(String[] args) {

        testMember();

        testMembership();

        testGetAverageWorkoutDurations();

    }

    public static void testMember() {

        System.out.println("Running testMember");

        Member testMember = new Member(1, "John Doe", MembershipStatus.BRONZE);

        assert testMember.memberId == 1 :

                "member ID should be 1, was " + testMember.memberId;

        assert testMember.name.equals("John Doe") :

                "member name should be \"John Doe\", was \"" + testMember.name + "\"";

        assert testMember.membershipStatus == MembershipStatus.BRONZE :

                "membership status should be BRONZE, was " + testMember.membershipStatus;

    }

    public static void testMembership() {

        System.out.println("Running testMembership");

        Membership testMembership = new Membership();

        Member testMember = new Member(1, "John Doe", MembershipStatus.BRONZE);

        testMembership.addMember(testMember);

        assert testMembership.members.size() == 1 :

                "members size should be 1, was " + testMembership.members.size();

        assert testMembership.members.get(0).equals(testMember) :

                "first member should equal testMember";

        testMembership.updateMembership(1, MembershipStatus.SILVER);

        assert testMembership.members.get(0).membershipStatus == MembershipStatus.SILVER :

                "membership status should be SILVER, was " + testMembership.members.get(0).membershipStatus;

        Member testMember2 = new Member(2, "Alex C", MembershipStatus.BRONZE);

        testMembership.addMember(testMember2);

        Member testMember3 = new Member(3, "Marie C", MembershipStatus.GOLD);

        testMembership.addMember(testMember3);

        Member testMember4 = new Member(4, "Joe D", MembershipStatus.SILVER);

        testMembership.addMember(testMember4);

        Member testMember5 = new Member(5, "June R", MembershipStatus.BRONZE);

        testMembership.addMember(testMember5);

        MembershipStatistics attendanceStats = testMembership.getMembershipStatistics();

        assert attendanceStats.totalMembers == 5 :

                "total members should be 5, was " + attendanceStats.totalMembers;

        assert attendanceStats.totalPaidMembers == 3 :

                "total paid members should be 3, was " + attendanceStats.totalPaidMembers;

        assert Math.abs(attendanceStats.conversionRate - 60.00) < 0.1 :

                "conversion rate should be 60.00, was " + attendanceStats.conversionRate;

    }

    public static void testGetAverageWorkoutDurations() {

        System.out.println("Running testGetAverageWorkoutDurations");

        Membership testMembership = new Membership();

        Member testMember1 = new Member(12, "John Doe", MembershipStatus.SILVER);

        testMembership.addMember(testMember1);

        Member testMember2 = new Member(22, "Alex Cleeve", MembershipStatus.BRONZE);

        testMembership.addMember(testMember2);

        Member testMember3 = new Member(31, "Marie Cardiff", MembershipStatus.GOLD);

        testMembership.addMember(testMember3);

        Member testMember4 = new Member(37, "George Costanza", MembershipStatus.SILVER);

        testMembership.addMember(testMember4);

        Workout testWorkout1 = new Workout(11, 10, 20);

        Workout testWorkout2 = new Workout(24, 15, 35);

        Workout testWorkout3 = new Workout(32, 45, 90);

        Workout testWorkout4 = new Workout(47, 100, 155);

        Workout testWorkout5 = new Workout(56, 120, 200);

        Workout testWorkout6 = new Workout(62, 300, 400);

        Workout testWorkout7 = new Workout(78, 1000, 1010);

        Workout testWorkout8 = new Workout(80, 1010, 1045);

        testMembership.addWorkout(12, testWorkout1);

        testMembership.addWorkout(22, testWorkout2);

        testMembership.addWorkout(31, testWorkout3);

        testMembership.addWorkout(12, testWorkout4);

        testMembership.addWorkout(22, testWorkout5);

        testMembership.addWorkout(31, testWorkout6);

        testMembership.addWorkout(12, testWorkout7);

        testMembership.addWorkout(4, testWorkout8);

        Map<Integer, Double> averageDurations = testMembership.getAverageWorkoutDurations();

        assert Math.abs(averageDurations.get(12) - 25.0) < 0.1 :

                "average duration for member 12 should be 25.0, was " + averageDurations.get(12);

        assert Math.abs(averageDurations.get(22) - 50.0) < 0.1 :

                "average duration for member 22 should be 50.0, was " + averageDurations.get(22);

        assert Math.abs(averageDurations.get(31) - 72.5) < 0.1 :

                "average duration for member 31 should be 72.5, was " + averageDurations.get(31);

        assertFalse(averageDurations.containsKey(4));

    }

}

