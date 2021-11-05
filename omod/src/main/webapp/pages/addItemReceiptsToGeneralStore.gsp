<%
    //    ui.decorateWith("kenyaemr", "standardEmrPage", [title: "Inventory Dashboard"])
    ui.decorateWith("appui", "standardEmrPage", [title: "Inventory Dashboard"]);

    ui.includeCss("ehrconfigs", "jquery.dataTables.min.css")
    ui.includeCss("ehrconfigs", "onepcssgrid.css")
    ui.includeCss("ehrconfigs", "referenceapplication.css")

    ui.includeCss("ehrinventoryapp", "main.css")
    ui.includeCss("ehrconfigs", "custom.css")

    ui.includeJavascript("kenyaui", "pagebus/simple/pagebus.js")
    ui.includeJavascript("kenyaui", "kenyaui-tabs.js")
    ui.includeJavascript("kenyaui", "kenyaui-legacy.js")
    ui.includeJavascript("ehrconfigs", "moment.js")
    ui.includeJavascript("ehrconfigs", "jquery.dataTables.min.js")
    ui.includeJavascript("ehrconfigs", "jq.browser.select.js")

    ui.includeJavascript("ehrconfigs", "knockout-2.2.1.js")
    ui.includeJavascript("ehrconfigs", "emr.js")
    ui.includeJavascript("ehrconfigs", "jquery.simplemodal.1.4.4.min.js")

