package org.ceylonsmunich.service.controllers;

import org.ceylonsmunich.service.config.table.FeColumn;
import org.ceylonsmunich.service.config.table.FeColumnRepository;
import org.ceylonsmunich.service.config.table.FeTable;
import org.ceylonsmunich.service.config.table.FeTableRepository;
import org.ceylonsmunich.service.entity.Parameters;
import org.ceylonsmunich.service.entity.repos.ConstantRepository;
import org.ceylonsmunich.service.entity.EnumRef;
import org.ceylonsmunich.service.entity.repos.EnumRefRepository;
import org.ceylonsmunich.service.entity.repos.ParameterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.web.bind.annotation.*;


import java.util.*;
import java.util.stream.Collectors;


@RestController
public class ConfigController {

    @Autowired
    EnumRefRepository refRepository;

    @Autowired
    ConstantRepository constantRepository;

    @Autowired
    private FeTableRepository tableRepository;

    @Autowired
    private ParameterRepository parameterRepository;

    @Autowired
    private FeColumnRepository columnRepository;
    


    @PostMapping("/save-customer")
    public FeTable saveRecord(@RequestBody FeTable fe) {
    	fe.getColumns().stream().forEach(col->columnRepository.save(col));
    	return  tableRepository.save(fe);
    }

    
    
    @RequestMapping(value = "/api/enums", method = RequestMethod.GET)
    public List<EnumRef> getEnums(){
        return refRepository.findAll();
    }

    @RequestMapping(value = "/api/enums/update", method = RequestMethod.POST)
    public void saveEnum(@RequestBody EnumRef ref){
        refRepository.save(ref);
        ref.getConstants().forEach(o->o.setEnumRef(ref));
        constantRepository.saveAll(ref.getConstants());

    }

    @RequestMapping(value = "api/configs/{key}",method = RequestMethod.GET)
    public FeTable getTableConfig(@PathVariable("key") String key){
        return tableRepository.findByTableId(key).orElseThrow(()->new ResourceNotFoundException(key+ ".    Not Found"));    //Id(key).orElse(null);
    }

    @RequestMapping(value = "api/configs/update",method = RequestMethod.POST)
    public void updateTableConfig(@RequestBody FeTable table){

        List<FeColumn> feColumns = table.getColumns().stream().map(o->columnRepository.save(o)).collect(Collectors.toList());
        table.setColumns(feColumns);
        tableRepository.save(table);
    }


    @RequestMapping(value = "api/column/save",method = RequestMethod.POST)
    public List<FeColumn> updateTableConfig(@RequestBody List<FeColumn> columns){
        return columns.stream().map(o->columnRepository.save(o)).collect(Collectors.toList());
    }

    @RequestMapping(value = "api/parameters")
    public List<Parameters> getAllParameters(){
        return parameterRepository.findAll();
    }

    @RequestMapping(value = "api/parameters/update",method = RequestMethod.POST)
    public void saveParam(@RequestBody Parameters parameters){
        parameterRepository.save(parameters);
    }




    public static int countPairs(List<Integer> numbers, int k){
        if(k != 0){
            List<Integer> p = numbers.parallelStream().distinct().sorted().collect(Collectors.toList());
            int counter = 0;
            for(int i = 0; i< p.size() - 1; ++i){
                for(int j = i + 1; j < p.size(); ++j){
                    int diff = Math.abs(p.get(i) - p.get(j));
                    if(diff >= k){
                        if(diff == k)
                            counter++;
                        break;
                    }
                }
            }
            return counter;
        }else{
            Set<Integer> i = new HashSet<>();
            List<Integer> p = numbers.parallelStream().filter(o-> !i.add(o)).distinct().collect(Collectors.toList());
            return p.size();
        }
    }
}
