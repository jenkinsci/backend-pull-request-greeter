package org.jenkinsci.backend.pr;

import org.kohsuke.github.GHIssueComment;
import org.kohsuke.github.GHIssueState;
import org.kohsuke.github.GHOrganization;
import org.kohsuke.github.GHPullRequest;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;

import java.io.IOException;

/**
 * Puts greeting comments to pull requests.
 *
 */
public class App {
    public static void main(String[] args) throws Exception {
        new App().run();
    }

    public void run() throws Exception {
        GitHub gh = GitHub.connect();
        GHOrganization org = gh.getOrganization("jenkinsci");
        for (GHRepository r : org.listRepositories()) {
            // as a roll out, only do this for 60% of the repositories
            if (r.getName().hashCode()%10<6 && !r.getName().equals("jenkins"))
                greet(r);
        }
    }

    protected void greet(GHRepository r) throws IOException {
        for (GHPullRequest pr : r.listPullRequests(GHIssueState.OPEN)) {
            greet(pr);
        }
    }

    /**
     * Posts a note to set the expectation.
     */
    protected void greet(GHPullRequest pr) throws IOException {
        if (hasGreetingComment(pr))
            return; // no need to do anything

        System.out.println("Greeting "+pr.getRepository().getName() + "\t" + pr.getTitle());

        pr.comment(String.format("Thank you for a pull request! Please check [this document](%s) for how the Jenkins project handles pull requests",LINK));
    }

    /**
     * Checks if this pull request already has a greeting
     */
    protected boolean hasGreetingComment(GHPullRequest pr) throws IOException {
        for (GHIssueComment c : pr.listComments()) {
            if (c.getBody().contains(LINK))
                return true;
        }
        return false;
    }

    public static final String LINK = "http://jenkins-ci.org/pull-request-greeting";
}
