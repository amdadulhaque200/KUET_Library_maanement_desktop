package org.example.kuet_library_management_desktop;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class StudentRepository {
    private final List<Student> students = new ArrayList<>();

    public StudentRepository() {
        students.add(new Student("S1001", "Alice Rahman", "alice@example.com", 2));
        students.add(new Student("S1002", "Bob Karim", "bob@example.com", 0));
        students.add(new Student("S1003", "Chen Li", "chen@example.com", 1));
        students.add(new Student("S1004", "Dina Noor", "dina@example.com", 3));
    }

    public List<Student> getAll() { return new ArrayList<>(students); }
    public Optional<Student> findById(String id) {
        return students.stream().filter(s -> s.getId().equals(id)).findFirst();
    }
}

