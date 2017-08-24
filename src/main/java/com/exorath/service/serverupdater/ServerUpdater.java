package com.exorath.service.serverupdater;

import com.exorath.service.serverscaling.api.ServerScalingServiceAPI;
import com.exorath.service.serverscaling.res.GameType;
import com.google.gson.Gson;
import org.gitlab.api.GitlabAPI;
import org.gitlab.api.models.GitlabProject;
import org.gitlab.api.models.GitlabRepositoryTree;

import java.util.concurrent.TimeUnit;

/**
 * Created by toonsev on 8/16/2017.
 */
public class ServerUpdater implements Runnable {
    private static final Gson GSON = new Gson();

    private ServerScalingServiceAPI serverScalingServiceAPI;
    private GitlabAPI gitlabAPI;
    private String projectId;

    public ServerUpdater(ServerScalingServiceAPI serverScalingServiceAPI, GitlabAPI gitlabAPI, String projectId) {
        this.serverScalingServiceAPI = serverScalingServiceAPI;
        this.gitlabAPI = gitlabAPI;
        this.projectId = projectId;
    }

    private Long lastCached;
    private String lastCommitId;


    public void run() {
        try {
            GitlabProject gitlabProject = gitlabAPI.getProject(projectId);
            String commitId = gitlabAPI.getBranch(gitlabProject, "master").getCommit().getId();
            if (lastCommitId != null && lastCommitId.equals(commitId) && lastCached != null && lastCached + TimeUnit.SECONDS.toMillis(180) >= System.currentTimeMillis())
                return;
            lastCached = System.currentTimeMillis();
            lastCommitId = commitId;
            for (GitlabRepositoryTree tree : gitlabAPI.getRepositoryTree(gitlabProject, null, null, true)) {
                if (!tree.getType().startsWith("blob") && !tree.getType().startsWith("file"))
                    continue;
                byte[] content = gitlabAPI.getRawFileContent(gitlabProject, gitlabProject.getDefaultBranch(), tree.getPath());
                serverScalingServiceAPI.registerGameType(GSON.fromJson(new String(content), GameType.class));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
