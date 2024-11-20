package com.spotlightspace;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.elasticsearch.config.EnableElasticsearchAuditing;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

@SpringBootApplication
public class SpotlightSpaceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpotlightSpaceApplication.class, args);
    }
}
