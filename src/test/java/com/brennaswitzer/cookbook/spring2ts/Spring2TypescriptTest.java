package com.brennaswitzer.cookbook.spring2ts;

import com.blueveery.springrest2ts.Rest2tsGenerator;
import com.blueveery.springrest2ts.converters.JacksonObjectMapper;
import com.blueveery.springrest2ts.converters.ModelClassesAbstractConverter;
import com.blueveery.springrest2ts.converters.ModelClassesToTsInterfacesConverter;
import com.blueveery.springrest2ts.converters.SpringRestToTsConverter;
import com.blueveery.springrest2ts.filters.JavaTypeFilter;
import com.blueveery.springrest2ts.filters.JavaTypePackageFilter;
import com.blueveery.springrest2ts.filters.OrFilterOperator;
import com.blueveery.springrest2ts.implgens.FetchBasedImplementationGenerator;
import com.blueveery.springrest2ts.implgens.ImplementationGenerator;
import com.brennaswitzer.cookbook.domain.BaseEntity;
import com.brennaswitzer.cookbook.message.PlanMessage;
import com.brennaswitzer.cookbook.payload.UserInfo;
import com.brennaswitzer.cookbook.web.UserController;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.val;
import org.junit.Test;
import org.springframework.util.FileSystemUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Spring2TypescriptTest {

    protected static final Path OUTPUT_DIR_PATH = Paths.get("client/src/types");

    protected Rest2tsGenerator tsGenerator;
    protected Set<String> javaPackageSet;
    protected ModelClassesAbstractConverter modelClassesConverter;

    @Test
    public void generateTypeDef() throws IOException {
        FileSystemUtils.deleteRecursively(OUTPUT_DIR_PATH.toFile());
        tsGenerator = new Rest2tsGenerator();

        val modelFilters = new ArrayList<JavaTypeFilter>();
        modelFilters.add(new JavaTypePackageFilter(UserInfo.class.getPackage()));
        modelFilters.add(new JavaTypePackageFilter(PlanMessage.class.getPackage()));
        tsGenerator.setModelClassesCondition(new OrFilterOperator(modelFilters));

        JacksonObjectMapper jacksonObjectMapper = new JacksonObjectMapper();
        jacksonObjectMapper.setFieldsVisibility(JsonAutoDetect.Visibility.ANY);
        modelClassesConverter = new ModelClassesToTsInterfacesConverter(jacksonObjectMapper);
        tsGenerator.setModelClassesConverter(modelClassesConverter);

        tsGenerator.setRestClassesCondition(new JavaTypePackageFilter(UserController.class.getPackage()));
        ImplementationGenerator implementationGenerator = new FetchBasedImplementationGenerator(true);
        tsGenerator.setRestClassesConverter(new SpringRestToTsConverter(implementationGenerator));

        javaPackageSet = new HashSet<>();
        javaPackageSet.add(BaseEntity.class.getPackage().getName());
        javaPackageSet.add(UserInfo.class.getPackage().getName());
        javaPackageSet.add(PlanMessage.class.getPackage().getName());
        javaPackageSet.add(UserController.class.getPackage().getName());
        tsGenerator.generate(javaPackageSet, OUTPUT_DIR_PATH);
    }

}
