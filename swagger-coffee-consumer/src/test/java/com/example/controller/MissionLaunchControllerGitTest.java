package com.example.controller;

import com.example.config.CoffeeProducerSettings;
import com.example.model.LaunchData;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerPort;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Sven Bayer
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@AutoConfigureStubRunner(stubsMode = StubRunnerProperties.StubsMode.REMOTE,
        repositoryRoot = "git://https://github.com/SvenBayer/spring-cloud-contract-swagger-contracts-git.git",
        ids = { "blog.svenbayer:swagger-coffee-producer-git:1.0-SNAPSHOT"})
@DirtiesContext
public class MissionLaunchControllerGitTest extends AbstractContractTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MissionLaunchController missionLaunchController;

    @Autowired
    private CoffeeProducerSettings coffeeProducerSettings;

    @StubRunnerPort("swagger-coffee-producer-git")
    int producerPort;

    @Before
    public void setupPort() {
        coffeeProducerSettings.setPort(producerPort);
    }

    @Test
    public void should_launch_rocket() throws Exception {
        LaunchData launchData = new LaunchData();
        launchData.setDeparture("departure");
        launchData.setDestination("destination");
        launchData.setRocketName("rocketName");

        mockMvc.perform(MockMvcRequestBuilders.post("/mission-launch-service/v1.0/launch")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-REQUEST-ID", "123456")
                .content(coffeeRocketJson.write(launchData).getJson()))
                .andExpect(status().isCreated())
                .andExpect(content().string("name"));
    }
}