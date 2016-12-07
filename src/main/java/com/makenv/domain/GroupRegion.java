package com.makenv.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class GroupRegion {
    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    private String groupId;
    @JsonIgnore
    private String value;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    private String description;


    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value == null ? null : value.trim();
    }

}