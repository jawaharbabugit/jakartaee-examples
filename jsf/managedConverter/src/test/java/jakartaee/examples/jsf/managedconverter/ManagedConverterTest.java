/*
 * Permission to use, copy, modify, and/or distribute this software for any 
 * purpose with or without fee is hereby granted.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR(S) DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR(S) BE LIABLE FOR
 * ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN ACTION
 * OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR IN
 * CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */
package jakartaee.examples.jsf.managedconverter;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import jakartaee.examples.utils.JakartaEEServer;
import java.io.File;
import java.net.URL;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.test.api.ArquillianResource;
import static org.jboss.shrinkwrap.api.ShrinkWrap.create;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * The JUnit tests for the CDI managed converter example.
 *
 * @author Manfred Riem (mriem@manorrock.com)
 */
@RunWith(Arquillian.class)
@JakartaEEServer
public class ManagedConverterTest {

    /**
     * Stores the base URL.
     */
    @ArquillianResource
    private URL baseUrl;

    /**
     * Stores the web client.
     */
    private WebClient webClient;

    /**
     * Setup before testing.
     */
    @Before
    public void before() {
        webClient = new WebClient();
    }

    /**
     * Create the deployment web archive.
     *
     * @return the deployment web archive.
     */
    @Deployment
    public static WebArchive createDeployment() {
        return create(WebArchive.class).
                addAsWebInfResource(new File("src/main/webapp/WEB-INF/web.xml")).
                addAsWebResource(new File("src/main/webapp/index.xhtml")).
                addClasses(ManagedConverter.class, ManagedConverterBean.class);
    }

    /**
     * Tear down after testing.
     */
    @After
    public void after() {
        webClient.close();
    }

    /**
     * Test the CDI managed converter.
     *
     * @throws Exception when a serious error occurs.
     */
    @RunAsClient
    @Test
    public void testCdiManagedConverter() throws Exception {
        HtmlPage page = webClient.getPage(baseUrl + "/index.xhtml");
        String content = page.asXml();
        assertTrue(content.contains("\"1\""));
        HtmlInput input = page.getHtmlElementById("form:inputText");
        input.setValueAttribute("0");
        HtmlSubmitInput submit = page.getHtmlElementById("form:submit");
        page = submit.click();
        content = page.asXml();
        assertTrue(content.contains("\"/"));
        input = page.getHtmlElementById("form:inputText");
        input.setValueAttribute("2");
        submit = page.getHtmlElementById("form:submit");
        page = submit.click();
        content = page.asXml();
        assertTrue(content.contains("\"-Infinity\""));
    }
}
