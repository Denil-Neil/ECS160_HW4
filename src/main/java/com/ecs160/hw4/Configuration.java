// Configuration class using Singleton pattern
package com.ecs160.hw4;
public class Configuration {
    private static Configuration instance;
    private String analysisType; // "weighted" or "non-weighted"
    private String jsonFileName;

    private Configuration() {
        // Private constructor to prevent direct instantiation
    }

    public static Configuration getInstance() {
        if (instance == null) {
            instance = new Configuration();
        }
        return instance;
    }

    public void setConfig(String analysisType, String jsonFileName) {
        this.analysisType = analysisType;
        this.jsonFileName = jsonFileName;
    }

    public String getAnalysisType() {
        return analysisType;
    }

    public String getJsonFileName() {
        return jsonFileName;
    }

    public boolean isWeighted() {
        return "weighted".equalsIgnoreCase(analysisType);
    }
}