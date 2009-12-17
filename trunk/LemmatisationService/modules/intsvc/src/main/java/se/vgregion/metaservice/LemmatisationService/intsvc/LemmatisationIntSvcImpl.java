
package se.vgregion.metaservice.LemmatisationService.intsvc;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Writer;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import se.vgregion.metaservice.LemmatisationService.LemmatisationIntSvc;
import se.vgregion.metaservice.LemmatisationService.LemmatisationSvc;
import se.vgregion.metaservice.LemmatisationService.domain.LemmatisedResponse;
import se.vgregion.metaservice.LemmatisationService.exception.InitializationException;

/**
 * LemmatisationIntSvcImpl
 */
@Controller
public class LemmatisationIntSvcImpl implements LemmatisationIntSvc {
    private static Logger log = Logger.getLogger(LemmatisationIntSvc.class);
    private LemmatisationSvc lemmationsationSvc;
    private Resource xsdResource;

    /**
     * Returns a XML response containing the lemmatisation and paradigms
     * of the parameter word.  
     *
     * @param word The word to lemmatise
     * @return
     */
    @RequestMapping(value="/lemmatisation/{word}", method=RequestMethod.GET)
    public ModelAndView getParadigms(@PathVariable String word) {
        LemmatisedResponse response = lemmationsationSvc.getParadigmsObject(word);

        // Include BindingResult.MODEL_KEY_PREFIX due to a bug in MarshallingView.java ?
        return new ModelAndView("jaxbView", BindingResult.MODEL_KEY_PREFIX + "paradigmsresponse", response);
    }

    @RequestMapping(value="/schema.xsd", method=RequestMethod.GET)
    public void getXMLSchema(Writer writer, HttpServletResponse response) {
        FileInputStream fis = null;
        BufferedInputStream bis = null;

        try {
            fis = new FileInputStream(xsdResource.getFile());
            bis = new BufferedInputStream(fis, 4096);

            response.setContentType("text/xml");
            response.setContentLength((int) xsdResource.getFile().length());

            int theChar;
            while ((theChar = bis.read()) != -1) {
                writer.write(theChar);
            }
            bis.close();
            fis.close();

            
        } catch (IOException ex) {
            try {
                if (fis != null) bis.close();
                if (bis != null) fis.close();
            } catch (IOException e) { }
        }
    }

    @Autowired(required=true)
    public void setLemmationsationSvc(LemmatisationSvc lemmationsationSvc) {
        this.lemmationsationSvc = lemmationsationSvc;
        try {
            lemmationsationSvc.init();
        } catch (InitializationException ex) {
            log.error("Error initializing lemmatisation service", ex);
        }
    }

    @Autowired(required=true)
    public void setXsdResource(Resource xsdResource) {
        this.xsdResource = xsdResource;
    }

 
}
