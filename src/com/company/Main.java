package com.company;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.text.DecimalFormat;

public class Main {

    static Map<Integer, Employee> empList;
    static List<Employee> topOfHierarchy;
    static List<Employee> moreManagersList;

    public static void main(String[] args) {
        empList = new HashMap<Integer, Employee>();
        topOfHierarchy = new ArrayList<>();
        moreManagersList = new ArrayList<>();
        ReadCSVFile();
        CreateHierarchy();
        for (int x = 0; x < topOfHierarchy.size(); x++) {
            PrintHierarchy(topOfHierarchy.get(x), 0);
        }
        PrintMoreManagersList();
    }

    private static void ReadCSVFile() {
        File payroll1 = new File("payroll1.csv");

        try {
            Scanner read = new Scanner(payroll1);

            while (read.hasNextLine()) {
                String str = read.nextLine();
                String[] rowArray = (str.split(","));
                String name = rowArray[0];
                int ID = Integer.parseInt(rowArray[1]);
                double salary = Double.parseDouble(rowArray[2]);

                if (rowArray.length == 4) {
                    int managerID = Integer.parseInt(rowArray[3]);
                    Employee emp = new Employee(name, ID, salary, managerID);

                    empList.put(emp.getID(), emp);
                } else if (rowArray.length == 3) {
                    Employee emp = new Employee(name, ID, salary);
                    topOfHierarchy.add(emp);

                    empList.put(emp.getID(), emp);
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Payroll 1 Not Read");
        }
    }

    private static void CreateHierarchy() {

        for (Employee e : empList.values()) {

            List<Employee> team = new ArrayList<>();

            for (Employee teamMember : empList.values()) {
                if (e.getID() == teamMember.getManagerID())
                    team.add(teamMember);
            }

            e.setCumulativeSalary(GetCumulativeSalary(team, e));

            e.setTeamMemberList(team);
        }
    }

    private static double GetCumulativeSalary(List<Employee> team, Employee manager) {
        double cumulativeTeamSalary = 0.00;

        for (Employee emp : team) {
            cumulativeTeamSalary += emp.getSalary();
        }

        if(team.size() != 0)
        {
            cumulativeTeamSalary += manager.getSalary();
        }

        return cumulativeTeamSalary;
    }

    private static void PrintHierarchy(Employee topOfHierarchy, int level) {
        topOfHierarchy.sethierarchyLevel(level);
        for (int i = 0; i < level; i++) {
            System.out.print("\t");
        }
        System.out.print(topOfHierarchy.getName());

        if (topOfHierarchy.getCumulativeSalary() != 0.0) {
            DecimalFormat df = new DecimalFormat("#.00");
            System.out.print("\tCumulative Team Salary: £" + df.format(topOfHierarchy.getCumulativeSalary()));
        }

        System.out.print("\n");

        List<Employee> teamMembers = topOfHierarchy.getTeamMemberList();
        if (level > teamMembers.size()) {
            moreManagersList.add(topOfHierarchy);
        }
        for (Employee e : teamMembers) {
            PrintHierarchy(e, level + 1);
        }
    }

    private static void PrintMoreManagersList() {
        System.out.println("\nList of people that have more managers than employee's\n\n");
        for (Employee emp : moreManagersList) {
            System.out.println(emp.getName() + "\t\t\tNumber of Employee's: " + emp.getTeamMemberList().size()
                    + "\t\t\tNumber of Managers: " + emp.gethierarchyLevel());
        }
    }
}