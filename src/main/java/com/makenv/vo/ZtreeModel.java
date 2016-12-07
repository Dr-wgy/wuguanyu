package com.makenv.vo;

import java.util.Objects;

/**
 * Created by wgy on 2016/12/5.
 */
public class ZtreeModel {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ZtreeModel that = (ZtreeModel) o;
        return Objects.equals(doCheck, that.doCheck) &&
                Objects.equals(id, that.id) &&
                Objects.equals(pId, that.pId) &&
                Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, pId, name, doCheck);
    }

    private String id;

    private String pId;

    private String name;

    private boolean doCheck = true;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getpId() {
        return pId;
    }

    public void setpId(String pId) {
        this.pId = pId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isDoCheck() {
        return doCheck;
    }

    public void setDoCheck(boolean doCheck) {
        this.doCheck = doCheck;
    }

    public ZtreeModel(String id, String pId, String name) {
        this.id = id;
        this.pId = pId;
        this.name = name;
    }

    public ZtreeModel(String id, String pId, String name, boolean doCheck) {
        this.id = id;
        this.pId = pId;
        this.name = name;
        this.doCheck = doCheck;
    }

}
