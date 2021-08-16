
<script>
    var paramDataString;
    var itemList;
    var toReturn;
    jq(function(){
        var paramData = getItemStockBalance();
        paramDataString = JSON.stringify(paramData);

        function itemStockListView(){
            var self = this;
            // Editable data
            self.itemStockList = ko.observableArray([]);
            var mappedStockItems = jQuery.map(paramData, function (item) {
                return item;
            });

            self.viewDetails = function(item){
                window.location.href = ui.pageLink("ehrinventoryapp", "viewCurrentItemStockBalanceDetail", {
                    itemId: item.item.id,
                    specificationId: item.specification.id
                });
            }
            self.itemStockList(mappedStockItems);
        }

        jq("#stockCategoryId").on("change", function () {
            var categoryId	= jq("#stockCategoryId").val();
            var itemName = jq("#stockItemName").val();
            var itemStockList =  getItemStockBalance(categoryId,itemName);
            itemList.itemStockList(itemStockList);
        });

        jq("#stockItemName").on("change", function () {
            var categoryId	= jq("#stockCategoryId").val();
            var itemName = jq("#stockItemName").val();
            var itName = getItemStockBalance(categoryId,itemName);
            itemList.itemStockList(itName);
        });

        /*jq('#stockItemName').keydown(function (e) {
            var key = e.keyCode || e.which;
            if ((key == 9 || key == 13) && jq(this).attr('id') != 'searchPhrase') {
                var itemName = jq("#stockItemName").val();
                var dName = getItemStockBalance(itemName);
                itemList.itemStockList(dName);
            }
        })*/;

        itemList = new itemStockListView();
        ko.applyBindings(itemList, jq("#itemStocklist")[0]);
    });


    function getItemStockBalance(categoryId,itemName) {
        jQuery.ajax({
            type: "GET",
            url: '${ui.actionLink("ehrinventoryapp", "itemStockBalance", "fetchItemStockBalance")}',
            dataType: "json",
            global: false,
            async: false,
            data: {
                categoryId:categoryId,
                itemName:itemName
            },
            success: function (data) {
                toReturn = data;
            }
        });
        return toReturn;
    }
</script>

<div class="dashboard clear">
    <div class="info-section">
        <div class="info-header">
            <i class="icon-folder-open"></i>
            <h3>VIEW ITEM STOCK</h3>

            <div>
                <i class="icon-filter" style="font-size: 26px!important; color: #5b57a6"></i>

                <label for="stockCategoryId">Category: </label>
                <select id="stockCategoryId" style="width: 200px;" name="stockCategoryId">
                    <option value="">ALL CATEGORIES</option>
                    <% itemCatagoryList.each { %>
                    <option value="${it.id}" title="${it.name}">${it.name}</option>
                    <% } %>
                </select>

                <label for="stockItemName">&nbsp; &nbsp;Name:</label>
                <input id="stockItemName" type="text" value="" name="stockItemName" placeholder=" Item Name">

            </div>


        </div>
    </div>
</div>

<table id="itemStocklist">
    <thead>
    <th>#</th>
    <th>ITEM NAME</th>
    <th>CATEGORY</th>
    <th>SPECIFICATION</th>
    <th>QUANTITY</th>
    <th>RE-ORDER</th>
    </thead>

    <tbody data-bind="foreach: itemStockList">
    <tr class="" data-bind = "css: { 'below-qnty' : (item.reorderQty > currentQuantity) }">
        <td data-bind="text: \$index() + 1"></td>
        <td>
            <a data-bind="html: item.name,click:\$parent.viewDetails"></a>
        </td>

        <td data-bind="text: item.category.name"></td>
        <td>
            <span data-bind="text: specification.name"></span>
        </td>

        <td data-bind="text: currentQuantity"></td>
        <td data-bind="text: item.reorderQty"></td>
    </tr>
    </tbody>
</table>
