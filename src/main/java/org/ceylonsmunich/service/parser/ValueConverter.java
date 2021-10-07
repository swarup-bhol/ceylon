package org.ceylonsmunich.service.parser;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.ceylonsmunich.service.config.table.FeColumn;

public interface ValueConverter {
    Object convert(Cell cell, FeColumn col, Row row);
}
