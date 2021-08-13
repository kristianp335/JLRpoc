package KnowledgeMenu.config;

import aQute.bnd.annotation.metatype.Meta;

@Meta.OCD(id = "com.knowledgemenu.KnowledgeMenuConfiguration")
public interface KnowledgeMenuConfiguration {

    @Meta.AD( deflt = "37849", required = false)
    public Integer baseFolder();
    
    @Meta.AD( deflt = "http://localhost:8080/web/guest/-/", required = false)
    public String baseUrl();

    @Meta.AD( deflt = "38121", required = false)
    public String structureKey();
    
    @Meta.AD( deflt = "Localised Folder Name" ,required = false)
    public String localisedFolderName();

}