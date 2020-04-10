package cgiserver;

import java.util.Arrays;

public class CGIRequestParams {

    private static final String KEY_METHOD = "METHOD";
    private static final String KEY_PATH = "REQUEST_PATH";
    private static final String KEY_PROTOCOL_TYPE = "PROTOCOL_TYPE";
    private static final String KEY_PROTOCOL_VERSION = "PROTOCOL_VERSION";
    private static final String KEY_QUERY_STRING = "QUERY_STRING";

    private int methodPos = -1;
    private int pathPos = -1;
    private int protTypePos = -1;
    private int protVerPos = -1;
    private int qsPos = -1;

    private int paramsCount = -1;
    private String[] params = new String[32];

    public void setMethod(String method) {
        methodPos = set(methodPos, KEY_METHOD, method);
    }

    public void setPath(String path) {
        pathPos = set(pathPos, KEY_PATH, path);
    }

    public void setProtocolType(String type) {
        protTypePos = set(protTypePos, KEY_PROTOCOL_TYPE, type);
    }

    public void setProtocolVersion(String ver) {
        protVerPos = set(protVerPos, KEY_PROTOCOL_VERSION, ver);
    }

    public void setQueryString(String qs) {
        qsPos = set(qsPos, KEY_QUERY_STRING, qs);
    }

    private int set(int pos, String key, String value) {
        if (pos == -1)
            return add(key, value);
        else {
            params[pos] = combine(key, value);
            return pos;
        }
    }

    public int add(String key, String value) {
        if (paramsCount + 1 == params.length) {
            params = Arrays.copyOf(params, params.length + 8);
        }
        params[++paramsCount] = combine(key, value);
        return paramsCount;
    }

    private String combine(String key, String value) {
        return key + "=" + value;
    }

    public String[] toArray() {
        return Arrays.copyOf(params, paramsCount + 1);
    }

}