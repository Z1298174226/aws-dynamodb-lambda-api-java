package com.amazonaws.pojo;

import lombok.Data;

import java.io.Serializable;

@Data
public class Team implements Serializable {
    private static final long serialVersionUID = -8243145429438016232L;

    String teamName;
    public Team(String teamName) {
        this.teamName = teamName;
    }
}
