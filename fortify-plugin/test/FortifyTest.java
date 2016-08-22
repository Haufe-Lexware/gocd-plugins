import com.thoughtworks.go.plugin.api.task.JobConsoleLogger;
import com.tw.go.plugin.common.Context;
import java.util.Map;
import com.tw.go.task.fortify.FortifyRequest;
import com.tw.go.task.fortify.FortifyTaskExecutor;

import org.json.JSONArray;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import static org.junit.Assert.assertEquals;

import java.security.GeneralSecurityException;

public class FortifyTest
{
    FortifyRequest request;

    @Before
    public void init() throws GeneralSecurityException
    {
        request = new FortifyRequest("", "", "", true);
        request.setUsername("user");
        request.setPassword("password");
        request.setSscProject("project");
        request.setSscVersion("version");
    }

    @Test
    public void requestPropertiesTest() throws GeneralSecurityException
    {
        assertEquals("user", request.getUsername());
        assertEquals("password", request.getPassword());
        assertEquals("project", request.getSscProject());
        assertEquals("version", request.getSscVersion());
    }

    @Test public void getRequestTest()
    {
        assertEquals(true, !"".equals(request.request("GET", "https://httpbin.org/get", "")));
    }

    @Test public void postRequestTest()
    {
        assertEquals(true, !"".equals(request.request("POST", "http://posttestserver.com/post.php", "")));
    }
}