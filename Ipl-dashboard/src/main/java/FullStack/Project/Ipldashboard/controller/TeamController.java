package FullStack.Project.Ipldashboard.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import FullStack.Project.Ipldashboard.model.Team;
import FullStack.Project.Ipldashboard.repository.MatchRepository;
import FullStack.Project.Ipldashboard.repository.TeamRepository;

@RestController
public class TeamController {

	private TeamRepository teamRepostory;
	private MatchRepository matchRepository;

	public TeamController(TeamRepository teamRepostory, MatchRepository matchRepository) {
		this.teamRepostory = teamRepostory;
		this.matchRepository = matchRepository;
	}

	@GetMapping("/team/{teamName}")
	public Team getTeam(@PathVariable String teamName) {
		Team team = this.teamRepostory.findByTeamName(teamName);
		team.setMatches(matchRepository.findLatestMatchesByTeam(teamName, 4));
		return team;
	}
}
