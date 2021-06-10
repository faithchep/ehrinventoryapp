package org.openmrs.module.ehrinventoryapp.task;

import org.openmrs.Drug;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.hospitalcore.InventoryCommonService;
import org.openmrs.module.hospitalcore.model.InventoryDrug;
import org.openmrs.scheduler.tasks.AbstractTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class CopyDrugsFromOpenmrsDrugToInventoryDrug extends AbstractTask {

    private static final Logger log = LoggerFactory.getLogger(CopyDrugsFromOpenmrsDrugToInventoryDrug.class);

    @Override
    public void execute() {
        InventoryCommonService inventoryCommonService = Context.getService(InventoryCommonService.class);
        if (!isExecuting) {
            if (log.isDebugEnabled()) {
                log.debug("Copying new drugs to the inventory drugs");
            }

            startExecuting();
            try {
                //do all the work here by looping through the durgs that are availble and compare against what is supplied
                for (Drug drug : Context.getConceptService().getAllDrugs(false)) {
                    //supply a method that will get all the drugs here
                    if(inventoryCommonService.getDrugByName(drug.getName()) == null){
                        System.out.println("The drug is NOT created, we need it created in the inventory drug>>"+drug.getName());
                        //create a new inventory drug object
                        InventoryDrug inventoryDrug = new InventoryDrug();

                    }

                }
            } catch (Exception e) {
                log.error("Error while copying patients to the respective destination ", e);
            } finally {
                stopExecuting();
            }
        }

    }
}
