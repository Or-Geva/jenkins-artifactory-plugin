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
    private final String spec;
    private final String name;
    private final String version;
    private final boolean dryRun;
    private final String description;
    private final StepContext context;
    private final transient Run build;
    private final boolean insecureTls;
    private final String gpgPassphrase;
    private final transient FilePath ws;
    private final String releaseNotePath;
    private final boolean signImmediately;
    private final String StoringRepository;
    private final DistributionServer server;
    private final JenkinsBuildInfoLog logger;
    private final transient TaskListener listener;
    private final ReleaseBundleCreate.ReleaseNotesSyntax releaseNoteSyntax;

    public ReleaseBundleCreateExecutor(DistributionServer server, String name, String version, String spec, String StoringRepository, boolean signImmediately, boolean dryRun, String gpgPassphrase, String releaseNotePath,
                                       ReleaseBundleCreate.ReleaseNotesSyntax releaseNoteSyntax, String description, boolean insecureTls, TaskListener listener, Run build, FilePath ws, StepContext context) {
        this.ws = ws;
        this.name = name;
        this.spec = spec;
        this.build = build;
        this.server = server;
        this.dryRun = dryRun;
        this.context = context;
        this.version = version;
        this.listener = listener;
        this.description = description;
        this.insecureTls = insecureTls;
        this.gpgPassphrase = gpgPassphrase;
        this.signImmediately = signImmediately;
        this.releaseNotePath = releaseNotePath;
        this.StoringRepository = StoringRepository;
        this.releaseNoteSyntax = releaseNoteSyntax;
        this.logger = new JenkinsBuildInfoLog(listener);
    }

    public void execute() throws IOException, InterruptedException {
        ReleaseBundleCreateCallable runCallable = new ReleaseBundleCreateCallable(name, version, spec, StoringRepository, signImmediately, dryRun, gpgPassphrase, releaseNotePath, releaseNoteSyntax, description, insecureTls, logger);
        addDistributionManagerBuilderToCallable(runCallable);
        ws.act(runCallable);
    }

    private void addDistributionManagerBuilderToCallable(ReleaseBundleCreateCallable callable) {
        callable.setDistributionManager(server.getDistributionManagerBuilder(logger, build.getParent()));
    }
}
