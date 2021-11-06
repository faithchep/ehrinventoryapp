package org.openmrs.module.ehrinventoryapp.task;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.Drug;
import org.openmrs.api.context.Context;
import org.openmrs.module.ehrinventory.InventoryService;
import org.openmrs.module.hospitalcore.InventoryCommonService;
import org.openmrs.module.hospitalcore.model.InventoryDrug;
import org.openmrs.module.hospitalcore.model.InventoryDrugCategory;
import org.openmrs.module.hospitalcore.model.InventoryDrugFormulation;
import org.openmrs.module.hospitalcore.model.InventoryDrugUnit;
import org.openmrs.module.kenyaemr.reporting.data.converter.DrugOrdersListForPatientDataEvaluator;
import org.openmrs.scheduler.tasks.AbstractTask;
import org.openmrs.util.OpenmrsClassLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CopyDrugsFromOpenmrsDrugToInventoryDrug extends AbstractTask {

    private static final Logger log = LoggerFactory.getLogger(CopyDrugsFromOpenmrsDrugToInventoryDrug.class);

    @Override
    public void execute() {
        InventoryCommonService inventoryCommonService = Context.getService(InventoryCommonService.class);
        InventoryService inventoryService = Context.getService(InventoryService.class);
        InputStream categoryPath = OpenmrsClassLoader.getInstance().getResourceAsStream("metadata/inventory_drug_category.csv");
        InputStream formulationPath = OpenmrsClassLoader.getInstance().getResourceAsStream("metadata/invetory_drug_formulation.csv");
        InputStream completeDrugPath = OpenmrsClassLoader.getInstance().getResourceAsStream("metadata/inventory_drug.csv");

        if (!isExecuting) {
            if (log.isDebugEnabled()) {
                log.debug("Copying new drugs to the inventory drugs");
            }

            startExecuting();
            try {
                //load drug categories
                setUnit(inventoryService);
                updateDrugCategories(inventoryService, categoryPath);
                updateDrugFormulation(inventoryService, formulationPath);
                //do all the work here by looping through the drugs that are availble and compare against what is supplied and compare with the list given and create them in our inventory
                updateInventoryDrugObjects(inventoryService, completeDrugPath);

            } catch (Exception e) {
                log.error("Error while copying drugs to the inventory resources ", e);
            } finally {
                stopExecuting();
            }
        }

    }
    private void updateDrugCategories(InventoryService inventoryService, InputStream csvFile) {
        String line = "";
        String cvsSplitBy = ",";
        String headLine = "";
        String name = "";
        String description = "";
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(csvFile, "UTF-8"));
            headLine = br.readLine();
            while ((line = br.readLine()) != null) {
                String[] records = line.split(cvsSplitBy);
                name = records[0];
                description = records[1];
                if (StringUtils.isNotEmpty(name) && inventoryService.getDrugCategoryByName(name) == null) {
                    InventoryDrugCategory inventoryDrugCategory = new InventoryDrugCategory();
                    inventoryDrugCategory.setName(name);
                    inventoryDrugCategory.setDescription(description);
                    inventoryDrugCategory.setCreatedOn(new Date());
                    inventoryDrugCategory.setCreatedBy(Context.getAuthenticatedUser().getGivenName());

                    //save the object
                    System.out.println("The categories are >>>"+name);
                    inventoryService.saveDrugCategory(inventoryDrugCategory);
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateDrugFormulation(InventoryService inventoryService, InputStream csvFile) {
        String line = "";
        String cvsSplitBy = ",";
        String headLine = "";
        String name = "";
        String dosage = "";
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(csvFile, "UTF-8"));
            headLine = br.readLine();
            while ((line = br.readLine()) != null) {
                String[] records = line.split(cvsSplitBy);
                dosage = records[0];
                name = records[1];
                if (StringUtils.isNotEmpty(name) && StringUtils.isNotEmpty(dosage)) {
                    if(inventoryService.getDrugFormulation(name, dosage) == null ) {
                        InventoryDrugFormulation inventoryDrugFormulation = new InventoryDrugFormulation();
                        inventoryDrugFormulation.setName(name);
                        inventoryDrugFormulation.setDozage(dosage);
                        inventoryDrugFormulation.setCreatedOn(new Date());
                        inventoryDrugFormulation.setCreatedBy(Context.getAuthenticatedUser().getGivenName());

                        inventoryService.saveDrugFormulation(inventoryDrugFormulation);
                    }
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void setUnit(InventoryService inventoryService){
        if (inventoryService.getDrugUnitByName("EACH") == null) {
            InventoryDrugUnit inventoryDrugUnit = new InventoryDrugUnit();
            inventoryDrugUnit.setName("EACH");
            inventoryDrugUnit.setCreatedBy(Context.getAuthenticatedUser().getGivenName());
            inventoryDrugUnit.setCreatedOn(new Date());
            inventoryDrugUnit.setDescription("Measure unit for a given drug");

            inventoryService.saveDrugUnit(inventoryDrugUnit);
        }
    }
    private void updateInventoryDrugObjects(InventoryService inventoryService, InputStream csvFile) {
        String line = "";
        String line2 = "";
        String cvsSplitBy = ",";
        String headLine = "";
        String headLine2 = "";
        String name = "";
        String concept_id = "";
        String category = "";
        String strength = "";
        String dosage = "";
        InventoryCommonService inventoryCommonService = Context.getService(InventoryCommonService.class);
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(csvFile, "UTF-8"));
            headLine = br.readLine();
            while ((line = br.readLine()) != null) {
                String[] records = line.split(cvsSplitBy);
                name = records[0];
                concept_id = records[1];
                category = records[2];
                strength = records[3];
                dosage = records[4];
                if (StringUtils.isNotEmpty(name) && StringUtils.isNotEmpty(dosage) && StringUtils.isNotEmpty(concept_id) && StringUtils.isNotEmpty(category) && StringUtils.isNotEmpty(strength)) {

                    Drug openMrsDrug = Context.getConceptService().getDrug(name);
                    InventoryDrug inventoryDrug = inventoryCommonService.getDrugByName(name);
                    InventoryDrugUnit inventoryDrugUnit = inventoryService.getDrugUnitById(1);
                    InventoryDrugCategory inventoryDrugCategory = inventoryService.getDrugCategoryByName(category);
                    InventoryDrugFormulation inventoryDrugFormulation = inventoryService.getDrugFormulation(dosage, strength);

                    if(openMrsDrug != null && inventoryDrugUnit != null && inventoryDrugCategory != null) {
                        if(inventoryDrug == null && inventoryDrugFormulation != null) {
                        Set<InventoryDrugFormulation> usedFormulations = new HashSet<InventoryDrugFormulation>();
                        usedFormulations.add(inventoryDrugFormulation);
                        inventoryDrug  = new InventoryDrug();
                        inventoryDrug.setName(name);
                        inventoryDrug.setUnit(inventoryDrugUnit);
                        inventoryDrug.setCategory(inventoryDrugCategory);
                        inventoryDrug.setFormulations(usedFormulations);
                        inventoryDrug.setCreatedOn(new Date());
                        inventoryDrug.setCreatedBy(Context.getAuthenticatedUser().getGivenName());
                        inventoryDrug.setDrugCore(openMrsDrug);
                        inventoryDrug.setAttribute(1);
                        inventoryDrug.setReorderQty(100);
                        inventoryDrug.setVoided(0);

                        inventoryService.saveDrug(inventoryDrug);
                        }
                        else if(inventoryDrug != null && inventoryDrugFormulation != null) {
                            //loop through the available formulations and compare with
                            //check if there is an existing formulation and put them into a list for easier comparison
                            List<InventoryDrugFormulation> inventoryDrugFormulationList = new ArrayList<InventoryDrugFormulation>(inventoryDrug.getFormulations());
                            //Now compare wit the incoming formulation if is different from what exits, if yes, update the drug formulation if NOT just exit
                            if(!(inventoryDrugFormulationList.contains(inventoryDrugFormulation))) {
                                inventoryDrugFormulationList.add(inventoryDrugFormulation);
                                Set<InventoryDrugFormulation> usedFormulations = new HashSet<InventoryDrugFormulation>(inventoryDrugFormulationList);
                                inventoryDrug.setFormulations(usedFormulations);
                                inventoryDrug.setCategory(inventoryDrugCategory);
                                inventoryDrug.setVoided(0);
                                inventoryService.saveDrug(inventoryDrug);
                            }
                        }

                    }
                }
            }

        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
