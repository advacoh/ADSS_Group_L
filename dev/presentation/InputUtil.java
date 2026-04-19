package dev.presentation;

import dev.domain.Certification;
import dev.domain.EmpType;
import dev.domain.SalType;
import dev.domain.ShiftType;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

public class InputUtil {

    private static final Scanner scanner = new Scanner(System.in);

    public static int readInt() {
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (Exception e) {
                System.out.print("Invalid input: ");
            }
        }
    }

    public static String readString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    public static int readInt(String prompt) {
        System.out.print(prompt);
        return readInt();
    }

    public static LocalDate readDate() {
        while (true) {
            System.out.print("Enter date (dd/MM/yyyy): ");
            String input = scanner.nextLine().trim();
            try {
                return LocalDate.parse(input, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            } catch (Exception e) {
                System.out.println("Invalid date format. Please use dd/MM/yyyy.");
            }
        }
    }

    public static String readRaw() {
        return scanner.nextLine().trim();
    }

    // Add to InputUtil

    public static LocalDate readDayOfWeek() {
        System.out.println("Select a day of the week:");
        System.out.println("1) Sunday");
        System.out.println("2) Monday");
        System.out.println("3) Tuesday");
        System.out.println("4) Wednesday");
        System.out.println("5) Thursday");
        System.out.println("6) Friday");
        System.out.println("7) Saturday");

        while (true) {
            int choice = readInt();
            DayOfWeek day = switch (choice) {
                case 1 -> DayOfWeek.SUNDAY;
                case 2 -> DayOfWeek.MONDAY;
                case 3 -> DayOfWeek.TUESDAY;
                case 4 -> DayOfWeek.WEDNESDAY;
                case 5 -> DayOfWeek.THURSDAY;
                case 6 -> DayOfWeek.FRIDAY;
                case 7 -> DayOfWeek.SATURDAY;
                default -> null;
            };
            if (day != null) 
                return nextOccurrence(day);
            System.out.println("Invalid option.");
        }
    }

    private static LocalDate nextOccurrence(DayOfWeek day) {
        LocalDate today = LocalDate.now();
        int daysUntil = (day.getValue() - today.getDayOfWeek().getValue() + 7) % 7;
        return today.plusDays(daysUntil == 0 ? 7 : daysUntil);
    }

    public static ShiftType readShiftType() {
        while (true) {
            System.out.print("Select shift type (M)orning or (E)vening: ");
            ShiftType type = ShiftType.fromValue(readRaw());
            if (type != null) return type;
            System.out.println("Please enter M or E.");
        }
    }

    public static Certification readRole() {
        Certification[] roles = Certification.values();
        System.out.println("Select role:");
        for (int i = 0; i < roles.length; i++) {
            System.out.println((i + 1) + ") " + roles[i].getValue());
        }
        while (true) {
            int choice = readInt() - 1;
            if (choice >= 0 && choice < roles.length) return roles[choice];
            System.out.println("Invalid option.");
        }
    }

    public static <T> T selectItem(List<T> items) {
        for (int i = 0; i < items.size(); i++) {
            System.out.println((i + 1) + ") " + items.get(i));
        }
        System.out.print("Select (0 to cancel): ");
        while (true) {
            int choice = readInt();
            if (choice == 0) return null;
            if (choice >= 1 && choice <= items.size()) return items.get(choice - 1);
            System.out.println("Invalid option.");
        }
    }

    public static int readEmployeeId() {
        System.out.print("Enter employee ID: ");
        return readInt();
    }

    public static SalType readSalType() {
        System.out.println("Salary Type:");
        System.out.println("1) Global");
        System.out.println("2) Hourly");
        while (true) {
            int choice = readInt("Choice: ");
            if (choice == 1) return SalType.GLOBAL;
            if (choice == 2) return SalType.HOURLY;
            System.out.println("Invalid option. Please enter 1 or 2.");
        }
    }

    public static EmpType readEmpType() {
        System.out.println("Employment Type:");
        System.out.println("1) Full Time");
        System.out.println("2) Part Time");
        while (true) {
            int choice = readInt("Choice: ");
            if (choice == 1) return EmpType.FULL_TIME;
            if (choice == 2) return EmpType.PART_TIME;
            System.out.println("Invalid option. Please enter 1 or 2.");
        }
    }
}