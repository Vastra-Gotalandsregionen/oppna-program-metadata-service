
package se.vgregion.metaservice.LemmatisationService;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

/**
 * The Lemmatisation service external interface
 */
public interface LemmatisationIntSvc {
    public ModelAndView getParadigms(@PathVariable String word);
}
