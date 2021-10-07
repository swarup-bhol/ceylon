package org.ceylonsmunich.service.config.table;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
public class FeTable {
	@Id
	private String tableId;

	@Column
	private String title;

	@Column
	private Boolean editable = false;

	@Column
	private Boolean filterable = false;

	@Column
	private Boolean exportable = false;

	@Column
	private Boolean addRows = false;

	@Column
	private Boolean search = false;

	@Column
	private Boolean deleteRow = false;

	@Column
	Boolean columnConfigure = false;

	@OneToMany(targetEntity = FeColumn.class, fetch = FetchType.EAGER)
	@OrderBy("col_order ASC")

	private List<FeColumn> columns;
}
