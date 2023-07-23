package com.company;

import java.util.ArrayList;
import java.util.List;

public class Employee implements Delegate {
    private String name;
    private int ID;
    private double salary;
    private int managerID;
    private int hierarchyLevel;
    private double cumulativeSalary;
    private boolean isPrinted;
    List<Employee> teamMemberList;

    public Employee(String name, int ID, double salary, int managerID)
    {
        this.name = name;
        this.ID = ID;
        this.salary = salary;
        this.managerID = managerID;
    }

    public Employee(String name, int ID, double salary)
    {
        this(name, ID, salary, 0);
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getID() {
        return this.ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public double getSalary() {
        return this.salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public int getManagerID() {
        return this.managerID;
    }

    public void setManagerID(int managerID) {
        this.managerID = managerID;
    }

    public int gethierarchyLevel() {
        return this.hierarchyLevel;
    }

    public void sethierarchyLevel(int hierarchyLevel) {
        this.hierarchyLevel = hierarchyLevel;
    }

    List<Employee> getTeamMemberList() {
        return teamMemberList;
    }

    void setTeamMemberList(List<Employee> teamMemberList) {
        this.teamMemberList = teamMemberList;
    }

    public double getCumulativeSalary() {
        return this.cumulativeSalary;
    }

    public void setCumulativeSalary(double cumulativeSalary) {
        this.cumulativeSalary = cumulativeSalary;
    }

    public boolean getIsPrinted() {
        return this.isPrinted;
    }

    public void setIsPrinted(boolean isPrinted) {
        this.isPrinted = isPrinted;
    }

	@Override
	public int totalEmployeesDelegated() {
		int i = 0;
		if(teamMemberList == null) {
			return 1;
		}
		for(Employee e: teamMemberList) {
			
			i += e.totalEmployeesDelegated();
		}
		
		return i;
	}

}
