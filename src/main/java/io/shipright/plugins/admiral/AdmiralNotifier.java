package io.shipright.plugins.admiral;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.*;
import hudson.model.BuildListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Notifier;
import hudson.tasks.Publisher;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.logging.Logger;

public class AdmiralNotifier extends Notifier {
    private final static Logger LOG = Logger.getLogger(AdmiralNotifier.class.getName());

    private final String projectTopicArn;
    private final String subjectTemplate;
    private final String messageTemplate;

    @DataBoundConstructor
    public AdmiralNotifier(String projectTopicArn, String subjectTemplate, String messageTemplate) {
        super();
        this.projectTopicArn = projectTopicArn;
        this.subjectTemplate = subjectTemplate;
        this.messageTemplate = messageTemplate;
    }

    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {
        return super.perform(build, launcher, listener);
    }

    @Override
    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.NONE;
    }

    @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Publisher> {

        private String admiralAccessKey;
        private String admiralSecretKey;
        private String defaultTopicArn;
        private String defaultMessageTemplate;
        private boolean defaultSendNotificationOnStart;

        public DescriptorImpl() {
            super(AdmiralNotifier.class);
            load();
        }

        @Override
        public String getDisplayName() {
            return "Admiral Notifier";
        }

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            return true;
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
            admiralAccessKey = formData.getString("admiralAccessKey");
            admiralSecretKey = formData.getString("admiralSecretKey");
            defaultTopicArn = formData.getString("defaultTopicArn");
            defaultMessageTemplate = formData.getString("defaultMessageTemplate");
            defaultSendNotificationOnStart = formData.getBoolean("defaultSendNotificationOnStart");

            save();
            return super.configure(req, formData);
        }

        public String getAdmiralAccessKey() {
            return admiralAccessKey;
        }

        public void setAdmiralAccessKey(String admiralAccessKey) {
            this.admiralAccessKey = admiralAccessKey;
        }

        public String getAdmiralSecretKey() {
            return admiralSecretKey;
        }

        public void setAdmiralSecretKey(String admiralSecretKey) {
            this.admiralSecretKey = admiralSecretKey;
        }

        public String getDefaultTopicArn() {
            return defaultTopicArn;
        }

        public String getDefaultMessageTemplate() {
            return StringUtils.isEmpty(defaultMessageTemplate) ? "${BUILD_URL}" : defaultMessageTemplate;
        }

        public boolean isDefaultSendNotificationOnStart() {
            return defaultSendNotificationOnStart;
        }

        public void setDefaultTopicArn(String defaultTopicArn) {
            this.defaultTopicArn = defaultTopicArn;
        }

        public void setDefaultMessageTemplate(String defaultMessageTemplate) {
            this.defaultMessageTemplate = defaultMessageTemplate;
        }

        public void setDefaultSendNotificationOnStart(boolean defaultSendNotificationOnStart) {
            this.defaultSendNotificationOnStart = defaultSendNotificationOnStart;
        }
    }
}
