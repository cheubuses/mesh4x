package org.mesh4j.sync.servlet;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.TimeZone;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mesh4j.geo.coder.IGeoCoder;
import org.mesh4j.sync.adapters.feed.ISyndicationFormat;
import org.mesh4j.sync.adapters.kml.exporter.KMLExporter;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.payload.mappings.IMapping;
import org.mesh4j.sync.payload.schema.ISchema;
import org.mesh4j.sync.payload.schema.xform.SchemaToXFormTranslator;
import org.mesh4j.sync.security.NullIdentityProvider;
import org.mesh4j.sync.utils.DateHelper;
import org.mesh4j.sync.web.FeedRepositoryFactory;
import org.mesh4j.sync.web.IFeedRepository;
import org.mesh4j.sync.web.geo.coder.GeoCoderFactory;

public class FeedServlet extends HttpServlet {

	private static final String PROPERTY_HISTORY_ACTIVE = "history.active";
	
	private static final String PARAM_FILTER = "filter";
	private static final String PARAM_SOURCE_ID = "sourceID";
	private static final String PARAM_DESCRIPTION = "description";
	private static final String PARAM_NEW_SOURCE_ID = "newSourceID";
	private static final String PARAM_ACTION = "action";
	private static final String PARAM_BY = "by";
	private static final String PARAM_VIEW_GROUP_MESH_ITEMS = "viewALLGroupMeshItems";
	private static final String PARAM_XFORM = "xform";
	
	private static final String PARAM_SYNC_ID = "syncId";
	
	private static final String PARAM_FORMAT = "format";
	private static final String PARAM_CONTENT = "content";

	private static final String RESOURCE_MAPPINGS = "mappings";
	private static final String RESOURCE_SCHEMA = "schema";
	private static final String RESOURCE_HISTORY = "history";

	private static final String ACTION_DELETE = "delete";
	private static final String ACTION_UPLOAD_MESH_DEFINITION = "uploadMeshDefinition";
	private static final String ACTION_CLEAN = "clean";

	private static final long serialVersionUID = 8932466869419169112L;
	
	// MODEL VARIABLES
	private IFeedRepository feedRepository;
	private IGeoCoder geoCoder;
	private boolean historyActive = false;
	
	// BUSINESS METHODS

	public FeedServlet(){
		super();
	}

	public void init() throws ServletException{
		this.log("Mesh4x is starting up.....");
		
		super.init();		
		
		try{
			Properties prop = getProperties();

			this.feedRepository = FeedRepositoryFactory.createFeedRepository(this, prop); 
			this.geoCoder = GeoCoderFactory.createGeoCoder(this, prop);
			
			String his = prop.getProperty(PROPERTY_HISTORY_ACTIVE);
			if(his == null || his.length() == 0){
				this.historyActive = false;
			} else {
				this.historyActive = Boolean.valueOf(his);
			}
		} catch(Exception e){
			this.log(e.getMessage());
			throw new ServletException(e);
		}
		this.log("Mesh4x is running.....");
	}

	private Properties getProperties() throws Exception {
		String fileName = this.getServletContext().getRealPath("/properties/mesh4j.properties");
		FileReader reader = new FileReader(fileName);
		Properties prop = new Properties();
		prop.load(reader);
		return prop;
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String sourceID = this.getSourceID(request);
		String link = getFeedLink(request);

		if(sourceID != null && sourceID.endsWith(RESOURCE_SCHEMA)){
			String contentFormatName = request.getParameter(PARAM_CONTENT);
			Format contentFormat = Format.getFormat(contentFormatName);
			processGetSchema(response, sourceID, link, contentFormat);
		} else if(sourceID != null && sourceID.endsWith(RESOURCE_MAPPINGS)){
			processGetMappings(response, sourceID, link);
		} else if(sourceID != null && sourceID.endsWith(RESOURCE_HISTORY)){
			processGetHistory(request, response, sourceID, link);
		} else {
			if(sourceID != null && !this.feedRepository.existsFeed(sourceID)){  // sourceID == null ==> Get all feeds
				response.sendError(404, sourceID);
			} else {
				String formatName = request.getParameter(PARAM_FORMAT);     // format=rss20/atom10/kml
				Format format = Format.getFormat(formatName);
				if(format != null && format.isKML()){					
					processGetKML(request, response, sourceID, link);
				}else {
					String contentFormatName = request.getParameter(PARAM_CONTENT);
					Format contentFormat = Format.getFormat(contentFormatName);
					
					try{
						processGetFeed(request, response, sourceID, link, format, contentFormat);
					} catch (Exception e) {
						throw new ServletException(e);
					}
				}
			}
		}
	}

