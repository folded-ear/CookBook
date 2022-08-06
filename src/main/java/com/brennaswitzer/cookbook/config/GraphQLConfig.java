package com.brennaswitzer.cookbook.config;

import graphql.scalars.ExtendedScalars;
import graphql.schema.GraphQLScalarType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GraphQLConfig {

    @Bean
    public GraphQLScalarType dateTime() {
        return ExtendedScalars.DateTime;
    }
}
