package top.infra.sample.admin;

import static org.junit.Assert.assertEquals;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import org.activiti.engine.HistoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.ProcessInstanceHistoryLog;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Event;
import org.activiti.engine.task.Task;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.subethamail.wiser.Wiser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {SampleActivityApplication.class}, webEnvironment = RANDOM_PORT)
public class HireProcessTest {

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private ApplicantRepository applicantRepository;

    private Wiser wiser;

    @Before
    public void setup() {
        this.wiser = new Wiser();
        this.wiser.setPort(1025);
        this.wiser.start();
    }

    @After
    public void cleanup() {
        for (final HistoricProcessInstance processInstance :
            this.historyService.createHistoricProcessInstanceQuery().finished().notDeleted().list()) {
            this.historyService.deleteHistoricProcessInstance(processInstance.getId());
        }

        this.wiser.stop();
    }

    @Test
    public void testHappyPath() {
        // Create test applicant
        Applicant applicant = new Applicant("John Doe", "john@activiti.org", "12344");
        this.applicantRepository.save(applicant);

        // Start process instance
        Map<String, Object> variables = new HashMap<>();
        variables.put("applicant", applicant);
        ProcessInstance processInstance = this.runtimeService.startProcessInstanceByKey("hireProcessWithJpa", variables);

        // First, the 'phone interview' should be active
        Task task = this.taskService.createTaskQuery()
            .processInstanceId(processInstance.getId())
            .taskCandidateGroup("dev-managers")
            .singleResult();
        assertEquals("Telephone interview", task.getName());

        // Completing the phone interview with success should trigger two new tasks
        Map<String, Object> taskVariables = new HashMap<>();
        taskVariables.put("telephoneInterviewOutcome", true);
        this.taskService.complete(task.getId(), taskVariables);

        List<Task> tasks = this.taskService.createTaskQuery()
            .processInstanceId(processInstance.getId())
            .orderByTaskName().asc()
            .list();
        assertEquals(2, tasks.size());
        assertEquals("Financial negotiation", tasks.get(0).getName());
        assertEquals("Tech interview", tasks.get(1).getName());

        // Completing both should wrap up the subprocess, send out the 'welcome mail' and end the process instance
        taskVariables = new HashMap<>();
        taskVariables.put("techOk", true);
        this.taskService.complete(tasks.get(0).getId(), taskVariables);

        taskVariables = new HashMap<>();
        taskVariables.put("financialOk", true);
        this.taskService.complete(tasks.get(1).getId(), taskVariables);

        // Verify email
        assertEquals(1, this.wiser.getMessages().size());

        // Verify process completed
        List<Task> tasksTodo = this.taskService.createTaskQuery()
            .processInstanceId(processInstance.getId())
            .orderByTaskName().asc()
            .list();
        assertEquals(0, tasksTodo.size());

        // TODO tables of history data not found
        // List<Event> events = this.runtimeService.getProcessInstanceEvents(processInstance.getId());
        // final boolean processInstanceEnded = processInstance.isEnded();
        // assertEquals(0, this.historyService.createHistoricProcessInstanceQuery().unfinished().count());
        // assertEquals(1, this.historyService.createHistoricProcessInstanceQuery().finished().count());
    }
}
