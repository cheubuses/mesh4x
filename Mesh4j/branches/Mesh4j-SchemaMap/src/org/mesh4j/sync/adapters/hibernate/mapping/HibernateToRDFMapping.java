package org.mesh4j.sync.adapters.hibernate.mapping;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.dom4j.Element;
import org.dom4j.tree.DefaultElement;
import org.mesh4j.sync.adapters.feed.ISyndicationFormat;
import org.mesh4j.sync.payload.schema.ISchemaTypeFormat;
import org.mesh4j.sync.payload.schema.SchemaTypeFormat;
import org.mesh4j.sync.payload.schema.rdf.AbstractRDFIdentifiableMapping;
import org.mesh4j.sync.payload.schema.rdf.CompositeProperty;
import org.mesh4j.sync.payload.schema.rdf.IRDFSchema;
import org.mesh4j.sync.payload.schema.rdf.RDFInstance;
import org.mesh4j.sync.utils.XMLHelper;
import org.mesh4j.sync.validations.Guard;

import com.hp.hpl.jena.datatypes.RDFDatatype;
import com.hp.hpl.jena.datatypes.TypeMapper;

public class HibernateToRDFMapping extends AbstractRDFIdentifiableMapping implements IHibernateToXMLMapping {
	
	private static final String DATE_FORMAT = "yyyy-mm-dd hh:mm:ss";
	protected static HashMap<String, ISchemaTypeFormat> FORMATS = new HashMap<String, ISchemaTypeFormat>();
	
	static{
		FORMATS.put(IRDFSchema.XLS_DATETIME, new SchemaTypeFormat(new SimpleDateFormat(DATE_FORMAT)));
	}
	
	// BUSINESS METHODS
	public HibernateToRDFMapping(IRDFSchema rdfSchema){
		super(rdfSchema);
	}
	
	@Override
	public Element convertRowToXML(String meshId, Element element) throws Exception {
		RDFInstance instance = null;
		if(this.rdfSchema.hasCompositeId()){
			instance = this.rdfSchema.createNewInstanceFromPlainXML(meshId, element, FORMATS, new String[]{"id"}); 
		} else {
			instance = this.rdfSchema.createNewInstanceFromPlainXML(meshId, element, FORMATS); 
		}
		return instance.asElementRDFXML();
	}

	@Override
	public Element convertXMLToRow(Element element) throws Exception {
		Element rdfXml;
		if(ISyndicationFormat.ELEMENT_PAYLOAD.equals(element.getName())){
			Element rdfElement = element.element(IRDFSchema.ELEMENT_RDF);
			if(rdfElement == null){
				Guard.throwsArgumentException("payload");
			}
			rdfXml = rdfElement;
		} else {
			rdfXml = element;
		}
		RDFInstance instance = this.rdfSchema.createNewInstanceFromRDFXML(rdfXml);
		
		if(this.rdfSchema.hasCompositeId()){
			CompositeProperty compositeId = new CompositeProperty("id", this.rdfSchema.getIdentifiablePropertyNames());			
			return instance.asElementPlainXml(FORMATS, new CompositeProperty[]{compositeId});
		} else {
			return instance.asElementPlainXml(FORMATS, null);
		}
	}

	@Override
	public String getMeshId(Element entityElement) {
		if(entityElement == null){
			return null;
		}

		Element element = entityElement;
		if(this.rdfSchema.hasCompositeId()){
			element = entityElement.element("id");
		}
		
		List<String> idValues = new ArrayList<String>();
		String idCellValue;
		List<String> idColumnNames = this.rdfSchema.getIdentifiablePropertyNames();
		for (String idColumnName : idColumnNames) {
			Element idElement = element.element(idColumnName);
			if(idElement == null){
				return null;
			}
			idCellValue = idElement.getText();
			if(idCellValue == null){
				return null;
			} else {
				idValues.add(idCellValue);
			}
		}
		return makeId(idValues);	
	}

	@Override
	public Serializable getHibernateId(String meshId) {
		if(this.rdfSchema.hasCompositeId()){
			String[] meshIds = getIds(meshId);
			List<String> propertyNames = this.rdfSchema.getIdentifiablePropertyNames();
			
			StringBuffer sb = new StringBuffer();
			sb.append("<id>");
			for (int i = 0; i < propertyNames.size(); i++) {
				String propertyName = propertyNames.get(i);
				sb.append("<");
				sb.append(propertyName);
				sb.append(">");
				sb.append(meshIds[i]);
				sb.append("</");
				sb.append(propertyName);
				sb.append(">");	
			}						
			sb.append("</id>");
			
			return (DefaultElement)XMLHelper.parseElement(sb.toString());
			
		} else {
			//Issue#125:Sharif:08/09/09
			String idPropertyType = this.rdfSchema.getPropertyType(this.rdfSchema.getIdentifiablePropertyNames().get(0));
			RDFDatatype dataType = TypeMapper.getInstance().getTypeByName(idPropertyType);
			
			if (IRDFSchema.XLS_LONG.equals(idPropertyType) || IRDFSchema.XLS_INTEGER.equals(idPropertyType)) {
				return (Serializable) dataType.parse(meshId);
			} else {
				return meshId;
			}	
			//Issue#125
		}
	}
}
