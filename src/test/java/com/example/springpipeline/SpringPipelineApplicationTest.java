package com.example.springpipeline;

import org.junit.jupiter.api.Test;

class SpringPipelineApplicationTest {

    @Test
    void mainStartsApplication() {
        SpringPipelineApplication.main(new String[] {"--server.port=0", "--spring.main.web-application-type=none"});
    }
}
