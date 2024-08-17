package com.sparta.pt.chinookwebapp.configs;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilderFactory;

@Configuration
public class HateoasConfig {

    @Bean
    @Qualifier("customPagedResourcesAssembler")
    public <T> PagedResourcesAssembler<T> customPagedResourcesAssembler() {
        return new PagedResourcesAssembler<>(null, null);
    }

    @Bean
    @Primary
    @Qualifier("customLinkBuilderFactory")
    public WebMvcLinkBuilderFactory customLinkBuilderFactory() {
        return new WebMvcLinkBuilderFactory();
    }
}