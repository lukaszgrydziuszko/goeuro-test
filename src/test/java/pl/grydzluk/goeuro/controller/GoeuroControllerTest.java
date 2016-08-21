package pl.grydzluk.goeuro.controller;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import pl.grydzluk.goeuro.Application;
import pl.grydzluk.goeuro.component.ConnectionStorage;
import pl.grydzluk.goeuro.dto.DirectResponseDto;

import java.net.URISyntaxException;
import java.nio.file.Paths;

/**
 * Created by luk on 2016-08-21.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(properties = "run.arguments=\"C:/goeuro_test/test.txt\"", classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GoeuroControllerTest {
    @Autowired
    TestRestTemplate template;

    @Autowired
    ConnectionStorage connectionStorage;

    private boolean initialized = false;

    @Before
    public void init() throws Exception {
        if (!initialized) {
            connectionStorage.run(getFilePathFromResources("CorrectInputFile.txt"));
        }
        initialized = true;
    }

    private String getFilePathFromResources(String fileName) throws URISyntaxException {
        return Paths.get(ClassLoader.getSystemResource(fileName).toURI()).toString();
    }

    @Test
    public void directConnectionExistTest() {
        //given

        //when
        ResponseEntity<DirectResponseDto> response = template.getForEntity("/provider/goeurobus/direct/1/2", DirectResponseDto.class);


        //then
        Assert.assertNotNull(response);
        Assert.assertEquals(response.getStatusCode(), HttpStatus.OK);
        Assert.assertTrue(response.getHeaders().get(HttpHeaders.CONTENT_TYPE)
                .contains(MediaType.APPLICATION_JSON_UTF8_VALUE));
        DirectResponseDto responseBody = response.getBody();
        Assert.assertEquals(responseBody.getDepartureId(), new Integer(1));
        Assert.assertEquals(responseBody.getArrivalId(), new Integer(2));
        Assert.assertTrue(responseBody.isExist());
    }

    @Test
    public void directConnectionNotExistTest() {
        //given

        //when
        ResponseEntity<DirectResponseDto> response = template.getForEntity("/provider/goeurobus/direct/1/5", DirectResponseDto.class);


        //then
        Assert.assertNotNull(response);
        Assert.assertEquals(response.getStatusCode(), HttpStatus.OK);
        Assert.assertTrue(response.getHeaders().get(HttpHeaders.CONTENT_TYPE)
                .contains(MediaType.APPLICATION_JSON_UTF8_VALUE));
        DirectResponseDto responseBody = response.getBody();
        Assert.assertEquals(responseBody.getDepartureId(), new Integer(1));
        Assert.assertEquals(responseBody.getArrivalId(), new Integer(5));
        Assert.assertFalse(responseBody.isExist());
    }
}
