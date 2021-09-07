package FullStack.Project.Ipldashboard.data;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import FullStack.Project.Ipldashboard.model.Team;

@Component
public class JobCompletionNotificationListener extends JobExecutionListenerSupport {

	private static final Logger log = LoggerFactory.getLogger(JobCompletionNotificationListener.class);

	// private final JdbcTemplate jdbcTemplate;
	private final EntityManager em;

	@Autowired
	public JobCompletionNotificationListener(EntityManager em) {
		this.em = em;
	}

	@Override
	@Transactional
	public void afterJob(JobExecution jobExecution) {
		if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
			log.info("!!! JOB FINISHED! Time to verify the results");

//			jdbcTemplate.query("SELECT team1, team2 ,date FROM match", (rs, row) -> "Team 1 " + rs.getString(1)
//					+ " Team 2 " + rs.getString(2) + " Date " + rs.getString(3))
//					.forEach(str -> System.out.println(str));
			Map<String, Team> teamData = new HashMap<>();

			em.createQuery("SELECT m.team1, count(*) FROM Match m group by m.team1", Object[].class).getResultList()
					.stream().map(e -> new Team((String) e[0], (long) e[1]))
					.forEach(team -> teamData.put(team.getTeamName(), team));

			em.createQuery("SELECT m.team2, count(*) FROM Match m group by m.team2", Object[].class).getResultList()
					.stream().forEach(e -> {
						Team team = teamData.get((String) e[0]);
						if (team != null)
							team.setTotalMatches(team.getTotalMatches() + (long) e[1]);
					});

			em.createQuery("SELECT m.matchWinner, count(*) FROM Match m group by m.matchWinner", Object[].class)
					.getResultList().stream().forEach(e -> {
						Team team = teamData.get((String) e[0]);
						if (team != null)
							team.setTotalWins((long) e[1]);
					});

			teamData.values().forEach(team -> em.persist(team));
			teamData.values().forEach(team -> System.out.println(team));
		}
	}
}
