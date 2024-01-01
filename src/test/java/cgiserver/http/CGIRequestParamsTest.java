package cgiserver.http;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class CGIRequestParamsTest {

    @Test
    public void testSettingAllParams() {
        CGIRequestParams params = new CGIRequestParams();

        params.setMethod("method");
        params.setPath("path");
        params.setProtocolType("protocolType");
        params.setProtocolVersion("version");
        params.setQueryString("queryString");

        String[] values = params.toArray();

        assertEquals(Integer.valueOf(5), Integer.valueOf(values.length));
        assertEquals("METHOD=method", values[0]);
        assertEquals("REQUEST_PATH=path", values[1]);
        assertEquals("PROTOCOL_TYPE=protocolType", values[2]);
        assertEquals("PROTOCOL_VERSION=version", values[3]);
        assertEquals("QUERY_STRING=queryString", values[4]);
    }
    
}
