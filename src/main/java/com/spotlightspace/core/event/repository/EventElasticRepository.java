package com.spotlightspace.core.event.repository;

import com.spotlightspace.core.event.domain.EventElastic;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventElasticRepository extends ElasticsearchRepository<EventElastic, Long> {
}
