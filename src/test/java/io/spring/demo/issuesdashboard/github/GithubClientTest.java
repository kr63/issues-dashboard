package io.spring.demo.issuesdashboard.github;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.metrics.CompositeMeterRegistryAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.metrics.MetricsAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RunWith(SpringRunner.class)
@RestClientTest(GithubClient.class)
@Import({MetricsAutoConfiguration.class, CompositeMeterRegistryAutoConfiguration.class})
public class GithubClientTest {

    @Autowired
    private MockRestServiceServer mockRestServiceServer;

    @Autowired
    private GithubClient githubClient;

    @Test
    public void fetchEvents() {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add("X-RateLimit-Remaining", "42");
        String expectedUri = "https://api.github.com/repos/spring-projects/spring-boot/issues/events";
        this.mockRestServiceServer
                .expect(requestTo(expectedUri))
                .andRespond(withSuccess(new ClassPathResource(
                        "events.json", getClass()), MediaType.APPLICATION_JSON)
                        .headers(responseHeaders));

        ResponseEntity<RepositoryEvent[]> responseEntity =
                this.githubClient.fetchEvents("spring-projects", "spring-boot");
        List<RepositoryEvent> repositoryEvents = Arrays.asList(Objects.requireNonNull(responseEntity.getBody()));

        assertThat(repositoryEvents).hasSize(1);
        assertThat(repositoryEvents.get(0).getIssue().getTitle()).startsWith("Mine bitcoins");
        assertThat(repositoryEvents.get(0).getActor().getLogin()).isEqualTo("bclozel");
    }
}