
package se.vgregion.metaservice.LemmatisationService;

import java.io.Writer;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

/**
 * The Lemmatisation service external interface
 */
public interface LemmatisationIntSvc {
    /**
     * Retrieve the lemmatisation and paradigms of the input word
     *
     * @param word Word to lemmatise and get paradigms for
     * @return
     */
    public ModelAndView getParadigms(@PathVariable String word);

    /**
     * Retrieve the XSD schema for the XML returned from getParadigms
     *
     * @return
     */
    public void getXMLSchema(Writer writer, HttpServletResponse response);
}
