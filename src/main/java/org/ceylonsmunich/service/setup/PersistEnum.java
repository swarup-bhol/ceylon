package org.ceylonsmunich.service.setup;

import org.ceylonsmunich.service.entity.Constant;
import org.ceylonsmunich.service.entity.repos.ConstantRepository;
import org.ceylonsmunich.service.entity.EnumRef;
import org.ceylonsmunich.service.entity.repos.EnumRefRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Order(2)
public class PersistEnum implements CommandLineRunner {

    @Autowired
    private ConstantRepository constantRepository;

    @Autowired
    private EnumRefRepository enumRefRepository;

    @Value("${application.database.enumerations.restore:#{false}}")
    private boolean restore;

    @Override
    public void run(String... args) throws Exception {

        if(restore) {
            constantRepository.deleteAll();
            enumRefRepository.deleteAll();

            addEnum("YesNo", "Yes", "No");
            addEnum("Gender", "Male", "Female");
            addEnum("Collection Type", "Lot", "Batch", "Set", "Pair");
            addEnum("Cut", "Asscher Cut", "Baguette Cut", "Cushion Cut", "Emerald Cut", "Marquise Cut", "Oval Cut", "Pear", "Princess", "Round", "Trillion");
            addEnum("Relevance", "open", "closed", "1", "2", "3", "4");
            addEnum("Customer Type", "B2B", "B2C", "B2T", "B2O");
            addEnum("POC", "Visit", "Friends", "Recommendation", "Call", "Facebook", "Instagram", "Website", "Fair Show", "Private Sales Event", "Speech Event", "Others", "N/A");
            addEnum("POC Location", "Gemworld", "Inhorgenta", "Hamburg", "Shanghai", "not personal", "N/A");
            addEnum("Focus", "Fair Trade", "Value Invest", "Big stones", "Manual");
            addEnum("Title", "Prof.", "Dr.", "Mr.", "Mrs.", "Dear Team", "Dear Mr. and Mrs.", "Dear Sirs", "Dear Women", "Dear Family");
            addEnum("Sales Category", "Goldsmith", "Jeweller", "Trader ", "Privat", "PR", "Fair show");
        }
    }

    private void addEnum(String name, String... args){
        EnumRef ref = new EnumRef();
        ref.setEnumId(name);
        ref = enumRefRepository.save(ref);

        List<Constant> constants = new ArrayList<>();

        for(String dispName : args){
            Constant constant = new Constant();
            constant.setValue(dispName);
            constant.setEnumRef(ref);
            constants.add(constant);
        }
        constantRepository.saveAll(constants);

    }
}
