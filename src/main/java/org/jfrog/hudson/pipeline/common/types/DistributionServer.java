package org.jfrog.hudson.pipeline.common.types;

import com.google.common.collect.Maps;
import hudson.model.Item;
import org.jenkinsci.plugins.scriptsecurity.sandbox.whitelists.Whitelisted;
import org.jenkinsci.plugins.workflow.cps.CpsScript;
import org.jfrog.build.api.util.Log;
import org.jfrog.build.extractor.clientConfiguration.DistributionManagerBuilder;
import org.jfrog.hudson.CredentialsConfig;
import org.jfrog.hudson.util.Credentials;
import org.jfrog.hudson.util.ProxyUtils;
import org.jfrog.hudson.util.plugins.PluginsUtils;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.jfrog.hudson.pipeline.common.Utils.appendBuildInfo;

/**
 * Represents an instance of Distribution configuration from pipeline script.
 */
public class DistributionServer implements Serializable {
    public static final String NAME = "name";
    public static final String VERSION = "version";
    public static final String SPEC = "spec";
    public static final String SERVER = "server";
    public static final String TARGET_PROPS = "targetProps";
    public static final String DRY_RUN = "dryRun";
    public static final String SIGN_IMMEDIATELY = "signImmediately";
    public static final String GPG_PASSPHRASE = "gpgPassphrase";
    public static final String DESCRIPTION = "description";
    public static final String RELEASE_NOTE_PATH = "releaseNotePath";
    public static final String RELEASE_NOTE_SYNTAX = "releaseNoteSyntax";
    public static final String STORING_REPOSITORY = "StoringRepository";
    public static final String INSECURE_TLS = "insecureTls";

    private final Connection connection = new Connection();
    private String id;
    private String url;
    private String username;
    private String password;
    private String credentialsId;
    private boolean bypassProxy;
    private transient CpsScript cpsScript;
    private boolean usesCredentialsId;

    public DistributionServer() {
    }

    public DistributionServer(org.jfrog.hudson.JFrogPlatformInstance jfrogPlatformInstance, Item parent) {
        id = jfrogPlatformInstance.getId();
        url = jfrogPlatformInstance.getDistributionUrl();
        if (PluginsUtils.isCredentialsPluginEnabled()) {
            credentialsId = jfrogPlatformInstance.getDeployerCredentialsConfig().getCredentialsId();
        } else {
            Credentials serverCredentials = jfrogPlatformInstance.getDeployerCredentialsConfig().provideCredentials(parent);
            username = serverCredentials.getUsername();
            password = serverCredentials.getPassword();
        }
        bypassProxy = jfrogPlatformInstance.isBypassProxy();
        connection.setRetry(jfrogPlatformInstance.getConnectionRetry());
        connection.setTimeout(jfrogPlatformInstance.getTimeout());
    }

    public CredentialsConfig createCredentialsConfig() {
        CredentialsConfig credentialsConfig = new CredentialsConfig(username, password, credentialsId, null);
        credentialsConfig.setIgnoreCredentialPluginDisabled(usesCredentialsId);
        return credentialsConfig;
    }

    public DistributionManagerBuilder getDistributionManagerBuilder(Log log, Item parent) {
        Credentials credentials = createCredentialsConfig().provideCredentials(parent);
        DistributionManagerBuilder builder = new DistributionManagerBuilder()
                .setServerUrl(url)
                .setUsername(credentials.getUsername())
                .setPassword(credentials.getUsername())
                .setLog(log)
                .setConnectionRetry(connection.getRetry())
                .setConnectionTimeout(connection.getTimeout());
        if (!bypassProxy) {
            builder.setProxyConfiguration(ProxyUtils.createProxyConfiguration());
        }
        return builder;
    }

    public void setCpsScript(CpsScript cpsScript) {
        this.cpsScript = cpsScript;
    }

    @Whitelisted
    private Map<String, Object> getReleaseBundleCreateObjectMap(Map<String, Object> arguments) {
        List<String> mandatoryKeys = Arrays.asList(NAME, VERSION, SPEC);
        if (!arguments.keySet().containsAll(mandatoryKeys)) {
            throw new IllegalArgumentException(String.format("%s, %s and %s are mandatory arguments", NAME, VERSION, SPEC));
        }
        List<String> optionalKeys = Arrays.asList(TARGET_PROPS, DRY_RUN, SIGN_IMMEDIATELY, GPG_PASSPHRASE, RELEASE_NOTE_PATH, RELEASE_NOTE_SYNTAX, STORING_REPOSITORY, INSECURE_TLS, DESCRIPTION);
        if (!optionalKeys.containsAll(arguments.keySet())) {
            throw new IllegalArgumentException("Only the following arguments are allowed, " + optionalKeys);
        }
        Map<String, Object> stepVariables = Maps.newLinkedHashMap(arguments);
        stepVariables.put(SERVER, this);
        return stepVariables;
    }

    @Whitelisted
    public void releaseBundleCreate(Map<String, Object> releaseBundleCreateArguments) {
        Map<String, Object> stepVariables = getReleaseBundleCreateObjectMap(releaseBundleCreateArguments);
        appendBuildInfo(cpsScript, stepVariables);

        // Throws CpsCallableInvocation - Must be the last line in this method
        cpsScript.invokeMethod("releaseBundleCreate", stepVariables);
    }
}