	private String getFeedLink(HttpServletRequest request) {
		return request.getRequestURL().toString();
	}

	private void processGetFeed(HttpServletRequest request, HttpServletResponse response, String sourceID, String link, Format feedFormat, Format contentFormat) throws Exception {
		ISyndicationFormat syndicationFormat = Format.getSyndicationFormat(feedFormat);
		if(syndicationFormat == null){
			response.sendError(404, feedFormat.name());
		} else {
			Date sinceDate = this.getSinceDate(request);
			String filterQuery = request.getParameter("filter");
			String responseContent = "";
			if(this.feedRepository.isMeshGroup(sourceID) && mustViewAllItems(request)){
				responseContent = this.feedRepository.readFeedGroup(sourceID, link, syndicationFormat, contentFormat, this.geoCoder, sinceDate, filterQuery);	
			} else {
				responseContent = this.feedRepository.readFeed(sourceID, link, syndicationFormat, contentFormat, this.geoCoder, sinceDate, filterQuery);
			}
						
			responseContent = responseContent.replaceAll("&lt;", "<");	// TODO (JMT) remove ==>  xml.replaceAll("&lt;", "<"); 
			responseContent = responseContent.replaceAll("&gt;", ">");
			
			response.setContentType(syndicationFormat.getContentType());
			response.setContentLength(responseContent.length());
			PrintWriter out = response.getWriter();
			out.println(responseContent);
		}
	}

	private boolean mustViewAllItems(HttpServletRequest request) {
		return request.getParameter(PARAM_VIEW_GROUP_MESH_ITEMS) != null;
	}

	private void processGetKML(HttpServletRequest request, HttpServletResponse response, String sourceID, String link)throws ServletException, IOException {
		Date sinceDate = this.getSinceDate(request);
		String filterQuery = this.getFilterQuery(request);
			
		try{
			ISchema schema = this.feedRepository.getSchema(sourceID, link);
			IMapping mapping= this.feedRepository.getMappings(sourceID, link, this.geoCoder);
		
			List<Item> items = this.feedRepository.getAll(sourceID, link, sinceDate, filterQuery, schema, mapping);
			String responseContent = KMLExporter.generateKML(sourceID, items, schema, mapping);
			
			response.setContentType("application/vnd.google-earth.kml+xml");
			response.setContentLength(responseContent.length());
			PrintWriter out = response.getWriter();
			out.println(responseContent);
		} catch (Exception e) {
			throw new ServletException(e);
		}
	}

	private void processGetSchema(HttpServletResponse response, String sourceID, String link, Format contentFormat) throws IOException, ServletException {
		try{
			link = link.substring(0, link.length() - "/schema".length());
			sourceID = sourceID.substring(0, sourceID.length() - "/schema".length());
	
			if(!this.feedRepository.existsFeed(sourceID)){
				response.sendError(404, sourceID);
			} else {
				String schemaXML = "";
				
				if(contentFormat != null && contentFormat.isXForm()){
					schemaXML = this.feedRepository.getXForm(sourceID, link);
				}
				
				if(schemaXML.length() == 0){
					ISchema schema = this.feedRepository.getSchema(sourceID, link);
					if(schema != null){
						if(contentFormat != null && contentFormat.isXForm()){
							schemaXML = SchemaToXFormTranslator.translate(schema);
						} else {
							schemaXML = schema.asXML();
						}
					}
				}
				
				String responseContent = schemaXML.length() == 0 ? "" : "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>"+ schemaXML;			
				responseContent = responseContent.replaceAll("&lt;", "<");	// TODO (JMT) remove ==>  xml.replaceAll("&lt;", "<"); 
				responseContent = responseContent.replaceAll("&gt;", ">");
				
				response.setContentType("text/plain");
				response.setContentLength(responseContent.length());
				PrintWriter out = response.getWriter();
				out.println(responseContent);
			}
		} catch (Exception e) {
			throw new ServletException(e);
		}
	}
	
