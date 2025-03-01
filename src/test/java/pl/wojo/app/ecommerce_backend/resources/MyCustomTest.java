package pl.wojo.app.ecommerce_backend.resources;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
// @ActiveProfiles("test")
public class MyCustomTest {
    
    @Value("${spring.datasource.url}")
    private String datasourceUrl;
    
    @Value("${my.custom.property}")
    private String customProperty;

    @Test
    public void testProperty() {
        System.out.println("Datasource URL: " + datasourceUrl);
        System.out.println("Custom property: " + customProperty);

        assertThat(customProperty).isEqualTo("CONFIG_LOADED");
    }
}
