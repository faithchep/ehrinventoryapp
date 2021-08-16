package org.openmrs.module.ehrinventoryapp.fragment.controller;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.Role;
import org.openmrs.api.context.Context;
import org.openmrs.module.ehrinventory.InventoryService;
import org.openmrs.module.ehrinventory.model.InventoryStoreItemTransactionDetail;
import org.openmrs.module.ehrinventory.util.PagingUtil;
import org.openmrs.module.ehrinventory.util.RequestUtil;
import org.openmrs.module.hospitalcore.model.InventoryStore;
import org.openmrs.module.hospitalcore.model.InventoryStoreRoleRelation;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.UiUtils;
import org.springframework.web.bind.annotation.RequestParam;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ItemStockBalanceFragmentController {
    public void controller() {

    }

    public List<SimpleObject> fetchItemStockBalance(@RequestParam(value = "categoryId", required = false) Integer categoryId,
                                                @RequestParam(value = "itemName", required = false) String itemName,
                                                @RequestParam(value = "currentPage", required = false) Integer currentPage,
                                                HttpServletRequest request,
                                                UiUtils uiUtils) {
    
        List<SimpleObject> itemStockBalanceList = null;
        InventoryService inventoryService = Context.getService(InventoryService.class);
        List<Role> roles = new ArrayList<Role>(Context.getAuthenticatedUser().getAllRoles());

        InventoryStoreRoleRelation inventoryStoreRoleRelation=null;
        for(Role roleUser : roles){
            if(inventoryService.getStoreRoleByName(roleUser.toString())!=null){
                inventoryStoreRoleRelation = inventoryService.getStoreRoleByName(roleUser.toString());
            }
        }
        InventoryStore store =null;
        if(inventoryStoreRoleRelation!=null){
            store = inventoryService.getStoreById(inventoryStoreRoleRelation.getStoreid());
        }
        int total = inventoryService.countStoreItemViewStockBalance(store.getId(), categoryId, itemName, "" ,"", "" );
        String temp = "";

        if(categoryId != null){
            temp = "?categoryId="+categoryId;
        }
        if(itemName != null){
            if(StringUtils.isBlank(temp)){
                temp = "?itemName="+itemName;
            }else{
                temp +="&itemName="+itemName;
            }
        }
        PagingUtil pagingUtil = new PagingUtil( RequestUtil.getCurrentLink(request)+temp ,0,currentPage,total );
        List<InventoryStoreItemTransactionDetail> stockBalances = inventoryService.listStoreItemViewStockBalance(store.getId(), categoryId, itemName,"","","",pagingUtil.getStartPos(),0);

        if (stockBalances!=null) {
            Collections.sort(stockBalances);
            itemStockBalanceList = SimpleObject.fromCollection(stockBalances, uiUtils, "item.id","item.name","item.category.name","item.category.id","specification.id","specification.name","quantity","issueQuantity","currentQuantity","item.reorderQty");

        }
        return itemStockBalanceList;
}
}
