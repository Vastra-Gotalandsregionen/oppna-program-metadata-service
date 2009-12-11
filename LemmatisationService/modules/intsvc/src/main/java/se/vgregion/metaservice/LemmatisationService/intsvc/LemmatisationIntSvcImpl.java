
package se.vgregion.metaservice.LemmatisationService.intsvc;

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

    /**
     * Returns a XML response containing the lemmatisation and paradigms
     * of the parameter word.  
     *
     * @param word The word to lemmatise
     * @return
     */
    @RequestMapping(value="/{word}", method=RequestMethod.GET)
    public ModelAndView getParadigms(@PathVariable String word) {
        LemmatisedResponse response = lemmationsationSvc.getParadigmsObject(word);

        // Include BindingResult.MODEL_KEY_PREFIX due to a bug in MarshallingView.java ?
        return new ModelAndView("xmlView", BindingResult.MODEL_KEY_PREFIX + "paradigmsresponse", response);
    }

    
    private LemmatisationSvc lemmationsationSvc;

    @Autowired(required=true)
    public void setLemmationsationSvc(LemmatisationSvc lemmationsationSvc) {
        this.lemmationsationSvc = lemmationsationSvc;
        try {
            lemmationsationSvc.init();
        } catch (InitializationException ex) {
            ex.printStackTrace();
        }
    }

}
