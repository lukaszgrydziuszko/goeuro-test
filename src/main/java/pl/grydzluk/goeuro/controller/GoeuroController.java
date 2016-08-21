package pl.grydzluk.goeuro.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import pl.grydzluk.goeuro.dto.DirectResponseDto;
import pl.grydzluk.goeuro.service.ConnectionService;

/**
 * Created by luk on 2016-08-20.
 */
@Controller
public class GoeuroController {

    @Autowired
    ConnectionService connectionService;

    @RequestMapping(value = "/provider/goeurobus/direct/{departureId}/{arrivalId}",
            method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public @ResponseBody DirectResponseDto checkDirectConnection(@PathVariable(value="departureId") Integer departureId,
                                                                 @PathVariable(value="arrivalId") Integer arrivalId) {

        return connectionService.checkDirectConnection(departureId, arrivalId);
    }
}
