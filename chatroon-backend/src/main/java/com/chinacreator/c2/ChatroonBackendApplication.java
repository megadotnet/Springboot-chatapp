package com.chinacreator.c2;

import com.chinacreator.c2.runtimex.annotation.EnableC2Runtime;
import com.chinacreator.c2.sso.client.annotation.EnableC2SSOClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableC2Runtime
@EnableC2SSOClient
@SpringBootApplication(scanBasePackages ="com.chinacreator.c2")
public class ChatroonBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(ChatroonBackendApplication.class, args);
	}

}
