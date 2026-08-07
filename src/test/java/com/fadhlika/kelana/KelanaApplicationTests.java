package com.fadhlika.kelana;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(classes = KelanaApplication.class)
@TestPropertySource(locations = "classpath:test.properties")
class KelanaApplicationTests {

}
