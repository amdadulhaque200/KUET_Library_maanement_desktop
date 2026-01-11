package org.example.kuet_library_management_desktop.service;

import java.util.Arrays;
import java.util.List;

/**
 * Minimal student service. Replace with real data access.
 */
public class StudentService {

    // TODO: integrate with your repository / database to fetch real rolls.
    public static List<String> getAllStudentRolls() {
        return Arrays.asList(
                "2019-001", "2019-002", "2019-010",
                "2020-100", "2020-101",
                "2021-050", "2021-051",
                "2022-200", "2022-201"
        );
    }
}