	private void processGetHistory(HttpServletRequest request, HttpServletResponse response, String sourceID, String link) throws IOException, ServletException {
		if(!this.historyActive){
			response.sendError(404, "item history is not active");
		} else {
		
			link = link.substring(0, link.length() - "/history".length());
			sourceID = sourceID.substring(0, sourceID.length() - "/history".length());
	
			if(!this.feedRepository.existsFeed(sourceID)){
				response.sendError(404, sourceID);
			} else {
				try{
					String formatName = request.getParameter(PARAM_FORMAT);     // format=rss20/atom10/kml
					Format feedFormat = Format.getFormat(formatName);
					ISyndicationFormat syndicationFormat = Format.getSyndicationFormat(feedFormat);
					if(syndicationFormat == null){
						response.sendError(404, formatName);
					} else {
						
						String syncId = request.getParameter(PARAM_SYNC_ID);
						if(syncId == null){
							response.sendError(404, PARAM_SYNC_ID);
						} else {
							String responseContent = this.feedRepository.getHistory(sourceID, link, syndicationFormat, syncId);
														
							responseContent = responseContent.replaceAll("&lt;", "<");	// TODO (JMT) remove ==>  xml.replaceAll("&lt;", "<"); 
							responseContent = responseContent.replaceAll("&gt;", ">");
							
							response.setContentType(syndicationFormat.getContentType());
							response.setContentLength(responseContent.length());
							PrintWriter out = response.getWriter();
							out.println(responseContent);
						}
					}
				} catch (Exception e) {
					throw new ServletException(e);
				}
			}
		}
	}

