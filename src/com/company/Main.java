package com.company;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.text.DecimalFormat;

public class Main {

    static Map<Integer, Employee> empList;
    static List<Employee> topOfHierarchy;
    static List<Employee> moreManagersList;
    static List<Employee> visited;

    public static void main(String[] args) {
        empList = new HashMap<Integer, Employee>();
        topOfHierarchy = new ArrayList<>();
        moreManagersList = new ArrayList<>();
        visited = new ArrayList<>();
        if(ReadCSVFile() == true) {
            if(CreateHierarchy() == true)
            {
                for (int x = 0; x < topOfHierarchy.size(); x++) {
                    BuildHierarchyForCycleCheck(topOfHierarchy.get(x), 0);
                }

                if(CheckForCycle() == false)
                {
                    System.out.println("\nCompany Hierarchy:\n");
                    for (int x = 0; x < topOfHierarchy.size(); x++) {
                        PrintHierarchy(topOfHierarchy.get(x), 0);
                    }
                    PrintMoreManagersList();
                    PrintCumulativeSalaries();
                }
            }
        }
        System.out.println(topOfHierarchy.get(0).getName());
        System.out.println(topOfHierarchy.get(0).getTeamMemberList());
        System.out.println(topOfHierarchy.get(0).totalEmployeesDelegated());
    }

