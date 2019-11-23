package com.example.studentside;

public class Unit {
    private String unitCode;
    private String unitName;

    public Unit(String unitCode, String unitName) {
        this.unitCode = unitCode;
        this.unitName = unitName;
    }

    public Unit(){

    }

    public String getUnitCode() {
        return unitCode;
    }

    public void setUnitCode(String unitCode) {
        this.unitCode = unitCode;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }
}
