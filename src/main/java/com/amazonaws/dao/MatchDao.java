package com.amazonaws.dao;

import com.amazonaws.pojo.Match;

import java.util.List;
import java.util.Optional;

public interface MatchDao {
    List<Match> findAllMatches();

    List<Match> findMatchByTeam(String team);

    Optional<Match> findMatchByTeamAndDate(String team, Long matchDate);

    void saveOrUpdateMatch(Match event);

    void deleteMatch(String team, Long matchDate);
}
