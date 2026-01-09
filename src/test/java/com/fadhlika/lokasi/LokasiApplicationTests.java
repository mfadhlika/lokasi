package com.fadhlika.lokasi;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(classes = LokasiApplication.class)
@TestPropertySource(locations = "classpath:test.properties")
class LokasiApplicationTests {

}
