package org.openmrs.module.ehrinventoryapp.fragment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.json.JSONArray;
import org.openmrs.Role;
import org.openmrs.api.context.Context;
import org.openmrs.module.ehrinventory.InventoryService;
import org.openmrs.module.ehrinventory.model.*;
import org.openmrs.module.ehrinventoryapp.StoreSingleton;
import org.openmrs.module.ehrinventoryapp.model.ItemInformation;
import org.openmrs.module.hospitalcore.model.*;
import org.openmrs.module.hospitalcore.util.ActionValue;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.UiUtils;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AddItemReceiptsToStoreFragmentController {
    private InventoryService inventoryService;

    public AddItemReceiptsToStoreFragmentController() {
        inventoryService = (InventoryService) Context.getService(InventoryService.class);
    }

    public List<SimpleObject> searchItemNames(@RequestParam(value = "q") String searchTerm, UiUtils uiUtils)
    {
        List<InventoryItem> items = inventoryService.findItem(null,searchTerm);
        return SimpleObject.fromCollection(items, uiUtils, "id", "name", "category.id");
    }

    public List<SimpleObject> fetchItemNames(@RequestParam(value = "categoryId") int categoryId, UiUtils uiUtils) {
        List<SimpleObject> itemNames = null;
        if (categoryId > 0) {
            List<InventoryItem> items = inventoryService.findItem(categoryId, null);
            itemNames = SimpleObject.fromCollection(items, uiUtils, "id", "name");
        }
        return itemNames;
    }

    public List<SimpleObject> getSpecificationByItemName(@RequestParam(value = "itemName") String itemName, UiUtils ui) {

        InventoryService inventoryService = Context.getService(InventoryService.class);
        InventoryItem item = inventoryService.getItemByName(itemName);

        List<SimpleObject> specificationsList = null;

        if (item != null) {
            List<InventoryItemSpecification> specifications = new ArrayList<InventoryItemSpecification>(item.getSpecifications());
            specificationsList = SimpleObject.fromCollection(specifications, ui, "id", "name");
        }

        return specificationsList;
    }

    public List<ItemInformation> getPrescriptions(String json) {
        ObjectMapper mapper = new ObjectMapper();
        List<ItemInformation> list = null;
        try {
            list = mapper.readValue(json,
                    TypeFactory.defaultInstance().constructCollectionType(List.class,
                            ItemInformation.class));

        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void saveReceipt(@RequestParam(value = "itemOrder", required = false) String itemOrder,
                            @RequestParam(value = "description", required = false) String description
    ) throws ParseException {

        JSONArray itemArray = new JSONArray(itemOrder);
        List<ItemInformation> itemInformationList = getPrescriptions(itemOrder);

        int userId = Context.getAuthenticatedUser().getId();
        String fowardParam = "reipt_"+userId;

        List<InventoryStoreItemTransactionDetail> list = (List<InventoryStoreItemTransactionDetail> ) StoreSingleton.getInstance().getHash().get(fowardParam);
        if(list == null){
            list = new ArrayList<InventoryStoreItemTransactionDetail>();
        }

        for (int i = 0; i < itemArray.length(); i++) {
            ItemInformation itemInformation = itemInformationList.get(i);

            int itemSpecificationId = itemInformation.getItemSpecificationId();
            int itemId = itemInformation.getItemId();
            int quantity = itemInformation.getQuantity();

            String itemName = itemInformation.getItemName();
            String unitPriceStr = itemInformation.getUnitPrice();
            String costToPatientStr = itemInformation.getCostToThePatient();
            String companyName = itemInformation.getCompanyName();
            String batchNo = itemInformation.getBatchNo();
            String receiptFrom = itemInformation.getReceiptFrom();
            String dateManufacture = itemInformation.getDateOfManufacture();
            String receiptDate = itemInformation.getReceiptDate();
            String institutionalCost = itemInformation.getInstitutionalCost();

            List<String> errors = new ArrayList<String>();
            InventoryItem item = null;
            List<InventoryItemCategory> listCategory = inventoryService.findItemCategory("");
            item = inventoryService.getItemByName(itemName);

            if (item == null) {
                errors.add("ehrnventory.receiptItem.item.required");
            }

            BigDecimal unitPrice  = new BigDecimal(0);
            BigDecimal VAT = new BigDecimal(0);
            BigDecimal costToPatient = NumberUtils.createBigDecimal(costToPatientStr);

            if (null != institutionalCost && "" != institutionalCost) {
                VAT = NumberUtils.createBigDecimal(institutionalCost);
            }
            if (null != unitPriceStr && "" != unitPriceStr) {
                unitPrice = NumberUtils.createBigDecimal(unitPriceStr);
            }
            if(!StringUtils.isBlank(dateManufacture)) {
                DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
                Date dateManufac = dateFormatter.parse(dateManufacture);
            }

            InventoryItemSpecification specification0 = inventoryService.getItemSpecificationById(itemSpecificationId);
            if (specification0 == null) {
                errors.add("ehrinventory.receiptItem.specification.required=");
            }
            //InventoryDrug drug = inventoryService.getDrugById(drugId);

            if (specification0 != null && item != null && !item.getSpecifications().contains(specification0)) {
                errors.add("ehrnventory.receiptItem.specification.notCorrect");
            }

            InventoryStoreItemTransactionDetail transactionDetail = new InventoryStoreItemTransactionDetail();

            transactionDetail.setItem(item);
            transactionDetail.setAttribute(item.getAttributeName());
            //transactionDetail.setReorderPoint(item.getReorderQty());
            transactionDetail.setSpecification(inventoryService.getItemSpecificationById(itemSpecificationId));
            transactionDetail.setVAT(VAT);
           // transactionDetail.setBatchNo(batchNo);
            transactionDetail.setCompanyName(companyName);
            transactionDetail.setCurrentQuantity(quantity);
            transactionDetail.setQuantity(quantity);
            transactionDetail.setUnitPrice(unitPrice);
            transactionDetail.setCostToPatient(costToPatient);
            transactionDetail.setIssueQuantity(0);
            transactionDetail.setCreatedOn(new Date());
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            try {
                transactionDetail.setDateManufacture(formatter.parse(dateManufacture + " 23:59:59"));
                transactionDetail.setReceiptDate(formatter.parse(receiptDate + " 23:59:59"));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            //Sagar Bele : Date - 22-01-2013 Issue Number 660 : [Inventory] Add receipt from field in Table and front end
          //  transactionDetail.setReceiptFrom(receiptFrom);

            /*Money moneyUnitPrice = new Money(unitPrice);
            Money totl = moneyUnitPrice.times(quantity);
            totl = totl.plus(totl.times((double)VAT/100));
            transactionDetail.setTotalPrice(totl.getAmount());*/

            BigDecimal moneyUnitPrice = costToPatient.multiply(new BigDecimal(quantity));
            //moneyUnitPrice = moneyUnitPrice.add(moneyUnitPrice.multiply(VAT.divide(new BigDecimal(100))));
            transactionDetail.setTotalPrice(moneyUnitPrice);

            list.add(transactionDetail);
            StoreSingleton.getInstance().getHash().put(fowardParam, list);
        }

        saveMoreReceiptInfo(description, list, fowardParam);
    }

    public void saveMoreReceiptInfo(String description, List<InventoryStoreItemTransactionDetail> list,String fowardParam) {

        InventoryService inventoryService = (InventoryService) Context.getService(InventoryService.class);
        Date date = new Date();
        int userId = Context.getAuthenticatedUser().getId();
        //	InventoryStore store = inventoryService.getStoreByCollectionRole(new ArrayList<Role>(Context.getAuthenticatedUser().getAllRoles()));;
        List <Role>role=new ArrayList<Role>(Context.getAuthenticatedUser().getAllRoles());

        InventoryStoreRoleRelation srl=null;
        Role rl = null;
        for(Role r: role){
            if(inventoryService.getStoreRoleByName(r.toString())!=null){
                srl = inventoryService.getStoreRoleByName(r.toString());
                rl=r;
            }
        }
        InventoryStore store =null;
        if(srl!=null){
            store = inventoryService.getStoreById(srl.getStoreid());

        }
        InventoryStoreItemTransaction transaction = new InventoryStoreItemTransaction();
        transaction.setDescription(description);
        transaction.setCreatedOn(date);
        transaction.setStore(store);
        transaction.setTypeTransaction(ActionValue.TRANSACTION[0]);
        transaction.setCreatedBy(Context.getAuthenticatedUser().getGivenName());
        transaction = inventoryService.saveStoreItemTransaction(transaction);

        if(list != null && list.size() > 0){
            for(int i=0;i< list.size();i++){
                InventoryStoreItemTransactionDetail transactionDetail = list.get(i);
                //save total first
                //System.out.println("transactionDetail.getDrug(): "+transactionDetail.getDrug());
                //System.out.println("transactionDetail.getFormulation(): "+transactionDetail.getFormulation());
                InventoryStoreItem storeItem = inventoryService.getStoreItem(store.getId(), transactionDetail.getItem().getId(), transactionDetail.getSpecification().getId());
                if(storeItem == null){
                    storeItem = new InventoryStoreItem();
                    storeItem.setCurrentQuantity(transactionDetail.getQuantity());
                    storeItem.setReceiptQuantity(transactionDetail.getQuantity());
                    storeItem.setItem(transactionDetail.getItem());
                    storeItem.setSpecification(transactionDetail.getSpecification());
                    storeItem.setStore(store);
                    storeItem.setStatusIndent(0);
                    storeItem.setReorderQty(0);
                    storeItem.setOpeningBalance(0);
                    storeItem.setClosingBalance(transactionDetail.getQuantity());
                    storeItem.setStatus(0);
                    storeItem.setReorderQty(transactionDetail.getItem().getReorderQty());
                    storeItem = inventoryService.saveStoreItem(storeItem);

                }else{
                    storeItem.setOpeningBalance(storeItem.getClosingBalance());
                    storeItem.setClosingBalance(storeItem.getClosingBalance()+transactionDetail.getQuantity());
                    storeItem.setCurrentQuantity(storeItem.getCurrentQuantity() + transactionDetail.getQuantity());
                    storeItem.setReceiptQuantity(transactionDetail.getQuantity());
                    storeItem.setReorderQty(transactionDetail.getItem().getReorderQty());
                    storeItem = inventoryService.saveStoreItem(storeItem);
                }
                //save transactionDetail
                transactionDetail.setOpeningBalance(storeItem.getOpeningBalance());
                transactionDetail.setClosingBalance(storeItem.getClosingBalance());
                transactionDetail.setTransaction(transaction);
                inventoryService.saveStoreItemTransactionDetail(transactionDetail);
            }
            StoreSingleton.getInstance().getHash().remove(fowardParam);

        }
    }
}
