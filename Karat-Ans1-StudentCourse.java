import java.util.*;

public class SharedCourses {

    public static void main(String[] args) {
        List<String[]> enrollmentList = new ArrayList<>();
        Scanner scanner = new Scanner(System.in);
        
        System.out.print("Enter the number of enrollments: ");
        int totalEnrollments = Integer.parseInt(scanner.nextLine());
        
        for(int i=1; i<=totalEnrollments ; i++) {
            System.out.println("Enter Enrollment " + i + " details.");
            System.out.print("Student Id: ");
            String studentId = scanner.nextLine();
            System.out.print("Course Name: ");
            String course = scanner.nextLine();
            
            String[] newEnrollment = new String[]{studentId, course};
            enrollmentList.add(newEnrollment);
        }
        scanner.close();
        
        Map<String, List<String>> studentCourseMap = getSharedCourses(enrollmentList);
        
        System.out.println("\nShared courses between students:");
        for(Map.Entry<String, List<String>> entry : studentCourseMap.entrySet()) {
            System.out.println(entry.getKey() + " ->" + entry.getValue());
        }
    }
    
    public static Map<String, List<String>> getSharedCourses(List<String[]> enrollmentList) {
        Map<String, Set<String>> studentCourseMap = new HashMap<>();
        
        for(String[] enrollment : enrollmentList) {
            String studentId = enrollment[0];
            String course = enrollment[1];
            
            studentCourseMap.computeIfAbsent(studentId, k -> new HashSet<>()).add(course);
        }
        
        //Get shared courses for each pair of students
        Map<String, List<String>> sharedCourseMap = new HashMap<>();
        List<String> studentsList = new ArrayList<>(studentCourseMap.keySet());
        
        for(int i=0 ; i<studentsList.size() ; i++) {
            for(int j=i+1 ; j<studentsList.size() ; j++) {
                String student1 = studentsList.get(i);
                String student2 = studentsList.get(j);
                
                Set<String> sharedCourses = new HashSet<>(studentCourseMap.get(student1));
                sharedCourses.retainAll(studentCourseMap.get(student2));
                
                sharedCourseMap.put(student1 + "," + student2, new ArrayList<>(sharedCourses));
            }
        }
        return sharedCourseMap;
    }
}