    private static boolean ReadCSVFile() {
        boolean isRead = true;
        File payroll = new File("payroll1.csv");

        try {
            Scanner read = new Scanner(payroll);

            //reads in line by line
            while (read.hasNextLine()) {
                String str = read.nextLine();
                //splits the line by the commas
                String[] rowArray = (str.split(","));
                String name = rowArray[0];

                int ID = 0;
                double salary = 0.0;

                //validation for making sure the value being parsed is a valid int
                try {
                    ID = Integer.parseInt(rowArray[1]);
                } catch (NumberFormatException ex) {
                    System.out.println("ID in CSV file was not a valid integer");
                }

                //validation for making sure the value being parsed is a valid double
                try {
                    salary = Double.parseDouble(rowArray[2]);
                } catch (NumberFormatException ex) {
                    System.out.println("Salary in CSV file was not a valid integer");
                }

                //checks to see if the employee has a manager ID or not and calls the correct constructor
                if (rowArray.length == 4) {
                    int managerID = Integer.parseInt(rowArray[3]);
                    Employee emp = new Employee(name, ID, salary, managerID);

                    //puts them in the list
                    empList.put(emp.getID(), emp);
                } else if (rowArray.length == 3) {
                    Employee emp = new Employee(name, ID, salary);
                    topOfHierarchy.add(emp);

                    //puts them in the list
                    empList.put(emp.getID(), emp);
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println(payroll.getPath() + " Not Read");
            isRead = false;
        }

        return isRead;
    }

    private static boolean CreateHierarchy() {

        boolean isWellFormed = false;

        for (Employee e : empList.values()) {

            //checks everyone's manager ID links to another person in the list
            if (ValidateManager(e) == false)
            {
                System.out.println("Hierarchy is not well formed because " + e.getName() + " does not have a valid manager");
                isWellFormed = false;
                break;
            }
            else {
                List<Employee> team = new ArrayList<>();

                //assigns each employee their team member
                for (Employee teamMember : empList.values()) {

                    if(e.getID() == teamMember.getManagerID())
                    {
                        team.add(teamMember);
                    }
                }

                //Stores the employee's team Cumulative Salary
                e.setCumulativeSalary(GetCumulativeSalary(team, e));

                //Stores the employee's team members
                e.setTeamMemberList(team);

                isWellFormed = true;
            }
        }
        return isWellFormed;
    }

    private static double GetCumulativeSalary(List<Employee> team, Employee manager) {
        double cumulativeTeamSalary = 0.00;

        //loops through the managers team and adds together all the salaries
        for (Employee emp : team) {
            cumulativeTeamSalary += emp.getSalary();
        }

        //adds the managers salary onto the total value
        if (team.size() != 0) {
            cumulativeTeamSalary += manager.getSalary();
        }

        return cumulativeTeamSalary;
    }

    private static void PrintHierarchy(Employee topOfHierarchy, int level) {

        //prints a "tab" for each level down the employee is in the hierarchy
        topOfHierarchy.sethierarchyLevel(level);
        for (int i = 0; i < level; i++) {
            System.out.print("\t");
        }
        System.out.print(topOfHierarchy.getName() + ", " + topOfHierarchy.getID() + ", £" + topOfHierarchy.getSalary());

        System.out.print("\n");

        List<Employee> teamMembers = topOfHierarchy.getTeamMemberList();

        if (level > teamMembers.size()) {
            moreManagersList.add(topOfHierarchy);
        }
        //used to build all the hierarchy underneath each employee
        for (Employee e : teamMembers) {
                PrintHierarchy(e, level + 1);
            }
    }

    private static void PrintMoreManagersList() {
        System.out.println("\nList of managers that have more managers than employee's:\n");
        for (Employee emp : moreManagersList) {
            if (emp.teamMemberList.size() > 0) {
                System.out.println(emp.getName() + ", " + emp.getID() + "\t\t\tNumber of Managers: " + emp.gethierarchyLevel()
                        + "\t\t\tNumber of Employee's: " + emp.getTeamMemberList().size());
            }
        }
    }

    private static void PrintCumulativeSalaries() {

        double maxCumulativeSal = 0.0;

        System.out.println("\nList of Cumulative Team Salaries:\n");

        for (Map.Entry<Integer, Employee> entry : empList.entrySet()) {
            Integer key = entry.getKey();
            Employee value = entry.getValue();

            //makes sure it only prints valid salaries(e.g not £0)
            if (value.getCumulativeSalary() != 0.0) {
                DecimalFormat df = new DecimalFormat("#.00");
                System.out.println("Team " + value.getName() + "\t\t\tCumulative Team Salary: £" + df.format(value.getCumulativeSalary()));

                //finds which team has the highest cumulative salary
                if (value.getCumulativeSalary() > maxCumulativeSal) {
                    maxCumulativeSal = value.getCumulativeSalary();
                }
            }
        }

        PrintMaxCumulativeSal(maxCumulativeSal);
    }

    private static void PrintMaxCumulativeSal(double maxCumulativeSal) {
        System.out.print("\nThe Maximum Cumulative Salary is £" + maxCumulativeSal + "\n");
    }

    private static boolean ValidateManager(Employee emp) {
        boolean validManager = false;

        //checks to ensure that each employees manager ID matches another employee in the list
        for (Map.Entry<Integer, Employee> entry : empList.entrySet()) {
            Integer key = entry.getKey();
            Employee value = entry.getValue();
            //Takes into account that the 2 most senior employees dont have a manager ID
            if(emp.getManagerID() == value.getID() || emp.getManagerID() == 0)
            {
                validManager = true;
                break;
            }
        }

        return validManager;
    }

    private static boolean CheckForCycle() {

        //loops through the employees and finds any that weren't built as part of the hierarchy
        for (Employee emp : empList.values()) {
            if (emp.getIsPrinted() == false) {
                //checks the employee's team to see if any of them have already been used higher up in the hierarchy (indicating a cycle)
                for (Employee teamMember : emp.getTeamMemberList()) {
                    if (visited.contains(teamMember)) {
                        System.out.println("Hierarchy was not well formed. A management cycle was detected");
                        return true;
                    }
                }
                visited.add(emp);
            }
        }
        return false;
    }

    private static void BuildHierarchyForCycleCheck(Employee topOfHierarchy, int level) {

        topOfHierarchy.sethierarchyLevel(level);

        //loops through employees list to find the passed employee so it can set the IsPrinted value of the object in the list
        for (Employee emp : empList.values()) {
            if(emp.getID() == topOfHierarchy.getID())
            {
                emp.setIsPrinted(true);
                break;
            }
        }

        visited.add(topOfHierarchy);

        List<Employee> teamMembers = topOfHierarchy.getTeamMemberList();

        //used to build all the hierarchy underneath each employee
        for (Employee e : teamMembers) {
            BuildHierarchyForCycleCheck(e, level + 1);
        }
    }
}