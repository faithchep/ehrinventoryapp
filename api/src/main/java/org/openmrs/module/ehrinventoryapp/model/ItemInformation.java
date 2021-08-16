package org.openmrs.module.ehrinventoryapp.model;

public class ItemInformation {
    private int itemCategoryId;
    private int itemId;
    private int itemSpecificationId;
    private int quantity;
    private int rowId;
    private String unitPrice;
    private String institutionalCost;
    private String costToThePatient;
    private String batchNo;
    private String companyName;
    private String receiptFrom;
    private String dateOfManufacture;
    private String receiptDate;
    private String itemCategoryName;
    private String itemName;
    private String itemSpecificationName;
    private String VAT;

    public int getItemCategoryId() {
        return itemCategoryId;
    }

    public void setItemCategoryId(int itemCategoryId) {
        this.itemCategoryId = itemCategoryId;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public int getItemSpecificationId() {
        return itemSpecificationId;
    }

    public void setItemSpecificationId(int itemSpecificationId) {
        this.itemSpecificationId = itemSpecificationId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getRowId() {
        return rowId;
    }

    public void setRowId(int rowId) {
        this.rowId = rowId;
    }

    public String getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(String unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getInstitutionalCost() {
        return institutionalCost;
    }

    public void setInstitutionalCost(String institutionalCost) {
        this.institutionalCost = institutionalCost;
    }

    public String getCostToThePatient() {
        return costToThePatient;
    }

    public void setCostToThePatient(String costToThePatient) {
        this.costToThePatient = costToThePatient;
    }

    public String getBatchNo() {
        return batchNo;
    }

    public void setBatchNo(String batchNo) {
        this.batchNo = batchNo;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getReceiptFrom() {
        return receiptFrom;
    }

    public void setReceiptFrom(String receiptFrom) {
        this.receiptFrom = receiptFrom;
    }

    public String getDateOfManufacture() {
        return dateOfManufacture;
    }

    public void setDateOfManufacture(String dateOfManufacture) {
        this.dateOfManufacture = dateOfManufacture;
    }

    public String getReceiptDate() {
        return receiptDate;
    }

    public void setReceiptDate(String receiptDate) {
        this.receiptDate = receiptDate;
    }

    public String getItemCategoryName() {
        return itemCategoryName;
    }

    public void setItemCategoryName(String itemCategoryName) {
        this.itemCategoryName = itemCategoryName;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemSpecificationName() {
        return itemSpecificationName;
    }

    public void setItemSpecificationName(String itemSpecificationName) {
        this.itemSpecificationName = itemSpecificationName;
    }

    public String getVAT() {
        return VAT;
    }

    public void setVAT(String VAT) {
        this.VAT = VAT;
    }
}
