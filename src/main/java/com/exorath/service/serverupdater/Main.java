package com.exorath.service.serverupdater;

import com.exorath.service.serverscaling.api.ServerScalingServiceAPI;
import org.gitlab.api.GitlabAPI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by toonsev on 8/16/2017.
 */
public class Main {
    private ServerUpdater serverUpdater;
    private final ScheduledExecutorService scheduler =
            Executors.newScheduledThreadPool(1);
    private static final Logger LOG = LoggerFactory.getLogger(com.exorath.service.connector.Main.class);

    public Main(){
        serverUpdater = new ServerUpdater(new ServerScalingServiceAPI(getServerScalingServiceAPIAddress()), getGitLabAPI(), getRepoId());
        scheduler.scheduleAtFixedRate(serverUpdater, 0, 5, TimeUnit.SECONDS);
        LOG.info("Server Updater " + this.serverUpdater.getClass() + " instantiated");
    }

    private String getServerScalingServiceAPIAddress(){
        String address = System.getenv("SERVERSCALING_SERVICE_ADDRESS");
        if (address == null) {
            System.out.println("ExoBasePlugin: Fatal error: " + "No SERVERSCALING_SERVICE_ADDRESS env var provided.");
            System.exit(-1);
        }
        return address;
    }
    private String getRepoId(){
        String address = System.getenv("REPOSITORY_ID");
        if (address == null) {
            System.out.println("ExoBasePlugin: Fatal error: " + "No REPOSITORY_ID env var provided.");
            System.exit(-1);
        }
        return address;
    }

    private GitlabAPI getGitLabAPI(){
        return GitlabAPI.connect("https://gitlab.com", getGitlabToken());
    }
    private String getGitlabToken(){
        String address = System.getenv("GITLAB_TOKEN");
        if (address == null) {
            System.out.println("ExoBasePlugin: Fatal error: " + "No GITLAB_TOKEN env var provided.");
            System.exit(-1);
        }
        return address;
    }
    public static void main(String[] args) {
        new Main();
    }
}
