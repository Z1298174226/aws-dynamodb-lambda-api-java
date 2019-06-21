package com.amazonaws.pojo;

import lombok.Data;

@Data
public class Team {
    String teamName;
    public Team(String teamName) {
        this.teamName = teamName;
    }
}
