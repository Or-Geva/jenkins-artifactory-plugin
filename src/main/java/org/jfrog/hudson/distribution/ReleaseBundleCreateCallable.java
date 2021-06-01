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
    private final boolean sign;
    private final String passphrase;
    private final String releaseNotePath;
    private final ReleaseBundleCreate.ReleaseNotesSyntax releaseNoteSyntax;
    private final String repo;
    private final boolean insecureTls;
    private DistributionManagerBuilder distributionManagerBuilder;

    public ReleaseBundleCreateCallable(String name, String version, String spec, String repo, boolean sign, boolean dryRun,
                                       String passphrase, String releaseNotePath, ReleaseBundleCreate.ReleaseNotesSyntax releaseNoteSyntax,
                                       boolean insecureTls, Log logger) {
        this.spec = spec;
        this.name = name;
        this.sign = sign;
        this.repo = repo;
        this.log = logger;
        this.dryRun = dryRun;
        this.version = version;
        this.passphrase = passphrase;
        this.insecureTls = insecureTls;
        this.releaseNotePath = releaseNotePath;
        this.releaseNoteSyntax = releaseNoteSyntax;
    }

    public void setDistributionManager(DistributionManagerBuilder distributionManagerBuilder) {
        this.distributionManagerBuilder = distributionManagerBuilder;
    }

    @Override
    public Void invoke(File file, VirtualChannel channel) throws IOException, InterruptedException {
        ReleaseBundleCreate releaseBundleCreate = new ReleaseBundleCreate(name, version, spec, repo, sign, distributionManagerBuilder, dryRun, passphrase, releaseNotePath, releaseNoteSyntax, insecureTls, log);
        releaseBundleCreate.execute();
        return null;
    }
}
