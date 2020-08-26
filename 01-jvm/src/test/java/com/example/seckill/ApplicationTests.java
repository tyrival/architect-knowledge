package com.example.seckill;

import com.tyrival.jvm.lesson07.Application;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

@RunWith(SpringRunner.class)
@SpringBootTest(classes={Application.class})
public class ApplicationTests {

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

	@Autowired
	private RestTemplate restTemplate;

	@Test
	public void test() throws Exception {
		for (int i = 0; i < 10000; i++) {
			String result = restTemplate.getForObject("http://localhost:8080/user/process", String.class);
			Thread.sleep(1000);
		}
	}

}