%>
<head>
    <script>
        var itemOrder = [];
        var receiptDescription = "";
        var index = 0;
        var count = 0;

        jq(function () {
            jq("#tabs").tabs();

            var getJSON = function (dataToParse) {
                if (typeof dataToParse === "string") {
                    return JSON.parse(dataToParse);
                }
                return dataToParse;
            }

            var addItemDialog = emr.setupConfirmationDialog({
                dialogOpts: {
                    overlayClose: false,
                    close: true
                },
                selector: '#addItemDialog',
                actions: {
                    confirm: function () {
                        if (!page_verified()) {
                            jq().toastmessage('showErrorToast', 'Ensure fields marked in Red are filled properly');
                            return false;
                        }

                        if (!compare_DOE_DOM_DOR(jq("#dateOfManufacture-field").val(), jq("#receiptDate-field").val())) {
                            return false;
                        }

                        var tbody = jq('#addItemsTable').children('tbody');
                        var table = tbody.length ? tbody : jq('#addItemsTable');

                        index = itemOrder.length;
                        count = index + 1;

                        var unitPrice = jq("#unitPrice").val();
                        var institutionalCost = jq("#institutionalCost").val();
                        var receiptFrom = jq("#receiptFrom").val();

                        if (unitPrice === '') {
                            unitPrice = 0;
                        }

                        if (institutionalCost === '') {
                            institutionalCost = 0;
                        }

                        if (receiptFrom === '') {
                            receiptFrom = 'N/A';
                        }

                        table.append('<tr id="' + index + '"><td>' + count + '</td><td>' + jq("#itemSubCategory").val() + '</td><td>'
                            + jq("#itemName").val() + '</td><td>' + jq("#itemSpecification option:selected").text() + '</td><td>'
                            + jq("#quantity").val() + '</td><td>' + unitPrice + '</td><td>' + institutionalCost + '</td><td>'
                            + jq("#costToThePatient").val() + '</td><td>' + jq("#receiptDate-display").val() + '</td><td>' + receiptFrom
                            + '</td><td><a onclick="removerFunction(' + index + ')" class="remover"><i class="icon-remove small" style="color:red"></i></a> ' +
                            '<a onclick="editorFunction(' + index + ')" class="remover" ><i class="icon-edit small" style="color:blue"></i></a></td></tr>');
                        itemOrder.push(
                            {
                                rowId: index,
                                itemCategoryId: jq("#itemSubCategory").children(":selected").attr("id"),
                                itemCategoryName: jq("#itemSubCategory").children(":selected").val(),
                                itemId: 0,
                                itemName: jq("#itemName").val(),
                                itemSpecificationId: jq("#itemSpecification").children(":selected").attr("id"),
                                itemSpecificationName: jq("#itemSpecification").children(":selected").val(),
                                quantity: jq("#quantity").val(),
                                unitPrice: unitPrice,
                                institutionalCost: institutionalCost,
                                costToThePatient: jq("#costToThePatient").val(),
                                dateOfManufacture: jq("#dateOfManufacture-field").val(),
                                receiptDate: jq("#receiptDate-field").val(),
                                receiptFrom: receiptFrom
                            }
                        );

                        addItemDialog.close();
                    },
                    cancel: function () {
                        addItemDialog.close();
                    }
                }

            });

            var addDescriptionDialog = emr.setupConfirmationDialog({
                dialogOpts: {
                    overlayClose: false,
                    close: true
                },
                selector: '#addDescriptionDialog',
                actions: {
                    confirm: function () {
                        receiptDescription = jq("#addReceiptsDescription").val();
                        itemOrder = JSON.stringify(itemOrder);

                        var additemsData = {
                            'itemOrder': itemOrder,
                            'description': receiptDescription
                        };

                        jq.getJSON('${ ui.actionLink("ehrinventoryapp", "AddItemReceiptsToStore", "saveReceipt") }', additemsData)
                            .success(function (data) {
                                jq().toastmessage('showSuccessToast', 'Receipt Saved Successfully');
                                var receiptsLink = ui.pageLink("ehrinventoryapp", "itemStock");
                                window.location = receiptsLink.substring(0, receiptsLink.length - 1) + "#receipts";
                            })
                            .error(function (xhr, status, err) {
                                jq().toastmessage('showErrorToast', 'Some Error Occured');
                            });

                        addItemDialog.close();
                    },
                    cancel: function () {
                        addItemDialog.close();
                    }
                }

            });

            function page_verified() {
                var error = 0;

                if (jq("#itemSubCategory").children(":selected").attr("id") === 0) {
                    jq("#itemSubCategory").addClass('red');
                    error++;
                } else {
                    jq("#itemSubCategory").removeClass('red');
                }

                if (jq("#itemName").children(":selected").attr("id") === 0) {
                    jq("#itemName").addClass('red');
                    error++;
                } else {
                    jq("#itemName").removeClass('red');
                }

                if (jq("#itemSpecification").children(":selected").attr("id") === 0) {
                    jq("#itemSpecification").addClass('red');
                    error++;
                } else {
                    jq("#itemSpecification").removeClass('red');
                }

                if (jq("#quantity").val() === "") {
                    jq("#quantity").addClass('red');
                    error++;
                } else {
                    jq("#quantity").removeClass('red');
                }

                if (jq("#costToThePatient").val() === "") {
                    jq("#costToThePatient").addClass('red');
                    error++;
                } else {
                    jq("#costToThePatient").removeClass('red');
                }


                if (jq("#companyName").val().trim() === '') {
                    jq("#companyName").addClass('red');
                    error++;
                } else {
                    jq("#companyName").removeClass('red');
                }
                if (jq("#dateOfManufacture-display").val() === "") {
                    jq("#dateOfManufacture-display").addClass('red');
                    error++;
                } else {
                    jq("#dateOfManufacture-display").removeClass('red');
                }
                if (jq("#receiptDate-display").val() === "") {
                    jq("#receiptDate-display").addClass('red');
                    error++;
                } else {
                    jq("#receiptDate-display").removeClass('red');
                }


                if (error === 0) {
                    return true;
                } else {
                    return false;
                }
            }

            function resets() {
                jq('form')[0].reset();
                jq('#itemSubCategory').change();
            }


            jq("#addItemsButton").on("click", function (e) {
                resets();
                addItemDialog.show();
            });

            jq("#itemSubCategory").on("change", function (e) {
                var categoryId = jq(this).children(":selected").attr("id");
                var itemNameData = "<option id='0' name='0'>Select item</option>";
                var itemSpecificationData = "<option id='0'>Select Specification</option>";

                jq('#itemName').empty();
                jq('#itemSpecification').empty();

                jq.getJSON('${ ui.actionLink("ehrinventoryapp", "AddItemReceiptsToStore", "fetchItemNames") }', {
                    categoryId: categoryId
                }).success(function (data) {
                    for (var key in data) {
                        if (data.hasOwnProperty(key)) {
                            var val = data[key];
                            for (var i in val) {
                                if (val.hasOwnProperty(i)) {
                                    var j = val[i];
                                    if (i == "id") {
                                        itemNameData = itemNameData + '<option id="' + j + '"';
                                    } else {
                                        itemNameData = itemNameData + 'name="' + j + '">' + j + '</option>';
                                    }
                                }
                            }
                        }
                    }
                    jq(itemNameData).appendTo("#itemName");
                    jq(itemSpecificationData).appendTo("#itemSpecification");
                }).error(function (xhr, status, err) {
                    alert('AJAX error ' + err);
                });
            });

            //add item autocomplete
            jq("#itemName").on("focus.autocomplete", function () {
                var selectedInput = this;
                jq(this).autocomplete({
                    source: function (request, response) {
                        jq.getJSON('${ ui.actionLink("ehrinventoryapp", "AddItemReceiptsToStore", "searchItemNames") }',
                            {
                                q: request.term
                            }
                        ).success(function (data) {
                            var results = [];
                            for (var i in data) {
                                var result = {label: data[i].name, value: data[i]};
                                results.push(result);
                            }
                            response(results);
                            console.log("The results are>> " + results);
                        });
                    },
                    minLength: 3,
                    select: function (event, ui) {
                        event.preventDefault();
                        jq(this).val(ui.item.label);
                    },
                    change: function (event, ui) {
                        event.preventDefault();
                        jq(this).val(ui.item.label);
                        var categoryId = ui.item.value.subCategory.id;
                        jq("#itemSubCategory option[id=" + categoryId + "]").attr('selected', 'selected');
                        jq.getJSON('${ ui.actionLink("ehrinventoryapp", "AddItemReceiptsToStore", "getSpecificationByItemName") }',
                            {
                                "itemName": ui.item.label
                            }
                        ).success(function (data) {
                            var specifications = jq.map(data, function (specification) {
                                jq('#itemSpecification').append(jq('<option>').text(specification.name ).attr('id', specification.id));
                            });
                        });
                    },
                    open: function () {
                        jq(this).removeClass("ui-corner-all").addClass("ui-corner-top");
                    },
                    close: function () {
                        jq(this).removeClass("ui-corner-top").addClass("ui-corner-all");
                    }
                });
            });

            jq("#addItemsSubmitButton").click(function (event) {
                addDescriptionDialog.show();
            });


            jq('input[type="text"]').keydown(function (e) {
                var numberFields = ['quantity', 'unitPrice', 'institutionalCost', 'costToThePatient'];

                if (jq.inArray(String(jq(this).attr('id')), numberFields) >= 0) {
                    // Allow: backspace, delete, tab, escape, enter and .
                    if (jq.inArray(e.keyCode, [46, 8, 9, 27, 13, 110, 190]) !== -1 ||
                        // Allow: Ctrl+A, Command+A
                        (e.keyCode == 65 && (e.ctrlKey === true || e.metaKey === true)) ||
                        // Allow: home, end, left, right, down, up
                        (e.keyCode >= 35 && e.keyCode <= 40)) {
                        // let it happen, don't do anything
                        return;
                    }
                    // Ensure that it is a number and stop the keypress
                    if ((e.shiftKey || (e.keyCode < 48 || e.keyCode > 57)) && (e.keyCode < 96 || e.keyCode > 105)) {
                        e.preventDefault();
                    }
                }
            });


        });

        function removerFunction(rowId) {
            if (confirm("Are you sure about this?")) {
                jq('#addItemsTable > tbody > tr').remove();
                var tbody = jq('#addItemsTable > tbody');
                var table = tbody.length ? tbody : jq('#addItemsTable');
                itemOrder = jq.grep(itemOrder, function (item, index) {
                    return (rowId !== index);
                });

                // jq('#' + rowId).remove();


                jq.each(itemOrder, function (rowId, item) {

                    console.log(item);
                        tbody.append('<tr id="' + (rowId + 1) + '"><td>' + (rowId + 1) + '</td><td>' + item.itemCategory + '</td><td>' + item.itemName +
                        '</td><td>' + item.itemSpecificationName + '</td><td>' + item.quantity +
                        '</td><td>' + item.unitPrice + '</td><td>' + item.institutionalCost +
                        '</td><td>' + item.costToThePatient + '</td><td>' + item.receiptDate + '</td><td>' + item.receiptFrom +
                        '</td><td><a onclick="removerFunction(' + rowId + ')" class="remover"><i class="icon-remove small" style="color:red"></i></a> ' +
                        '<a class="remover" ><i class="icon-edit small" style="color:blue"></i></a></td></tr>');
                });
            } else {
                return false;
            }

        }

        function printSlip() {
            var printDiv = jq("#addItemsTablePrintable").html();
            var printWindow = window.open('', '', 'height=400,width=800');
            printWindow.document.write('<html><head><title>RECEIPT INFORMATION</title>');
            printWindow.document.write(printDiv);
            printWindow.document.write('</body></html>');
            printWindow.document.close();
            printWindow.print();
        }

        function clearSlip() {
            if (confirm("Are you sure about this?")) {
                jq('#addItemsTable > tbody > tr').remove();
            } else {
                return false;
            }
        }

        function editorFunction(rowId) {
            //editor logic
        }

        function stringToDateConvert(stringDate) {
            var parts = stringDate.split('-');
            return Date.parse(new Date(parts[0], parts[1] - 1, parts[2]));
        }

        function compare_DOE_DOM_DOR(dateOfManufactureString, dateOfReceiptString) {
            console.log("Method has been called");
            console.log("Date of Manufacture");
            console.log(dateOfManufactureString);
            // console.log(dateOfManufactureString);
            console.log("Date of Receipt");
            console.log(dateOfReceiptString);
            dateOfManufacture = stringToDateConvert(dateOfManufactureString);
            dateOfReceipt = stringToDateConvert(dateOfReceiptString);

           if (dateOfManufacture > dateOfReceipt) {
                jq().toastmessage('showErrorToast', 'Please review date of manufacture is greater than receipt date');
                return false;
            } else {
                return true;
            }

        }
    </script>

    <style>
    body {
        margin-top: 20px;
    }

    .col1, .col2, .col3, .col4, .col5, .col6, .col7, .col8, .col9, .col10, .col11, .col12 {
        color: #555;
        text-align: left;
    }

    form input,
    form select {
        margin: 0px;
        display: inline-block;
        min-width: 50px;
        padding: 2px 10px;
        height: 32px !important;
    }

    .info-header span {
        cursor: pointer;
        display: inline-block;
        float: right;
        margin-top: -2px;
        padding-right: 5px;
    }

    .dashboard .info-section {
        margin: 2px 5px 5px;
    }

    .toast-item {
        background-color: #222;
    }

    @media all and (max-width: 768px) {
        .onerow {
            margin: 0 0 100px;
        }
    }

    form .advanced {
        background: #363463 none repeat scroll 0 0;
        border-color: #dddddd;
        border-style: solid;
        border-width: 1px;
        color: #fff;
        cursor: pointer;
        float: right;
        padding: 6px 0;
        text-align: center;
        width: 27%;
    }

    form .advanced i {
        font-size: 22px;
    }

    .col4 label {
        width: 110px;
        display: inline-block;
    }

    .col4 input[type=text] {
        display: inline-block;
        padding: 4px 10px;
    }

    .col4 select {
        padding: 4px 10px;
    }

    form select {
        min-width: 50px;
        display: inline-block;
    }

    .identifiers span {
        border-radius: 50px;
        color: white;
        display: inline;
        font-size: 0.8em;
        letter-spacing: 1px;
        margin: 5px;
    }

    table.dataTable thead th, table.dataTable thead td {
        padding: 5px 10px;
    }

    input[type="text"], select {
        border: 1px solid #aaa;
        border-radius: 2px !important;
        box-shadow: none !important;
        box-sizing: border-box !important;
        height: 32px;
        width: 250px;
        padding: 5px 0 5px 10px;
    }

    .newdtp {
        width: 166px;
    }

    #lastDayOfVisit label,
    #referred-date label {
        display: none;
    }

    #lastDayOfVisit input {
        width: 160px;
    }

    .add-on {
        color: #f26522;
        float: right;
        left: auto;
        margin-left: -29px;
        margin-top: 2px;
        position: absolute;
        right: 25px;
    }

    .chrome .add-on {
        margin-left: -31px;
        margin-right: -15px;
        margin-top: 7px !important;
        position: relative !important;
    }

    #lastDayOfVisit-wrapper .add-on {
        margin-top: 5px;
    }

    .ui-widget-content a {
        color: #007fff;
    }

    #breadcrumbs a, #breadcrumbs a:link, #breadcrumbs a:visited {
        text-decoration: none;
    }

    .new-patient-header .identifiers {
        margin-top: 5px;
    }

    .name {
        color: #f26522;
    }

    #inline-tabs {
        background: #f9f9f9 none repeat scroll 0 0;
    }

    .formfactor {
        background: #f3f3f3 none repeat scroll 0 0;
        border: 1px solid #ddd;
        margin-bottom: 5px;
        padding: 5px 10px;
        text-align: left;
        width: auto;
    }

    .formfactor .lone-col {
        display: inline-block;
        margin-top: 5px;
        overflow: hidden;
        width: 100%;
    }

    .formfactor .first-col {
        display: inline-block;
        margin-top: 5px;
        overflow: hidden;
        width: 300px;
    }

    .formfactor .second-col {
        display: inline-block;
        float: right;
        margin-top: 5px;
        overflow: hidden;
        width: 600px;
    }

    .formfactor .lone-col input,
    .formfactor .first-col input,
    .formfactor .second-col input {
        margin-top: 5px;
        padding: 5px 15px;
        width: 100%;
    }

    .formfactor .lone-col label,
    .formfactor .first-col label,
    .formfactor .second-col label {
        padding-left: 5px;
        color: #363463;
        cursor: pointer;
    }

    .ui-tabs-panel h2 {
        display: inline-block;
    }

    #addItemsTable {
        font-size: 9px;
        margin-top: 40px;
    }

    .dialog li label span {
        color: #f00;
        float: right;
        margin-right: 10px;
    }

    .dialog label {
        display: inline-block;
        width: 180px;
    }

    .dialog select {
        display: inline-block;
        margin: 0;
        padding: 2px 0 2px 5px;
        width: 250px;
    }

    .dialog .dialog-content li {
        margin-bottom: 2px;
    }

    .dialog .dialog-content {
        padding: 5px 20px 0;
    }

    .dialog select option {
        font-size: 1.0em;
    }

    .dialog textarea {
        margin-bottom: 10px;
        resize: none;
        width: 95%;
    }

    td a,
    td a:hover {
        text-decoration: none;
    }

    .red {
        border: 1px solid #f00 !important;
    }

    #receiptDate label,
    #dateOfManufacture label {
        display: none;
    }

    th:first-child {
        width: 5px;
    }

    th:nth-child(4) {
        min-width: 75px;
    }

    th:nth-child(5),
    th:nth-child(7) {
        min-width: 30px;
    }

    th:nth-child(6),
    th:nth-child(8),
    th:nth-child(11),
    th:nth-child(12),
    th:nth-child(13),
    th:nth-child(14) {
        min-width: 30px;
    }

    th:nth-child(9) {
        min-width: 40px;
    }

    th:nth-child(10) {
        min-width: 50px;
    }

    th:last-child {
        width: 35px;
    }

    form input:focus, form select:focus, form textarea:focus, form ul.select:focus, .form input:focus, .form select:focus, .form textarea:focus, .form ul.select:focus {
        outline: 2px none #007fff;
        box-shadow: 0 0 2px 0 #888 !important;
    }

    #footer {
        height: 50px;
        margin-top: 5px;
    }

    #footer img {
        float: left;
        height: 20px;
        margin-right: 5px;
        margin-top: 6px;
    }

    #footer span {
        color: #777;
        float: left;
        font-size: 12px;
        margin-top: 8px;
    }

    #footer .task {
        float: right;
    }

    #modal-overlay {
        background: #000 none repeat scroll 0 0;
        opacity: 0.4 !important;
    }
    </style>
