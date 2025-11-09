package com.example.WebPerformance;

import com.example.WebPerformance.repository.ItemRepository;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

@Configuration
public class RestDataConfig implements RepositoryRestConfigurer {

    private final ItemRepository itemRepository;

    public RestDataConfig(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @Override
    public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config, CorsRegistry cors) {
        config.exposeIdsFor(com.example.WebPerformance.entities.Category.class);
        config.exposeIdsFor(com.example.WebPerformance.entities.Item.class);

        // Optional: disable DELETE if needed
        // config.getExposureConfiguration().forDomainType(Item.class).disableDelete();
    }
}