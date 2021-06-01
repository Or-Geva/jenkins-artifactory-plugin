package org.jfrog.hudson.pipeline.common.executors;

import hudson.FilePath;
import hudson.model.Run;
import hudson.model.TaskListener;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jfrog.build.extractor.distribution.ReleaseBundleCreate;
import org.jfrog.hudson.distribution.ReleaseBundleCreateCallable;
import org.jfrog.hudson.pipeline.common.types.DistributionServer;
import org.jfrog.hudson.util.JenkinsBuildInfoLog;

import java.io.IOException;

public class ReleaseBundleCreateExecutor implements Executor {
    private final String repo;
    private final String spec;
    private final String name;
    private final boolean sign;
    private final String version;
    private final boolean dryRun;
    private final String passphrase;
    private final StepContext context;
    private final transient Run build;
    private final boolean insecureTls;
    private final transient FilePath ws;
    private final String releaseNotePath;
    private final DistributionServer server;
    private final JenkinsBuildInfoLog logger;
    private final transient TaskListener listener;
    private final ReleaseBundleCreate.ReleaseNotesSyntax releaseNoteSyntax;

    public ReleaseBundleCreateExecutor(DistributionServer server, String name, String version, String spec, String repo, boolean sign, boolean dryRun, String passphrase, String releaseNotePath,
                                       ReleaseBundleCreate.ReleaseNotesSyntax releaseNoteSyntax, boolean insecureTls, TaskListener listener, Run build, FilePath ws, StepContext context) {
        this.ws = ws;
        this.name = name;
        this.spec = spec;
        this.sign = sign;
        this.repo = repo;
        this.build = build;
        this.server = server;
        this.dryRun = dryRun;
        this.context = context;
        this.version = version;
        this.listener = listener;
        this.passphrase = passphrase;
        this.insecureTls = insecureTls;
        this.releaseNotePath = releaseNotePath;
        this.releaseNoteSyntax = releaseNoteSyntax;
        this.logger = new JenkinsBuildInfoLog(listener);
    }


    public void execute() throws IOException, InterruptedException {
        ReleaseBundleCreateCallable runCallable = new ReleaseBundleCreateCallable(name, version, spec, repo, sign, dryRun, passphrase, releaseNotePath, releaseNoteSyntax, insecureTls, logger);
        addDistributionManagerBuilderToCallable(runCallable);
        ws.act(runCallable);
    }


    private void addDistributionManagerBuilderToCallable(ReleaseBundleCreateCallable callable) {
        callable.setDistributionManager(server.getDistributionManagerBuilder(logger, build.getParent()));
    }
}
