package com.sparta.pt.chinookwebapp.configs;

import com.sparta.pt.chinookwebapp.controllers.api.PaginationUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilderFactory;

@Configuration
public class AppConfig {

    @Bean
    public <T> PaginationUtils<T> paginationUtils(
            @Qualifier("customPagedResourcesAssembler") PagedResourcesAssembler<T> pagedResourcesAssembler,
            @Qualifier("customLinkBuilderFactory") WebMvcLinkBuilderFactory linkBuilderFactory) {
        return new PaginationUtils<>(pagedResourcesAssembler, linkBuilderFactory);
    }
}