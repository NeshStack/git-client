package com.example.proto.controller;

import com.example.proto.model.GitLog;
import com.example.proto.model.RefLog;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.ReflogEntry;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.StreamSupport;

@RequestMapping("/git")
@RestController
public class GitController {

    private static final String REMOTE_URL = "https://github.com/shashirajraja/onlinebookstore";

    File localPath;

    @GetMapping("/clone")
    public ResponseEntity<String> cloneRepository(@RequestParam String path) throws GitAPIException, IOException {

        localPath = new File(path);

        FileUtils.deleteDirectory(localPath);

        try (Git result = Git.cloneRepository()
                .setURI(REMOTE_URL)
                .setDirectory(localPath)
                .call()) {

            System.out.println("Having Repository :" + result.getRepository().getDirectory());
        }

        return new ResponseEntity<>("Git Repository cloned", HttpStatus.CREATED);
    }

    @GetMapping("/logs")
    public ResponseEntity<List<GitLog>> cloneRepositoryLogs(@RequestParam String path) throws GitAPIException, IOException {

        List<GitLog> listLog = new ArrayList<>();
        localPath = new File(path);

        try (Git git = Git.open(localPath)) {

            Iterable<RevCommit> iterable = git.log().call();

            System.out.println("Having Repository :" + git.getRepository());

            StreamSupport.stream(iterable.spliterator(), false)
                    .forEach(revCommit -> {
                        listLog.add(new GitLog(
                                revCommit.toObjectId().getName(),
                                revCommit.getCommitterIdent().getName(),
                                revCommit.getCommitterIdent().getEmailAddress(),
                                revCommit.toString(),
                                revCommit.getFullMessage()
                        ));
                    });

            return ResponseEntity.ok(listLog);
        }
    }

    @GetMapping("/history")
    public ResponseEntity<List<RefLog>> cloneRepositoryHistory(@RequestParam String path) throws GitAPIException, IOException {

        List<RefLog> listLog = new ArrayList<>();
        localPath = new File(path);

        try (Git git = Git.open(localPath)) {

            Collection<ReflogEntry> logs = git.reflog().call();

            logs.forEach(log ->{
                listLog.add(new RefLog(
                        log.getWho().getName(),
                        log.toString(),
                        log.getComment(),
                        log.getNewId().toString(),
                        log.getOldId().toString()
                ));
            });
        }

        return ResponseEntity.ok(listLog);
    }

    @GetMapping("/delete")
    public ResponseEntity<?> cloneRepositoryDelete(@RequestParam String path) throws IOException {

        FileUtils.deleteDirectory(new File(path));
        return new ResponseEntity<>("Local Git Repository Deleted", HttpStatus.OK);
    }
}