</head>

<body>
<div class="clear"></div>

<div class="container">
    <div class="example">
        <ul id="breadcrumbs">
            <li>
                <a href="${ui.pageLink('kenyaemr', 'userHome')}">
                    <i class="icon-home small"></i></a>
            </li>

            <li>
                <a href="${ui.pageLink('ehrinventoryapp', 'itemStock')}">
                    <i class="icon-chevron-right link"></i>Inventory
                </a>
            </li>

            <li>
                <i class="icon-chevron-right link"></i>
                Receipts
            </li>
        </ul>
    </div>

    <div class="patient-header new-patient-header">
        <div class="demographics">
            <h1 class="name" style="border-bottom: 1px solid #ddd;">
                <span>ADD RECEIPTS TO STORE &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;</span>
            </h1>

        </div>

        <span class="button confirm right" id="addItemsButton">
            <i class="icon-plus-sign small"></i>
            Add to Slip
        </span>

        <div id="addItemsTablePrintable">
            <table id="addItemsTable">
                <thead>
                <tr role="row">
                    <th title="Record ID">#</th>
                    <th title="Item Category">SUB-CATEGORY</th>
                    <th title="Item Name">NAME</th>
                    <th title="Specification">SPECIFICATION</th>
                    <th title="Quantity">QNTY</th>
                    <th title="Unit Price">PRICE</th>
                    <th title="Institutional Cost(%)">I.COST</th>
                    <th title="Cost To The Patient">COST</th>
                    <th title="Receipt Date">R.D</th>
                    <th title="Receipt From">FROM</th>
                    <th title="Task Actions">&nbsp;</th>
                </tr>
                </thead>

                <tbody>
                </tbody>
            </table>
        </div>

        <div id="footer">
            <img src="../ms/uiframework/resource/ehrinventoryapp/images/tooltip.jpg"/>
            <span>Place the mouse over the Titles to get the meaning in full</span>

            <div class="button task" id="addItemsSubmitButton">
                <i class="icon-save small"></i>
                Finish
            </div>

            <div class="button task" onclick="printSlip()" id="printSlip" style="margin-right:5px;">
                <i class="icon-print small"></i>
                Print
            </div>


            <div class="button cancel" onclick="clearSlip()" id="clearSlip" style="margin-left:20px;">
                Clear Slip
            </div>
        </div>

    </div>
