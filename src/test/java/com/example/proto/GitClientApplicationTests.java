package com.example.proto;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static java.util.stream.StreamSupport.stream;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class GitClientApplicationTests {

    private static final String MASTER = "refs/heads/master";

    private static Git remote;
    private static Git local;

    @TempDir
    static File tempDir;

    @BeforeAll
    public static void setUp() throws GitAPIException, IOException {
        remote = initRepository();
        local = cloneRepository();
    }
    public void tearDown() {
        remote.close();
        local.close();
    }

    @Test
    void contextLoads() {
    }

    @Test
    public void testRevWalk() throws IOException, GitAPIException {
        RevCommit initialCommit = local.commit().setMessage( "init commit" ).call();
        Ref branch = local.branchCreate().setName( "side" ).call();
        local.checkout().setName( branch.getName() ).call();
        RevCommit branchCommit = local.commit().setMessage( "commit on side branch" ).call();
        local.checkout().setName( MASTER ).call();

        List<RevCommit> commits;
        try( RevWalk revWalk = new RevWalk( local.getRepository() ) ) {
            ObjectId commitId = local.getRepository().resolve( branch.getName() );
            revWalk.markStart( revWalk.parseCommit( commitId ) );
            commits = stream( revWalk.spliterator(), false ).collect( toList() );
        }

        assertEquals( branchCommit, commits.get( 0 ) );
        assertEquals( initialCommit, commits.get( 1 ) );
    }

    @Test
    public void testLog() throws GitAPIException {
        RevCommit commit = local.commit().setMessage("empty commit").call();

        Iterable<RevCommit> iterable = local.log().call();

        List<RevCommit> commits = stream(iterable.spliterator(), false).collect(toList());
        assertEquals(1, commits.size());
        assertEquals(commit, commits.get(0));
    }

    private static Git initRepository() throws GitAPIException, IOException {
        return Git.init().setDirectory(new File(tempDir, "remote")).call();
    }

    private static Git cloneRepository() throws GitAPIException, IOException {
        String remoteUri = remote.getRepository().getDirectory().getCanonicalPath();
        File localDir = new File(tempDir, "local");
        return Git.cloneRepository().setURI(remoteUri).setDirectory(localDir).call();
    }

}
