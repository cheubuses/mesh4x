package org.mesh4j.grameen.training.intro.adapter.googlespreadsheet.model;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.mesh4j.grameen.training.intro.adapter.googlespreadsheet.GoogleSpreadsheetUtils;

import sun.reflect.ReflectionFactory.GetReflectionFactoryAction;

import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.data.BaseEntry;
import com.google.gdata.data.batch.BatchUtils;
import com.google.gdata.data.spreadsheet.CellEntry;
import com.google.gdata.data.spreadsheet.ListEntry;
import com.google.gdata.data.spreadsheet.WorksheetEntry;
import com.google.gdata.util.ServiceException;

public class GSWorksheet<C> extends GSBaseElement<C> {

	// MODEL VARIABLES
	//all moved to base class
	
	// BUSINESS METHODS	
	@Deprecated
	public GSWorksheet(WorksheetEntry worksheet, int sheetIndex) {
		super();
		this.baseEntry = worksheet;
		this.elementListIndex = sheetIndex;
		this.childElements = new LinkedHashMap<String, C>();
	}
	
	public GSWorksheet(WorksheetEntry worksheet, int sheetIndex,
			GSSpreadsheet<?> parentElement) {
		super();
		this.baseEntry = worksheet;
		this.elementListIndex = sheetIndex;
		this.childElements = new LinkedHashMap<String, C>();
		this.parentElement = parentElement;
	}
	
	/**
	 * get the core {@link WorksheetEntry} object wrapped by this
	 * {@link GSWorksheet}
	 * @return
	 */
	public WorksheetEntry getWorksheetEntry() {
		return (WorksheetEntry) getBaseEntry();
	}

	/**
	 * get the parent/container {@link GSSpreadsheet} object of this {@link GSWorksheet}
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public GSSpreadsheet getParentSpreadsheet() {
		return (GSSpreadsheet) getParentElement();
	}
	
	/**
	 * get all the child {@link GSRow} contained in this {@link GSSpreadsheet} 
	 * @return
	 */
	public Map<String, C> getGSRows() {
		return getChildElements();  
	}
	
	/**
	 * get a {@link GSRow} from this {@link GSWorksheet} by row index
	 * @param rowIndex
	 * @return
	 */
	public C getGSRow(int rowIndex) {
		
		for(GSRow gsRow : ((GSWorksheet<GSRow>)this).getChildElements().values()){
			if(gsRow.getRowIndex() == rowIndex){
				return (C) gsRow;
			}			
		}
		return null;
	}	
	
	/**
	 * get a {@link GSRow} by key from this {@link GSWorksheet}
	 * 
	 * @param key
	 * @return
	 */
	public C getGSRow(String key){
		return getChildElement(key);
	}

	/**
	 * get the sheet index of this {@link GSWorksheet} in the container {@link GSSpreadsheet}
	 * @return
	 */
	public int getSheetIndex() {
		return getElementListIndex();
	}	
	
	/**
	 * get a {@link GSCell} from this {@link GSWorksheet} by row and column index 
	 * @param rowIndex
	 * @param colIndex
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public GSCell getGSCell(int rowIndex, int colIndex) {
		for(GSRow gsRow : ((GSWorksheet<GSRow>)this).getChildElements().values()){
			if(gsRow.getRowIndex() == rowIndex){
				return (GSCell) gsRow.getGSCell(colIndex);
			}	
		}
		return null;		
	}	
		
	/**
	 * return the name/title of the core {@link WorksheetEntry} wrapped by this
	 * {@link GSWorksheet}
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String getName() {
		return ((BaseEntry<WorksheetEntry>) this.baseEntry).getTitle()
				.getPlainText();
	}
	
	
	/**
	 * get the cell header tag as an ordered set according to their order in spreadsheet 
	 * @return
	 */
	public List<String> getCellHeaderTagset() {
		List<String> tagList = new ArrayList<String>();
		if (this.getChildElements().size() > 0) {
			if(this.getChildElements().size() > 1){
				ListEntry listEntry = (ListEntry) ((GSWorksheet<GSRow>) this)
						.getGSRow(2).baseEntry;
				for(String tag :listEntry.getCustomElements().getTags()){
					tagList.add(tag);
				}
				
			}else{
				//create a temporary row
				//grab the tags
				//delete the row
			}
		}
		return tagList;
	}
	
