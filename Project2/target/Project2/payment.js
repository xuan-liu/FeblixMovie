let payment_form = $("#payment_form");

/**
 * Handle the data returned by CartServlet
 * @param resultDataString jsonObject
 */
function handleSaleData(resultData) {
    console.log("handleSaleData");

    // Iterate through resultData, sum all the sales
    const jsonObj = resultData[0];
    var sale = 0;
    for (let [key, value] of Object.entries(jsonObj)) {
        console.log(key);
        console.log(value);
        sale += value;
        console.log(sale);
    }
    sale *= 4;

    // add the sale to the empty body by id "total_price"
    $("#total_price").text("Total price of your shopping cart: $" + sale + ".00");
}

/**
 * Handle the data returned by PaymentServlet
 * @param resultDataString jsonObject
 */
function handlePaymentResult(resultDataString) {
    let resultDataJson = JSON.parse(resultDataString);

    console.log("handle payment response");
    console.log(resultDataJson);
    console.log(resultDataJson["status"]);

    // If payment succeeds, it will redirect the user to confirmation
    if (resultDataJson["status"] === "success") {
        window.location.replace("confirmation.html");
    } else {
        // If payment fails, the web page will display
        // error messages on <div> with id "payment_error_message"
        console.log("show error message");
        var txt = "<div class=\"alert alert-danger\" role=\"alert\">" + resultDataJson["message"] + "</div>";
        $("#payment_error_message").append(txt);
    }
}

/**
 * Submit the form content with POST method
 * @param formSubmitEvent
 */
function submitPaymentForm(formSubmitEvent) {
    console.log("submit payment form");
    /**
     * When users click the submit button, the browser will not direct
     * users to the url defined in HTML form. Instead, it will call this
     * event handler when the event is triggered.
     */
    formSubmitEvent.preventDefault();

    $.ajax(
        "api/payment", {
            method: "POST",
            // Serialize the login form to the data sent by POST request
            data: payment_form.serialize(),
            success: handlePaymentResult
        }
    );
}

// Makes the HTTP GET request and registers on success callback function handleSaleData
jQuery.ajax({
    dataType: "json",
    method: "GET",
    url: "api/cart",
    success: (resultData) => handleSaleData(resultData)
});

// Bind the submit action of the form to a handler function
payment_form.submit(submitPaymentForm);