package com.fission.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@SpringBootApplication
@Controller
@EnableDiscoveryClient
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@RequestMapping("/")
	public String getHomePage() {
		return "index.html";
	}

	@RequestMapping("/fileChannel")
	public String getPage() {
		return "/FileChannel.html";
	}

	@RequestMapping("/uploadBlob")
	public String getBlobPage() {
		return "/blob.html";
	}

	@RequestMapping("/uploadCBlob")
	public String getCBlobPage() {
		return "/blobchunks.html";
	}
}
