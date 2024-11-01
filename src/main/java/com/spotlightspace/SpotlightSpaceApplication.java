package com.spotlightspace;

import com.spotlightspace.core.event.repository.EventElasticRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories(excludeFilters = @ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE,
        classes = EventElasticRepository.class))
@SpringBootApplication
public class SpotlightSpaceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpotlightSpaceApplication.class, args);
    }
}