</div>

<div id="addItemDialog" class="dialog" style="display:none; width:480px">
    <div class="dialog-header">
        <i class="icon-folder-open"></i>

        <h3>ADD RECEIPTS</h3>
    </div>

    <form class="dialog-content">
        <ul>
            <li>
                <label for="itemSubCategory">item Category<span>*</span></label>
                <select name="itemSubCategory" id="itemSubCategory">
                    <option id="0">Select Category</option>
                    <% if (listItemSubCategory != null || listItemSubCategory != "") { %>
                    <% listItemSubCategory.each { itemCategory -> %>
                    <option id="${itemCategory.id}">${itemCategory.name}</option>
                    <% } %>
                    <% } %>
                </select>
            </li>
            <li>
                <label for="itemName">Item Name<span>*</span></label>
                <input type="text" name="itemName" id="itemName">
            </li>

            <li>
                <label for="itemSpecification">Specification<span>*</span></label>
                <select name="itemSpecification" id="itemSpecification">
                    <option id="0">Select Specification</option>
                </select>
            </li>

            <li>
                <label for="quantity">Quantity<span>*</span></label>
                <input name="quantity" id="quantity" type="text">
            </li>

            <li>
                <label for="unitPrice">Unit Price</label>
                <input name="unitPrice" id="unitPrice" type="text">
            </li>

            <li>
                <label for="institutionalCost">Institutional Cost(%)</label>
                <input name="institutionalCost" id="institutionalCost" type="text">
            </li>

            <li>
                <label for="costToThePatient">Cost To The Patient<span>*</span></label>
                <input name="costToThePatient" id="costToThePatient" type="text">
            </li>

            <li>
                <label for="companyName">Company Name<span>*</span></label>
                <input name="companyName" id="companyName" type="text">
            </li>


            <li>
                <label for="dateOfManufacture">Date of Manufacture<span>*</span></label>
                ${ui.includeFragment("uicommons", "field/datetimepicker", [formFieldName: 'dateOfManufacture', id: 'dateOfManufacture', label: '', useTime: false, defaultToday: false, class: ['newdtp']])}
            </li>

            <li>
                <label for="receiptDate">Receipt Date<span>*</span></label>
                ${ui.includeFragment("uicommons", "field/datetimepicker", [formFieldName: 'receiptDate', id: 'receiptDate', label: '', useTime: false, defaultToday: false, class: ['newdtp']])}
            </li>

            <li>
                <label for="receiptFrom">Receipt From</label>
                <input name="receiptFrom" id="receiptFrom" type="text">
            </li>

        </ul>

        <span class="button confirm right">Confirm</span>
        <span class="button cancel">Cancel</span>
    </form>
</div>

<div id="addDescriptionDialog" class="dialog" style="display:none; width:480px">
    <div class="dialog-header">
        <i class="icon-folder-open"></i>

        <h3>ADD Description</h3>
    </div>

    <form class="dialog-content">
        <label>RECEIPT DESCRIPTION</label>
        <textarea id="addReceiptsDescription" resize: none; height: 70px; width: 418px; margin-bottom: 10px;></textarea>

        <span class="button cancel">Cancel</span>
        <span class="button confirm right" style="margin-right: 0px">Submit</span>
    </form>
</div>

</body>


