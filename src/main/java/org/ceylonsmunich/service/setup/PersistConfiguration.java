package org.ceylonsmunich.service.setup;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import org.ceylonsmunich.service.config.table.*;
import org.ceylonsmunich.service.config.table.actions.*;
import org.ceylonsmunich.service.entity.repos.ConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.annotation.Order;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Component;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import javax.persistence.Column;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Order(3)
public class PersistConfiguration implements CommandLineRunner {

    @Autowired
    ConfigRepository repository;


    @Autowired
    private FeColumnRepository columnRepository;

    @Autowired
    private FeTableRepository tableRepository;

    @Value("${application.database.configuration.restore:#{false}}")
    private boolean config;

    @Override
    public void run(String... args) throws Exception {

        if(config){
            generateTableConfigs();

            //overrides
            FeTable table = tableRepository.findById("consignment.json").orElse(null);
            if(table != null){
                for(FeColumn col: table.getColumns()){
                    if(col.getName().equals("customer")){
                        col.setName("customer_summary");
                        columnRepository.save(col);
                    }
                    if(col.getName().equals("items")){
                        col.setName("items_count");
                        columnRepository.save(col);
                    }
                }
            }
        }
    }


    private void generateTableConfigs(){

        tableRepository.deleteAll();
        columnRepository.deleteAll();

        ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false);
        provider.addIncludeFilter(new AnnotationTypeFilter(DisplayAsTable.class));

        List<FeTable> tables = new ArrayList<>();
        List<FeColumn> columns = new ArrayList<>();
        List<FeColumn> schemaColumns = new ArrayList<>();

        for(BeanDefinition def: provider.findCandidateComponents("org.ceylonsmunich.service.entity")){
            try {
                Class<?> aClass = Class.forName(def.getBeanClassName());
                DisplayAsTable annotation = aClass.getAnnotation(DisplayAsTable.class);
                FeTable table = new FeTable();
                table.setTableId(annotation.value());
                table.setTitle(annotation.title());
                this.setOperations(aClass,table);
                System.out.println("adding " + table.getTableId() + " as "+table.getTitle());
                List<FeColumn> cols = new ArrayList<>();
                generateColumns(aClass,cols,table,"");
                int i = 0;
                for(FeColumn col: cols){
                    col.setOrder(i);
                    col.setSchemaIndex(i);
                    ++i;
                }
                table.setColumns(cols.stream().map(o->columnRepository.save(o)).collect(Collectors.toList()));
                tableRepository.save(table);

            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private void setOperations(Class<?> aClass,FeTable table){
        table.setEditable(aClass.isAnnotationPresent(FeEditable.class));
        table.setAddRows(aClass.isAnnotationPresent(FeAdd.class));
        table.setColumnConfigure(aClass.isAnnotationPresent(FeColumnConfigure.class));
        table.setDeleteRow(aClass.isAnnotationPresent(FeDelete.class));
        table.setExportable(aClass.isAnnotationPresent(FeExport.class));
        table.setFilterable(aClass.isAnnotationPresent(FeFilter.class));
        table.setSearch(aClass.isAnnotationPresent(FeSearch.class));
    }


    private void generateColumns(Class<?> c, List<FeColumn> columns, FeTable table, String prefix){
        for(Field f: c.getDeclaredFields()){
            if(!f.isAnnotationPresent(JsonIgnore.class) &&  !f.isAnnotationPresent(DisplayIgnore.class)){
                Class<?> type = f.getType();
                if(f.isAnnotationPresent(JsonUnwrapped.class)){
                    JsonUnwrapped annotation = f.getAnnotation(JsonUnwrapped.class);
                    generateColumns(type,columns,table,annotation.prefix());
                }else{
                    FeColumn column = this.mapToColumn(f,prefix);
                    if(column.getType().equals("string")){
                        column.setWidth(Math.max(320,column.getDisplayName().length() * 17));
                    }else
                        column.setWidth(column.getDisplayName().length() * 17);
                    columns.add(column);
                }
            }
        }
    }

    private void generateSchemaColumns(Class<?> c, List<FeColumn> columns, FeTable table, String prefix){
        for(Field f: c.getDeclaredFields()){

            if(!f.isAnnotationPresent(GeneratedValue.class)){
                Class<?> type = f.getType();
                if(f.isAnnotationPresent(JsonUnwrapped.class)){
                    JsonUnwrapped annotation = f.getAnnotation(JsonUnwrapped.class);
                    generateSchemaColumns(type,columns,table,annotation.prefix());
                }else if(f.isAnnotationPresent(Column.class)){
                    FeColumn column = this.mapToColumn(f,prefix);
                    columns.add(column);
                }
            }
        }
    }

    private FeColumn mapToColumn(Field f,String prefix){
        FeColumn column = new FeColumn();
        Class<?> type = f.getType();
        column.setEditable(f.isAnnotationPresent(Column.class));
        if(f.isAnnotationPresent(JsonProperty.class)){
            JsonProperty annotation = f.getAnnotation(JsonProperty.class);
            if(annotation.value().isEmpty()){
                column.setName(prefix + f.getName());
            }else
                column.setName(prefix + annotation.value());
        }
        else{
            column.setName(prefix + f.getName());
        }
        if(f.isAnnotationPresent(DisplayAsColumn.class)){
            DisplayAsColumn annotation = f.getAnnotation(DisplayAsColumn.class);
            column.setDisplayName(annotation.name());
            column.setType(annotation.type());
            column.setEnumName(annotation.enumName());
            column.setVisible(annotation.visible());
            column.setEditable(annotation.editable().equals("always"));
            column.setSortable(annotation.sortable());
            if(annotation.type().equals("number")){
                column.setAlign("right");
            }
        }
        else {
            String[] split = column.getName().substring(prefix.length()).split("(?=\\p{Lu})");
            String join = String.join(" ", split);
            column.setDisplayName(prefix + " "+join.substring(0,1).toUpperCase() + join.substring(1));
            column.setVisible(true);
            column.setEnumName("");
            try {
                Class<? extends Number> aClass = type.asSubclass(Number.class);
                column.setType("int");
                column.setAlign("right");
            }catch(ClassCastException e){
                if(type.equals(Boolean.class)) {
                    column.setType("boolean");
                }else if(type.equals(Date.class)){
                    column.setType("date");
                }else{
                    column.setType("string");
                }
            }
        }
        this.formattingData(f,column);
        return column;
    }


    private void formattingData(Field f,FeColumn column){
        if(f.isAnnotationPresent(DisplayNumberFormat.class)){
            DisplayNumberFormat format = f.getAnnotation(DisplayNumberFormat.class);
            column.setThousandSeparator(format.thousandSeparators());
            if(format.precision() > 0){
                column.setPrecision(format.precision());
            }
        }
        if(f.isAnnotationPresent(DisplayCurrency.class)){
            DisplayCurrency currency = f.getAnnotation(DisplayCurrency.class);
            column.setCurrencyId(currency.id());
            column.setDerivedCurrency(currency.derived());
            column.setType("currency");
        }

        if(f.isAnnotationPresent(DisplayPrefix.class)){
            DisplayPrefix prefix = f.getAnnotation(DisplayPrefix.class);
            column.setPrefix(prefix.value());
        }
        if(f.isAnnotationPresent(DisplaySuffix.class)){
            DisplaySuffix suffix = f.getAnnotation(DisplaySuffix.class);
            column.setSuffix(suffix.value());
        }
    }


}
