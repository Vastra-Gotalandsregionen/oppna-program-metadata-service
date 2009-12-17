
package se.vgregion.metaservice.LemmatisationService.intsvc;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.util.logging.Level;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
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
    public void getXMLSchema(Writer writer) {
        File xsdFile = new File("E:\\projects\\oppna-program-metadata-service\\LemmatisationService\\modules\\intsvc\\target\\MetaService-LemmatisationService-module-intsvc\\WEB-INF\\classes\\schema1.xsd");
        FileInputStream fis = null;
        BufferedInputStream bis = null;

        try {
            fis = new FileInputStream(xsdFile);
            bis = new BufferedInputStream(fis, 4096);
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

    
 
}