	/**
	 * add a new row to the spreadsheet
	 * 
	 * @param rowToAdd
	 */
	public void addNewRow(GSRow rowToAdd) {		
		int newRowIndex = this.getChildElements().size() + 2;
		rowToAdd.elementListIndex= newRowIndex;
		((GSWorksheet<GSRow>) this).addChildElement(
				rowToAdd.getElementId(), rowToAdd);
		this.setDirty();
	}	
	
	/**
	 * generate a new row for the worksheet 
	 * 
	 * @param values: cell value for each cell
	 * @return
	 * @throws IOException
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public GSRow<GSCell> createNewRow(LinkedHashMap<String, String> values) throws IOException, ServiceException {

		int noOfColumns = ((GSWorksheet<GSRow>) this).getGSRow(1)
				.getChildElements().size();

		if (values.size() < noOfColumns) {
			// TODO: throw exception
			return null;
		}
		int newRowIndex = this.getChildElements().size() + 1;
		
		ListEntry newRow = new ListEntry();		
		
		GSRow<GSCell> newGSRow = new GSRow(newRow, newRowIndex, this);
		
		int col = 1; 	// entries in values make sure actual ordering in spreadsheet, 
						// so we can assume they are in a position according to column order 1, 2, 3....   	
		
		for (String key : values.keySet()) {
			CellEntry newCell = new CellEntry(newRowIndex, col, ""); //this is not supported for batch update :(
		    GSCell newGSCell = new GSCell(newCell, newGSRow, key);
			newGSCell.updateCellValue(values.get(key));
			newGSRow.addChildElement(key, newGSCell);
			col++;
		}		
		
		return newGSRow;
	}
	
	
	public GSRow<GSCell> createNewRow(String[] values) throws IOException, ServiceException {

		int noOfColumns = ((GSWorksheet<GSRow>) this).getGSRow(1)
				.getChildElements().size();

		if (values.length < noOfColumns) {
			// TODO: throw exception
			return null;
		}
		int newRowIndex = this.getChildElements().size() + 1;
		
		ListEntry newRow = new ListEntry();		
		
		GSRow<GSCell> newGSRow = new GSRow(newRow, newRowIndex, this);
		
		for (int col = 1; col <= noOfColumns; col++) {
			
		    String batchId = "R" + newRowIndex + "C" + col;
			URL entryUrl = new URL(((WorksheetEntry) this.getBaseEntry())
					.getCellFeedUrl().toString()
					+ "/" + batchId);

			CellEntry newCell = ((WorksheetEntry) this.getBaseEntry())
					.getService().getEntry(entryUrl, CellEntry.class);			
			
		    GSCell newGSCell = new GSCell(newCell, newGSRow, "TODO: need to provide column tag");
			newGSCell.updateCellValue(values[col - 1]);
			newGSRow.addChildElement(Integer.toString(col), newGSCell); //TODO: need to supply header tag here as key instead of colIndex
		}

		return newGSRow;
	}	
		
	@SuppressWarnings("unchecked")
	@Override
	public void refreshMe() throws IOException, ServiceException {
		if(this.isDirty()){
			
			List<ListEntry> rowList = GoogleSpreadsheetUtils
							.getAllRows((WorksheetEntry) this.baseEntry); // 1 http
																			// request
			List<CellEntry> cellList = GoogleSpreadsheetUtils
							.getAllCells((WorksheetEntry) this.baseEntry); // 1 http
																			// request
			
			if( rowList.size() > 0 && cellList.size() > 0 ){
				//get the header row and put it as the 1st row in the rowlist
				this.childElements.clear();
				GSRow<GSCell> gsListHeaderEntry = new GSRow(
						new ListEntry(), 1, this);
				gsListHeaderEntry.populateClildWithHeaderTag(cellList, (WorksheetEntry)this.baseEntry);				
				((GSWorksheet<GSRow>)this).getChildElements().put(gsListHeaderEntry.getElementId(), gsListHeaderEntry);			
				
				for (ListEntry row : rowList){
					//create a custom row object and populate its child
					GSRow<GSCell> gsListEntry = new GSRow(
							row, rowList.indexOf(row) + 2, this); //+2 because #1 position is occupied by list header entry 
					gsListEntry.populateClildWithHeaderTag(cellList, (WorksheetEntry)this.baseEntry);				
					
					//add a row to the custom worksheet object
					((GSWorksheet<GSRow>)this).getChildElements().put(gsListEntry.getElementId(), gsListEntry);
					//TODO: right now index has been used as key; mjrow.getId() could have used, this need to review
				}
			} // if
			
			
			this.dirty = false;
			this.deleteCandidate = false;
		}
	}


}
