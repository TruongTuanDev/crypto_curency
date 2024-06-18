package com.example.testapi.config;

public class PrivateConfig {
    private static final String API_KEY = "yZT0ggSeSrmdVTmsh1A5PJ1XzbX9kWXcWFpTkmFwJ1b3eLeVcWNWZuphVluJ4epD";
    private static final String SECRET_KEY = "ajaewcn1k5KxSCYoC3XO7f4qUFekSYKVv6oWz9GCR9uMStR2ZOz1CmN1MPJxxcqE";

    public static String getApiKey() {
        return API_KEY;
    }

    public static String getSecretKey() {
        return SECRET_KEY;
    }
}
