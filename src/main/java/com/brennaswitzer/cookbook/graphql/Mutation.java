package com.brennaswitzer.cookbook.graphql;

import com.brennaswitzer.cookbook.domain.Post;
import graphql.kickstart.tools.GraphQLMutationResolver;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Component
public class Mutation implements GraphQLMutationResolver {

    public Post createPost(String name) {
        Post post = new Post(new SecureRandom().nextLong());
        post.setText(name);
        post.setDate(OffsetDateTime.now());
        return post;
    }
}
