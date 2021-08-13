package KnowledgeMenu.portlet;

import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.model.JournalFolder;
import com.liferay.journal.service.JournalArticleService;
import com.liferay.journal.service.JournalFolderService;
import com.liferay.journal.util.comparator.ArticleIDComparator;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.portlet.Portlet;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import KnowledgeMenu.config.KnowledgeMenuConfiguration;
import KnowledgeMenu.constants.KnowledgeMenuPortletKeys;
import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Modified;

/**
 * @author carlos
 */
@Component(
	immediate = true,
	property = {
		"com.liferay.portlet.display-category=category.sample",
		"com.liferay.portlet.header-portlet-css=/css/main.css",
		"com.liferay.portlet.instanceable=true",
		"javax.portlet.display-name=KnowledgeMenu",
		"javax.portlet.init-param.template-path=/",
		"javax.portlet.init-param.view-template=/view.jsp",
		"javax.portlet.name=" + KnowledgeMenuPortletKeys.KNOWLEDGEMENU,
		"javax.portlet.resource-bundle=content.Language",
		"javax.portlet.security-role-ref=power-user,user",
		"com.liferay.fragment.entry.processor.portlet.alias=webcontent-menu"
	}
	,service = Portlet.class
	,configurationPid = "com.knowledgemenu.KnowledgeMenuConfiguration"
)
public class KnowledgeMenuPortlet extends MVCPortlet {
	
	@Override
	public void doView( RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {
		
		ThemeDisplay themeDisplay = (ThemeDisplay) renderRequest.getAttribute(WebKeys.THEME_DISPLAY);
		long groupId = themeDisplay.getSiteGroupId();
        boolean ascending=true;
		OrderByComparator<JournalArticle> orderByComparator = new ArticleIDComparator(ascending);
		
		List<JournalArticle> articles = _jaService.getArticlesByStructureId(groupId, _configuration.structureKey(), 0, Integer.MAX_VALUE,orderByComparator);
		JSONArray jsonAA = JSONFactoryUtil.createJSONArray();
		articles.stream().forEach(a->{
			JSONObject json = JSONFactoryUtil.createJSONObject();
			json.put("title",a.getTitle(themeDisplay.getLocale()));
			json.put("folder",a.getFolderId());
			json.put("id",a.getArticleId());
			try {
				json.put("friendlyUrl", _configuration.baseUrl()+a.getUrlTitle(themeDisplay.getLocale()));
			} catch (PortalException e) {
				e.printStackTrace();
			}
			jsonAA.put(json);
		});
		
		try{
			HttpServletRequest request = PortalUtil.getHttpServletRequest(renderRequest);
			String url = request.getRequestURL().toString().split("/-/")[1];
			JournalArticle a = _jaService.getDisplayArticleByUrlTitle(groupId, url);
			renderRequest.setAttribute("articleId", a.getArticleId());
		}catch(Exception e) {
			System.out.println("No Article to display. Display default");
		}

		boolean r=true;
		List<JournalFolder> folders = _jfService.getFolders(groupId);
		
		JSONArray jsonAF = JSONFactoryUtil.createJSONArray();
		folders.stream().forEach(f->{
			String folderName = f.getName();
			JSONObject json = JSONFactoryUtil.createJSONObject();	
			if(f.getExpandoBridge().hasAttribute(_configuration.localisedFolderName())){
				Map<String,String> localizedFolderName= 
						(Map<String,String>)f.getExpandoBridge().getAttribute(_configuration.localisedFolderName());
				if(localizedFolderName.get(themeDisplay.getLocale())!=null) 
				folderName=localizedFolderName.get(themeDisplay.getLocale());
			}
			json.put("name", folderName);
			json.put("parent", f.getParentFolderId());
			json.put("id", f.getFolderId());
			jsonAF.put(json);
		});
		renderRequest.setAttribute("articles", jsonAA.toJSONString());
		renderRequest.setAttribute("folders", jsonAF.toJSONString());
		renderRequest.setAttribute("baseFolder", _configuration.baseFolder());

		include(viewTemplate, renderRequest, renderResponse);
	}
	
    @Activate
    @Modified
    protected void activate(Map<String, Object> properties) {
        _configuration = ConfigurableUtil.createConfigurable( KnowledgeMenuConfiguration.class, properties);
    }
	
	
	@Reference JournalArticleService _jaService;
	@Reference JournalFolderService _jfService;
	private volatile KnowledgeMenuConfiguration _configuration;
}
