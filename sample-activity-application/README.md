
Start process instance by REST API
```bash
curl -i -u user:user_pass -H "Content-Type: application/json" -d '{"processDefinitionKey":"hireProcess", "variables": [ {"name":"applicantName", "value":"John Doe"}, {"name":"email", "value":"john.doe@alfresco.com"}, {"name":"phoneNumber", "value":"1234567"} ]}' http://localhost:8080/runtime/process-instances
```

Access /activiti (`curl -i -u user:user_pass http://localhost:8080/actuator/activiti/`) to get some basic stats about processes


Use HireProcessRestController
```bash
curl -i -u user:user_pass -H "Content-Type: application/json" -d '{"name":"John Doe", "email": "john.doe@alfresco.com", "phoneNumber":"123456789"}' http://localhost:8080/start-hire-process

curl -i -u user:user_pass -H "Content-Type: application/json" http://localhost:8080/runtime/tasks

curl -i -u user:user_pass -H "Content-Type: application/json" -d '{"action" : "complete", "variables": [ {"name":"telephoneInterviewOutcome", "value":true} ]}' http://localhost:8080/runtime/tasks/7510
```

see: https://spring.io/blog/2015/03/08/getting-started-with-activiti-and-spring-boot
see: https://www.baeldung.com/spring-activiti

Say hello process
```bash
curl -i -u user:user_pass -H "Content-Type: application/json" http://localhost:8080/start-process
```
