package org.openmrs.module.ehrinventoryapp.page.controller;

import org.openmrs.api.context.Context;
import org.openmrs.module.ehrinventory.InventoryService;
import org.openmrs.module.ehrinventory.model.InventoryItem;
import org.openmrs.module.ehrinventory.model.InventoryItemSpecification;
import org.openmrs.module.ehrinventoryapp.EhrInventoryAppConstants;
import org.openmrs.module.kenyaemr.api.KenyaEmrService;
import org.openmrs.module.kenyaui.annotation.AppPage;
import org.openmrs.ui.framework.page.PageModel;
import org.springframework.web.bind.annotation.RequestParam;


@AppPage(EhrInventoryAppConstants.APP_EHRINVENTORY_APP)
public class ViewCurrentItemStockBalanceDetailPageController {
    
    public void get(@RequestParam(value = "itemId", required = false) Integer itemId,
                    @RequestParam(value = "specificationId", required = false) Integer specificationId,
                    PageModel pageModel){
        InventoryService inventoryService = (InventoryService) Context.getService(InventoryService.class);
        KenyaEmrService kenyaEmrService = Context.getService(KenyaEmrService.class);

        pageModel.addAttribute("specificationId", specificationId);
        pageModel.addAttribute("itemId",itemId);

        InventoryItem item = inventoryService.getItemById(itemId);
        InventoryItemSpecification specification = inventoryService.getItemSpecificationById(specificationId);
        pageModel.addAttribute("specification",specification);
        pageModel.addAttribute("item",item);
        pageModel.addAttribute("userLocation", kenyaEmrService.getDefaultLocation().getName());
    }
}
