package io.spring.demo.issuesdashboard;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("github")
public class GithubProperties {

    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
