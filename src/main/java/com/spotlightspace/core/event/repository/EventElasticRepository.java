package com.spotlightspace.core.event.repository;

import com.spotlightspace.core.event.domain.EventElastic;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface EventElasticRepository extends ElasticsearchRepository<EventElastic, Long>, EventElasticQueryRepository {
}
