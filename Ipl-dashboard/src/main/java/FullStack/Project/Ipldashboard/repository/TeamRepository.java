package FullStack.Project.Ipldashboard.repository;

import org.springframework.data.repository.CrudRepository;

import FullStack.Project.Ipldashboard.model.Team;

public interface TeamRepository extends CrudRepository<Team, Long> {

	Team findByTeamName(String teamName);

}
