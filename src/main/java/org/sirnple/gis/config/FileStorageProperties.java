package org.sirnple.gis.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Created by: sirnple
 * Created in: 2020-06-06
 * Description:
 */
@ConfigurationProperties(prefix = "file")
@Configuration
public class FileStorageProperties {
    private String uploadDir;
    private String porePressureDir;
    private String flowRateDir;
    private String seabedSlidingDir;
    private String waveDir;

    public String getUploadDir() {
        return uploadDir;
    }

    public void setUploadDir(String uploadDir) {
        this.uploadDir = uploadDir;
    }

    public String getPorePressureDir() {
        return porePressureDir;
    }

    public void setPorePressureDir(String porePressureDir) {
        this.porePressureDir = porePressureDir;
    }

    public String getFlowRateDir() {
        return flowRateDir;
    }

    public void setFlowRateDir(String flowRateDir) {
        this.flowRateDir = flowRateDir;
    }

    public String getSeabedSlidingDir() {
        return seabedSlidingDir;
    }

    public void setSeabedSlidingDir(String seabedSlidingDir) {
        this.seabedSlidingDir = seabedSlidingDir;
    }

    public String getWaveDir() {
        return waveDir;
    }

    public void setWaveDir(String waveDir) {
        this.waveDir = waveDir;
    }
}
