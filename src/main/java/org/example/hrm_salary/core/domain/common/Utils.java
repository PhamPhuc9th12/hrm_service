package org.example.hrm_salary.core.domain.common;

public class Utils {
    public static String buildSearch(String searchString) {
        if (searchString == null || searchString.isEmpty()) {
            return "%";
        }
        return "%" + searchString + "%";
    }
}