package org.ceylonsmunich.service.parser;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.monitorjbl.xlsx.StreamingReader;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.ceylonsmunich.service.config.table.FeColumn;
import org.ceylonsmunich.service.entity.Product;
import org.ceylonsmunich.service.entity.repos.ConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

public class ExcelWorkbook {

    public static class DefaultValueConversion implements ValueConverter{
        @Override
        public Object convert(Cell cell, FeColumn col, Row row){
            switch (col.getType()){
                case "int":
                case "currency":
                case "float":
                    return cell.getNumericCellValue();
                case "boolean":
                    return cell.getBooleanCellValue();
                case "date":
                case "string":
                default:
                    return cell.getStringCellValue();
            }
        }
    }

    public static <E> List<E> parse(InputStream stream, List<FeColumn> configs,Class<?> type) throws IOException{
        return ExcelWorkbook.<E>parse(stream,0, configs,type, new DefaultValueConversion(),1);
    }
    public static <E> List<E> parse(InputStream stream, List<FeColumn> configs,Class<?> type, ValueConverter converter) throws IOException{
        return ExcelWorkbook.<E>parse(stream, 0, configs,type,converter, 1);
    }

    public static <E> List<E> parse(InputStream stream, int sheetNo, List<FeColumn> configs,Class<?> type, ValueConverter converter,int skip) throws IOException {

        List<FeColumn> columns = configs.stream().filter(o -> o.getSchemaIndex() >= 0).sorted(Comparator.comparingInt(FeColumn::getSchemaIndex)).collect(Collectors.toList());
        Workbook book = StreamingReader.builder().open(stream);
        Sheet sheet = book.getSheetAt(sheetNo);

        List<Map<String, Object>> data = new ArrayList<>();
        int counter = skip;
        for(Row r : sheet){
                if(counter >0){
                    counter--;
                    continue;
                }
                Map<String, Object> row = new HashMap<>();
                for (FeColumn col : columns) {
                    Cell cell = r.getCell(col.getSchemaIndex());
                    try{
                        row.put(col.getName(), converter.convert(cell,col,r));
                    }catch (Exception e){
                        row.put(col.getName(), null);
                    }
                }
                data.add(row);
        }
        ObjectMapper objectMapper = new ObjectMapper();
        String s = objectMapper.writeValueAsString(data);
        return objectMapper.readValue(s, objectMapper.getTypeFactory().constructParametricType(List.class, type));
    };
}
