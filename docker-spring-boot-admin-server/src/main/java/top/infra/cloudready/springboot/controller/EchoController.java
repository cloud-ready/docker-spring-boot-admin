package top.infra.cloudready.springboot.controller;

import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE;
import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController("/")
public class EchoController {

    @RequestMapping(path = "echo", method = POST, consumes = APPLICATION_FORM_URLENCODED_VALUE, produces = TEXT_PLAIN_VALUE)
    public String echoPostForm(@RequestParam(name = "content") final String content) {
        return content;
    }

    @RequestMapping(path = "echo", method = POST, consumes = TEXT_PLAIN_VALUE, produces = TEXT_PLAIN_VALUE)
    public String echoPostText(@RequestBody final String content) {
        return content;
    }

    @RequestMapping(path = "echo", method = GET, produces = TEXT_PLAIN_VALUE)
    public String echoGet(@RequestParam(name = "content") final String content) {
        return content;
    }
}
