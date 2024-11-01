package com.spotlightspace.core.event.repository;

import com.spotlightspace.core.event.domain.EventElastic;
import com.spotlightspace.core.event.dto.request.SearchEventRequestDto;
import com.spotlightspace.core.event.dto.response.GetEventElasticResponseDto;
import lombok.RequiredArgsConstructor;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class EventElasticQueryRepositoryImpl implements EventElasticQueryRepository {
    private final ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Override
    public Page<GetEventElasticResponseDto> searchElasticEvents(SearchEventRequestDto requestDto, String type, Pageable pageable) {

        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery()
                .must(QueryBuilders.termQuery("isDeleted", false));

        if (requestDto.getTitle() != null) {
            boolQuery.must(QueryBuilders.matchQuery("title", requestDto.getTitle()));
        }
        if (requestDto.getLocation() != null) {
            boolQuery.must(QueryBuilders.matchQuery("location", requestDto.getLocation()));
        }
        if (requestDto.getMaxPeople() != null) {
            boolQuery.filter(QueryBuilders.rangeQuery("maxPeople").lte(requestDto.getMaxPeople()));
        }
        if (requestDto.getRecruitmentStartAt() != null) {
            boolQuery.filter(QueryBuilders.rangeQuery("recruitmentStartAt").gte(requestDto.getRecruitmentStartAt()));
        }
        if (requestDto.getRecruitmentFinishAt() != null) {
            boolQuery.filter(QueryBuilders.rangeQuery("recruitmentFinishAt").lt(requestDto.getRecruitmentFinishAt()));
        }

        // 정렬 옵션 추가
        SortBuilder<?> sortBuilder;
        switch (type) {
            case "upprice":
                sortBuilder = SortBuilders.fieldSort("price").order(SortOrder.ASC);
                break;
            case "downprice":
                sortBuilder = SortBuilders.fieldSort("price").order(SortOrder.DESC);
                break;
            case "date":
                sortBuilder = SortBuilders.fieldSort("recruitmentFinishAt").order(SortOrder.ASC);
                break;
            default:
                sortBuilder = SortBuilders.fieldSort("id").order(SortOrder.DESC);
                break;
        }

        // Elasticsearch 검색 요청
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(boolQuery)
                .withSort(sortBuilder)
                .withPageable(pageable)
                .build();

        var searchHits = elasticsearchRestTemplate.search(searchQuery, EventElastic.class);


        // 검색 결과 매핑
        List<GetEventElasticResponseDto> results = searchHits.stream()
                .map(hit -> GetEventElasticResponseDto.from(hit.getContent()))
                .collect(Collectors.toList());

        return new PageImpl<>(results, pageable, searchHits.getTotalHits());
    }
}
