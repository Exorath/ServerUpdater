package com.exorath.service.serverupdater;

import com.exorath.service.serverscaling.api.ServerScalingServiceAPI;
import com.exorath.service.serverscaling.res.GameType;
import com.google.gson.Gson;
import org.gitlab.api.GitlabAPI;
import org.gitlab.api.models.GitlabProject;
import org.gitlab.api.models.GitlabRepositoryTree;

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

    public void run() {
        try {
            GitlabProject gitlabProject = gitlabAPI.getProject(projectId);
            for (GitlabRepositoryTree tree : gitlabAPI.getRepositoryTree(gitlabProject, null, null, true)) {
                if (!tree.getType().startsWith("files/"))
                    continue;
                byte[] content = gitlabAPI.getRawFileContent(gitlabProject, null, tree.getPath());
                serverScalingServiceAPI.registerGameType(GSON.fromJson(new String(content), GameType.class));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
