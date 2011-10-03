package se.vgregion.metaservice.keywordservice.web;

import java.io.IOException;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import javax.faces.bean.RequestScoped;
import org.apache.myfaces.custom.fileupload.UploadedFile;
import se.vgregion.metaservice.keywordservice.KeyWordService;
import se.vgregion.metaservice.keywordservice.domain.Identification;
import se.vgregion.metaservice.keywordservice.domain.MedicalNode;
import se.vgregion.metaservice.keywordservice.domain.NodeListResponseObject;
import se.vgregion.metaservice.keywordservice.domain.document.TextDocument;
import se.vgregion.metaservice.keywordservice.domain.document.FileDocument;
import se.vgregion.metaservice.keywordservice.domain.Options;

@RequestScoped
@ManagedBean(name = "user")
@SessionScoped
public class UserBean implements Serializable {

    private UploadedFile uploadedFile;
    private String text;
    private Identification id = new Identification("vgr", "vgr");
    private static KeyWordService keywordService;
    private List<Keyword> keywords = new ArrayList<Keyword>();

    private void extractKeywordsFromResponse(NodeListResponseObject keywordResponse) {
        keywords.clear();
        Iterator<MedicalNode> iter = keywordResponse.getNodeList().iterator();
        while (iter != null && iter.hasNext()) {
            MedicalNode node = iter.next();
            keywords.add(new Keyword(node.getName(), node.getSynonyms()));
        }
    }

    public List<Keyword> getKeywords() {
        return keywords;
    }

    public void setMonkeys(Collection<String> keywords){
        return;
    }

    private Options getOptions() {
        Options options = new Options();
        options.setWordsToReturn(5);
        options.setInputWords(30);
        options.setMatchSynonyms(false);
        options.setSynonymize(false);
        options.setUrl("");
        return options;
    }

    public void setText(String text) {
        this.text = text;
        TextDocument document = new TextDocument();
        document.setTitle("");
        document.setTextContent(text);
        if (keywordService != null) {
            NodeListResponseObject keywordResponse = keywordService.getKeywords(id, "1234567890", document, getOptions());
            extractKeywordsFromResponse(keywordResponse);
        }
    }

    public String getText() {
        return text;
    }

    public void setKeywordService(KeyWordService keywordService) {
        UserBean.keywordService = keywordService;
    }

    public static KeyWordService getKeywordService() {
        return keywordService;
    }

    public void submit() throws IOException {
        if (uploadedFile != null) {
            FileDocument file = new FileDocument();
            file.setFilename(uploadedFile.getName());
            file.setData(uploadedFile.getBytes());
            if (keywordService != null) {
                NodeListResponseObject keywordResponse = keywordService.getKeywords(id, "1234567890", file, getOptions());
                extractKeywordsFromResponse(keywordResponse);
            }
        }
    }

    public UploadedFile getUploadedFile() {
        return uploadedFile;
    }

    public void setUploadedFile(UploadedFile uploadedFile) {
        this.uploadedFile = uploadedFile;
    }
}
