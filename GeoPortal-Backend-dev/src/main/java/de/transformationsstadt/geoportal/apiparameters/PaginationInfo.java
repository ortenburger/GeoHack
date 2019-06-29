package de.transformationsstadt.geoportal.apiparameters;

import java.sql.Date;


/**
 * Informationen über die Pagination (Seitenweises anzeigen von Daten) als Parameter für alle Zugriffe, bei denen Pagination sinnvoll (oder erwünscht) ist.
 * 
 * @author Sebastian Bruch < s.bruch@utopiastadt.eu >
 *
 */
public class PaginationInfo {
	Long page=0L; // seite
	Long itemsPerPage=10L; // einheiten pro seite
	Date after; // anfangsdatum des ausschnittes
	Date before; // enddatum des ausschnittes
	String filter; // filterung (z.B. für ein Stringmatching oder so)
	public PaginationInfo() {

	}

	public Long getPage() {
		return page;
	}
	
	public void setPage(Long page) {
		if(page != null) {
			this.page = page;
		}
	}
	
	public Long getItemsPerPage() {
		return itemsPerPage;
	}
	
	public void setItemsPerPage(Long itemsPerPage) {
		if(itemsPerPage != null) {
			this.itemsPerPage = itemsPerPage;
		}
	}
	
	public Date getAfter() {
		return after;
	}
	
	public void setAfter(Date after) {
		if(after != null) {
			this.after = after;
		}
	}
	
	public Date getBefore() {
		return before;
	}
	
	public void setBefore(Date before) {
		if(before != null) {
			this.before = before;
		}
	}
	
	public String getFilter() {
		return filter;
	}
	
	public void setFilter(String filter) {
		if(filter != null) {
			this.filter = filter;
		}
	}
}
