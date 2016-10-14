package org.jenkinci.plugins.gmd;

import hudson.Extension;
import hudson.model.Item;
import hudson.model.listeners.ItemListener;
import jenkins.scm.api.SCMHead;
import jenkins.scm.api.actions.ChangeRequestAction;
import org.apache.commons.lang3.StringEscapeUtils;
import org.jenkinsci.plugins.github_branch_source.PullRequestSCMHead;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.jenkinsci.plugins.workflow.multibranch.BranchJobProperty;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;

@Extension
@SuppressWarnings("unused")
public class MultibranchBranchJobListener extends ItemListener {

    @Override
    public void onCreated(Item item) {
        if (!(item instanceof WorkflowJob)) {
            return;
        }

        WorkflowJob job = (WorkflowJob) item;
        BranchJobProperty property = job.getProperty(BranchJobProperty.class);
        if (property == null) {
            return;
        }

        SCMHead head = property.getBranch().getHead();
        if (!(head instanceof PullRequestSCMHead)) {
            return;
        }

        // PullRequestAction extends ChangeRequestAction in github-branch-source, and is package-private
        ChangeRequestAction metadata = head.getAction(ChangeRequestAction.class);
        URL url = invoke(metadata, "getURL");
        String title = invoke(metadata, "getTitle");
        if (title != null) {
            title = StringEscapeUtils.escapeHtml4(title);
        }

        // TODO: Could create output according to which source formatter is currently enabled...
        String description = String.format("<br/><font size='+1'>\n<b>%1$s</b><br/>\n" +
                "<a href='%2$s'>%2$s</a>\n</font><br/>", title, url);

        try {
            job.setDescription(description);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static <T> T invoke(ChangeRequestAction action, String methodName) {
        try {
            Method method = action.getClass().getMethod(methodName);
            method.setAccessible(true);
            return (T) method.invoke(action);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

}
