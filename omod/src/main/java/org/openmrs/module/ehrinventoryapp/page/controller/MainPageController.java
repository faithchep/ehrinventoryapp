package org.openmrs.module.ehrinventoryapp.page.controller;

import org.openmrs.Role;
import org.openmrs.api.context.Context;
import org.openmrs.module.ehrinventory.model.InventoryItemCategory;
import org.openmrs.module.ehrinventoryapp.EhrInventoryAppConstants;
import org.openmrs.module.hospitalcore.model.InventoryDrugCategory;
import org.openmrs.module.hospitalcore.model.InventoryStore;
import org.openmrs.module.hospitalcore.model.InventoryStoreRoleRelation;
import org.openmrs.module.hospitalcore.util.Action;
import org.openmrs.module.hospitalcore.util.ActionValue;
import org.openmrs.module.ehrinventory.InventoryService;
import org.openmrs.module.kenyaui.annotation.AppPage;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.page.PageModel;

import java.util.ArrayList;
import java.util.List;


@AppPage(EhrInventoryAppConstants.APP_EHRINVENTORY_APP)
public class MainPageController {
    /**
     * Default handler for get and post requests if none is provided
     */
    public static void controller() {

    }

    public String get(PageModel pageModel, UiUtils uiUtils) {
        InventoryService inventoryService = Context.getService(InventoryService.class);
        List<InventoryDrugCategory> listCategory = inventoryService.listDrugCategory("", 0, 0);
        List<InventoryItemCategory> itemCatagoryList = inventoryService.listItemCategory("",0,0);
        List<Role> role = new ArrayList<Role>(Context.getAuthenticatedUser().getAllRoles());
        List<Action> listMainStoreStatus = ActionValue.getListIndentMainStore();

        InventoryStoreRoleRelation srl = null;
        Role rl = null;
        for (Role r : role) {
            if (inventoryService.getStoreRoleByName(r.toString()) != null) {
                srl = inventoryService.getStoreRoleByName(r.toString());
                rl = r;
            }
        }
        InventoryStore mainStore = null;
        List<InventoryStore> listStore = new ArrayList<InventoryStore>();
        if (srl != null) {
            mainStore = inventoryService.getStoreById(srl.getStoreid());
            listStore = inventoryService.listStoreByMainStore(mainStore.getId(), false);

        }
        /*else{
            return "redirect: index.htm";
        }*/
        pageModel.addAttribute("listMainStoreStatus", listMainStoreStatus);
        pageModel.addAttribute("listCategory", listCategory);
        pageModel.addAttribute("itemCatagoryList",itemCatagoryList);
        pageModel.addAttribute("listStore", listStore);
        return null;
    }


}
