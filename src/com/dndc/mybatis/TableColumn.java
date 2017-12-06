package com.dndc.mybatis;

public class TableColumn {

    private String name;
    private String lowerName;
    private String upperName;
    private String comment;
    private String type;
    private Boolean primaryKey;
    public String getComment() {
        return comment;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getLowerName() {
        return lowerName;
    }
    public void setLowerName(String lowerName) {
        this.lowerName = lowerName;
    }
    public String getUpperName() {
        return upperName;
    }
    public void setUpperName(String upperName) {
        this.upperName = upperName;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public Boolean getPrimaryKey() {
        return primaryKey;
    }
    public void setPrimaryKey(Boolean primaryKey) {
        this.primaryKey = primaryKey;
    }
}
