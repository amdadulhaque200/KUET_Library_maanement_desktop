package org.example.kuet_library_management_desktop;

public class SessionManager {
    private static Student currentStudent;
    private static Admin currentAdmin;

    public static Student getCurrentStudent() {
        return currentStudent;
    }

    public static void setCurrentStudent(Student student) {
        currentStudent = student;
        currentAdmin = null; // Clear admin session
    }

    public static Admin getCurrentAdmin() {
        return currentAdmin;
    }

    public static void setCurrentAdmin(Admin admin) {
        currentAdmin = admin;
        currentStudent = null; // Clear student session
    }

    public static void clearSession() {
        currentStudent = null;
        currentAdmin = null;
    }

    public static boolean isStudentLoggedIn() {
        return currentStudent != null;
    }

    public static boolean isAdminLoggedIn() {
        return currentAdmin != null;
    }
}

