package com.sparta.pt.chinookwebapp.configs;

import com.sparta.pt.chinookwebapp.controllers.api.HateoasUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilderFactory;

@Configuration
public class AppConfig {

    @Bean
    public <T> HateoasUtils<T> paginationUtils(
            @Qualifier("customPagedResourcesAssembler") PagedResourcesAssembler<T> pagedResourcesAssembler,
            @Qualifier("customLinkBuilderFactory") WebMvcLinkBuilderFactory linkBuilderFactory) {
        return new HateoasUtils<>(pagedResourcesAssembler, linkBuilderFactory);
    }
}