	private void processGetMappings(HttpServletResponse response, String sourceID, String link) throws IOException, ServletException {
		link = link.substring(0, link.length() - "/mappings".length());
		sourceID = sourceID.substring(0, sourceID.length() - "/mappings".length());

		if(!this.feedRepository.existsFeed(sourceID)){
			response.sendError(404, sourceID);
		} else {
			IMapping mappingsResolver;
			try {
				mappingsResolver = this.feedRepository.getMappings(sourceID, link, this.geoCoder);
			} catch (Exception e) {
				throw new ServletException(e);
			}
			String responseContent = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>" + mappingsResolver.asXML();
			responseContent = responseContent.replaceAll("&lt;", "<");	// TODO (JMT) remove ==>  xml.replaceAll("&lt;", "<"); 
			responseContent = responseContent.replaceAll("&gt;", ">");
			
			response.setContentType("text/plain");
			response.setContentLength(responseContent.length());
			PrintWriter out = response.getWriter();
			out.println(responseContent);
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {		
		String action = request.getParameter(PARAM_ACTION);
		if(ACTION_CLEAN.equals(action)){
			cleanFeed(request, response);
		} else if (ACTION_UPLOAD_MESH_DEFINITION.equals(action)){
			uploadMeshDefinition(request, response);
		} else if(ACTION_DELETE.equals(action)){
			deleteFeed(request, response);
		}else {
			try{
				if(isAddNewItemFromRawData(request)){ 
					addNewItemFromRawData(request, response);
				} else {			
					synchronizeFeed(request, response);
				}
			} catch (Exception e) {
				throw new ServletException(e);
			}
		}
	}
	
	private boolean isAddNewItemFromRawData(HttpServletRequest request) {
		String sourceId = this.getSourceID(request);
		return sourceId.endsWith("/add");
	}

	private void cleanFeed(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String sourceID = request.getParameter(PARAM_SOURCE_ID);
		if(!this.feedRepository.existsFeed(sourceID)){
			response.sendError(404, sourceID);
		} else {		
			this.feedRepository.cleanFeed(sourceID);
			String link = getFeedLink(request)+ "/" + sourceID;
			response.sendRedirect(link);
		}
	}
	
	private void deleteFeed(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String sourceID = request.getParameter(PARAM_SOURCE_ID);
		if(!this.feedRepository.existsFeed(sourceID)){
			response.sendError(404, sourceID);
		} else {		
			String link = getFeedLink(request)+ "/" + sourceID;
			this.feedRepository.deleteFeed(sourceID, link, getBy(request));
			response.sendRedirect(getFeedLink(request));
		}
	}

	private void synchronizeFeed(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String sourceID = this.getSourceID(request);
		if(!this.feedRepository.existsFeed(sourceID)){
			response.sendError(404, sourceID);
		} else {		
			String formatName = request.getParameter(PARAM_FORMAT);  		// format=rss20/atom10
			Format feedFormat = Format.getFormat(formatName);
			ISyndicationFormat syndicationFormat = Format.getSyndicationFormat(feedFormat);	
			if(syndicationFormat == null){
				response.sendError(404, formatName);	
			} else {
				String feedXml = this.readXML(request);
				if(feedXml == null){
					response.sendError(404, sourceID);
				} else {
					String link = getFeedLink(request);
					String contentFormatName = request.getParameter(PARAM_CONTENT);  		// format=rss20/atom10
					Format contentFormat = Format.getFormat(contentFormatName);
					String responseContent = this.feedRepository.synchronize(sourceID, link, syndicationFormat, contentFormat, this.geoCoder, feedXml);

					response.setContentType(syndicationFormat.getContentType());
					response.setContentLength(responseContent.length());
					PrintWriter out = response.getWriter();
					out.println(responseContent);
				}
			}
		}
	}
	
	private void addNewItemFromRawData(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String sourceID = this.getSourceID(request);
		sourceID = sourceID.substring(0, sourceID.length() - 4);  // 4 => "/add" size
		
		if(!this.feedRepository.existsFeed(sourceID)){
			response.sendError(404, sourceID);
		} else {		
			//String rawXml = this.readXML(request);
			String rawXml = request.getParameter("data");
			if(rawXml == null){
				response.sendError(404, sourceID);
			} else {
				String link = getFeedLink(request);
				link = link.substring(0, link.length() -4);
				this.feedRepository.addNewItemFromRawContent(sourceID, link, rawXml, getBy(request));
				response.sendRedirect(link);
			}
		}
	}

	private void uploadMeshDefinition(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String newSourceID = request.getParameter(PARAM_NEW_SOURCE_ID);
		if(newSourceID == null){
			response.sendError(404, newSourceID);
		} else {
			String formatName = request.getParameter(PARAM_FORMAT);
			Format feedFormat = Format.getFormat(formatName);
			
			String description = request.getParameter(PARAM_DESCRIPTION);
			String schema = request.getParameter(RESOURCE_SCHEMA);
			schema = normalizeXML(schema);
			
			String mappings = request.getParameter(RESOURCE_MAPPINGS);
			mappings = normalizeXML(mappings);
			
			String xform = request.getParameter(PARAM_XFORM);
			xform=normalizeXML(xform);
			
			ISyndicationFormat syndicationFormat = Format.getSyndicationFormat(feedFormat);	
			if(syndicationFormat == null){
				response.sendError(404, formatName);	
			}
			
			String link = getFeedLink(request)+ "/" + newSourceID;
			
			String by = getBy(request);
			
			if(this.feedRepository.existsFeed(newSourceID)){
				this.feedRepository.updateFeed(newSourceID, syndicationFormat, link, description, schema, mappings, by, xform);
			} else {
				this.feedRepository.addNewFeed(newSourceID, syndicationFormat, link, description, schema, mappings, by, xform);
			}
			
			response.sendRedirect(link);
		}
	}

	private String normalizeXML(String xml) {
		if(xml.startsWith("<?xml")){
			int len = xml.indexOf("<", 2);
			if(len!=-1){
				return xml.substring(len, xml.length());
			}
		}
		return xml;
	}

	private String getBy(HttpServletRequest request) {
		String by = request.getParameter(PARAM_BY);
		if(by == null || by.length() == 0){
			by = NullIdentityProvider.INSTANCE.getAuthenticatedUser();
		}
		return by;
	}

	private String readXML(HttpServletRequest request) throws IOException{
		Reader reader = null;
		try {
			reader = new InputStreamReader(request.getInputStream());
			StringBuffer result = new StringBuffer();
			char[] cb = new char[2048];
			int amtRead = reader.read(cb);
			while (amtRead > 0) {
				result.append(cb, 0, amtRead);
				amtRead = reader.read(cb);
			}
			return result.toString();
		} finally {
			if (reader != null){
				reader.close();
			}
		}		
	}

	private String getSourceID(HttpServletRequest request) {
		
		String servletPath = request.getServletPath();
		String requestUri = request.getRequestURI();
		
		int index = requestUri.indexOf(servletPath);
		if(index + servletPath.length() + 1 <= requestUri.length()){
			String sourceID = requestUri.substring(index + servletPath.length() + 1, requestUri.length());
			if(!sourceID.isEmpty()){
				return sourceID;	
			}
		} 
		return null;
	}

	private Date getSinceDate(HttpServletRequest request){
		String modifiedSince = request.getHeader("If-Modified-Since");
		if(modifiedSince != null && modifiedSince.trim().length() > 0){
			return DateHelper.parseDateYYYYMMDDHHMMSS(modifiedSince, TimeZone.getTimeZone("GMT"));
		} else {
			return null;
		}
	}
	
	private String getFilterQuery(HttpServletRequest request) {
		String filterQuery = request.getParameter(PARAM_FILTER);
		return filterQuery;
	}
}
