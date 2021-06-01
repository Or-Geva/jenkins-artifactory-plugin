package org.jfrog.hudson.distribution;

import hudson.remoting.VirtualChannel;
import jenkins.MasterToSlaveFileCallable;
import org.jfrog.build.api.util.Log;
import org.jfrog.build.extractor.clientConfiguration.DistributionManagerBuilder;
import org.jfrog.build.extractor.distribution.ReleaseBundleCreate;

import java.io.File;
import java.io.IOException;

public class ReleaseBundleCreateCallable extends MasterToSlaveFileCallable<Void> {
    private final Log log;
    private final String spec;
    private final String name;
    private final String version;
    private final boolean dryRun;
    private final String description;
    private final boolean insecureTls;
    private final String gpgPassphrase;
    private final String releaseNotePath;
    private final boolean signImmediately;
    private final String StoringRepository;
    private DistributionManagerBuilder distributionManagerBuilder;
    private final ReleaseBundleCreate.ReleaseNotesSyntax releaseNoteSyntax;

    public ReleaseBundleCreateCallable(String name, String version, String spec, String StoringRepository, boolean signImmediately, boolean dryRun,
                                       String gpgPassphrase, String releaseNotePath, ReleaseBundleCreate.ReleaseNotesSyntax releaseNoteSyntax, String description,
                                       boolean insecureTls, Log logger) {
        this.spec = spec;
        this.name = name;
        this.log = logger;
        this.dryRun = dryRun;
        this.version = version;
        this.description = description;
        this.insecureTls = insecureTls;
        this.gpgPassphrase = gpgPassphrase;
        this.signImmediately = signImmediately;
        this.releaseNotePath = releaseNotePath;
        this.StoringRepository = StoringRepository;
        this.releaseNoteSyntax = releaseNoteSyntax;
    }

    public void setDistributionManager(DistributionManagerBuilder distributionManagerBuilder) {
        this.distributionManagerBuilder = distributionManagerBuilder;
    }

    @Override
    public Void invoke(File file, VirtualChannel channel) throws IOException, InterruptedException {
        ReleaseBundleCreate releaseBundleCreate = new ReleaseBundleCreate(name, version, spec, StoringRepository, signImmediately, distributionManagerBuilder, dryRun, gpgPassphrase, releaseNotePath, releaseNoteSyntax, description, insecureTls, log);
        releaseBundleCreate.execute();
        return null;
    }
}
