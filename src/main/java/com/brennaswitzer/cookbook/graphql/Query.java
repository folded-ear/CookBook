package com.brennaswitzer.cookbook.graphql;

import com.brennaswitzer.cookbook.domain.Post;
import graphql.kickstart.tools.GraphQLQueryResolver;
import org.springframework.stereotype.Component;

@Component
public class Query implements GraphQLQueryResolver {

    Post getPost(Long id) {
        return new Post(id);
    }

}
