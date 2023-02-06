package com.example.proto;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GitClientApplication {

	public static void main(String[] args) throws GitAPIException {
		SpringApplication.run(GitClientApplication.class, args);
	}
}
