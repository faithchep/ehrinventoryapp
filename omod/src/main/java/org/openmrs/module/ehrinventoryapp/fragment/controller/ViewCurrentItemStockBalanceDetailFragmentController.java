package org.openmrs.module.ehrinventoryapp.fragment.controller;

import org.openmrs.Role;
import org.openmrs.api.context.Context;
import org.openmrs.module.ehrinventory.InventoryService;
import org.openmrs.module.ehrinventory.model.InventoryStoreItemTransactionDetail;
import org.openmrs.module.hospitalcore.model.InventoryStore;
import org.openmrs.module.hospitalcore.model.InventoryStoreRoleRelation;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.UiUtils;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

public class ViewCurrentItemStockBalanceDetailFragmentController
{
    public void controller() {

    }

    public List<SimpleObject> ViewCurrentItemStockBalanceDetail(
            @RequestParam(value = "itemId", required = false) Integer itemId,
            @RequestParam(value = "specificationId", required = false) Integer specificationId,
            UiUtils uiUtils) {
        InventoryService inventoryService = (InventoryService) Context
                .getService(InventoryService.class);
        List<Role> role=new ArrayList<Role>(Context.getAuthenticatedUser().getAllRoles());

        InventoryStoreRoleRelation storeRoleRelation=null;

        for(Role roleUser: role){
            if(inventoryService.getStoreRoleByName(roleUser.toString())!=null){
                storeRoleRelation = inventoryService.getStoreRoleByName(roleUser.toString());
            }
        }
        InventoryStore store =null;
        if(storeRoleRelation!=null){
            store = inventoryService.getStoreById(storeRoleRelation.getStoreid());

        }
        List<InventoryStoreItemTransactionDetail> listViewStockBalance = inventoryService
                .listStoreItemTransactionDetail(store.getId(), itemId,
                        specificationId,0,0);

        return SimpleObject.fromCollection(listViewStockBalance,uiUtils,"item.name","item.category.name","specifications","transaction.typeTransactionName","openingBalance","quantity","issueQuantity","closingBalance","receiptDate");
    }
}
