package com.dndc.mybatis;

import java.util.List;

import org.eclipse.jdt.core.IPackageFragment;

public class CustomInfo {

    private String datasource;
    private String saveDir;
    private String packageName;
    private List<List<String>> tables;
    private IPackageFragment packageFragment;
    public String getDatasource() {
        return datasource;
    }
    public void setDatasource(String datasource) {
        this.datasource = datasource;
    }
    public String getSaveDir() {
        return saveDir;
    }
    public void setSaveDir(String saveDir) {
        this.saveDir = saveDir;
    }
    public String getPackageName() {
        return packageName;
    }
    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }
    public List<List<String>> getTables() {
        return tables;
    }
    public void setTables(List<List<String>> tables) {
        this.tables = tables;
    }
    public IPackageFragment getPackageFragment() {
        return packageFragment;
    }
    public void setPackageFragment(IPackageFragment packageFragment) {
        this.packageFragment = packageFragment;
    }
}
