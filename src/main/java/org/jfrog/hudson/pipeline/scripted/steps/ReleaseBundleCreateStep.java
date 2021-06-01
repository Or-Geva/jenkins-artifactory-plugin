package org.jfrog.hudson.pipeline.scripted.steps;

import com.google.inject.Inject;
import hudson.Extension;
import hudson.Util;
import org.jenkinsci.plugins.workflow.steps.AbstractStepDescriptorImpl;
import org.jenkinsci.plugins.workflow.steps.AbstractStepImpl;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jfrog.build.extractor.distribution.ReleaseBundleCreate;
import org.jfrog.hudson.pipeline.ArtifactorySynchronousNonBlockingStepExecution;
import org.jfrog.hudson.pipeline.common.executors.ReleaseBundleCreateExecutor;
import org.jfrog.hudson.pipeline.common.types.DistributionServer;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.IOException;

public class ReleaseBundleCreateStep extends AbstractStepImpl {
    static final String STEP_NAME = "releaseBundleCreate";
    private final String spec;
    private final String name;
    private final String storingRepository;
    private final boolean signImmediately;
    private final String version;
    private final boolean dryRun;
    private final String gpgPassphrase;
    private final String description;
    private final boolean insecureTls;
    private final String releaseNotePath;
    private final DistributionServer server;
    private final ReleaseBundleCreate.ReleaseNotesSyntax releaseNoteSyntax;

    @DataBoundConstructor
    public ReleaseBundleCreateStep(DistributionServer server, String name, String version, String spec, String storingRepository, boolean signImmediately, boolean dryRun,
                                   String gpgPassphrase, String releaseNotePath, ReleaseBundleCreate.ReleaseNotesSyntax releaseNoteSyntax,
                                   String description, boolean insecureTls) {
        this.spec = spec;
        this.name = name;
        this.server = server;
        this.dryRun = dryRun;
        this.version = version;
        this.description = description;
        this.insecureTls = insecureTls;
        this.gpgPassphrase = gpgPassphrase;
        this.signImmediately = signImmediately;
        this.releaseNotePath = releaseNotePath;
        this.storingRepository = storingRepository;
        this.releaseNoteSyntax = releaseNoteSyntax;
    }

    public String getSpec() {
        return spec;
    }

    public DistributionServer getServer() {
        return server;
    }

    public static class Execution extends ArtifactorySynchronousNonBlockingStepExecution<Void> {

        private final transient ReleaseBundleCreateStep step;

        @Inject
        public Execution(ReleaseBundleCreateStep step, StepContext context) throws IOException, InterruptedException {
            super(context);
            this.step = step;
        }

        @Override
        protected Void runStep() throws Exception {
            ReleaseBundleCreateExecutor releaseBundleCreateExecutor = new ReleaseBundleCreateExecutor(step.getServer(), step.name, step.version, Util.replaceMacro(step.getSpec(), env),
                    step.storingRepository, step.signImmediately, step.dryRun, step.gpgPassphrase, step.releaseNotePath, step.releaseNoteSyntax, step.description, step.insecureTls, listener, build, ws, getContext());
            releaseBundleCreateExecutor.execute();
            return null;
        }

        @Override
        public org.jfrog.hudson.ArtifactoryServer getUsageReportServer() {
            return null;
        }

        @Override
        public String getUsageReportFeatureName() {
            return STEP_NAME;
        }

    }

    @Extension
    public static final class DescriptorImpl extends AbstractStepDescriptorImpl {

        public DescriptorImpl() {
            super(ReleaseBundleCreateStep.Execution.class);
        }

        @Override
        // The step is invoked by DistributionServer by the step name
        public String getFunctionName() {
            return STEP_NAME;
        }

        @Override
        public String getDisplayName() {
            return "Release Bundle Create";
        }

        @Override
        public boolean isAdvanced() {
            return true;
        }
    }
}
