package crixec.app.hostsupdater;

/**
 * Created by crixec on 16-11-19.
 */

public class Host {
    private String hostName;
    private String hostUrl;
    private boolean isRecommand;

    public Host(String hostName, String hostUrl, boolean isRecommand) {
        this.hostName = hostName;
        this.hostUrl = hostUrl;
        this.isRecommand = isRecommand;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getHostUrl() {
        return hostUrl;
    }

    public void setHostUrl(String hostUrl) {
        this.hostUrl = hostUrl;
    }

    public boolean isRecommand() {
        return isRecommand;
    }

    public void setRecommand(boolean recommand) {
        isRecommand = recommand;
    }
}
