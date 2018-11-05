package top.infra.sample.admin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class TaskRepresentation {

    private String id;
    private String name;
    private String processInstanceId;
}
