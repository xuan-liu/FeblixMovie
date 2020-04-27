
function handleCartResult(resultData) {
    console.log("handleSaleResult: populating cart table from resultData");

    // Populate the star table
    // Find the empty table body by id "star_table_body"
    let saleTableBodyElement = jQuery("#sale_table_body");

    // Iterate through resultData, no more than 10 entries
    const jsonObj = resultData[0];
    let sale = 0;
    for (let [key, value] of Object.entries(jsonObj)) {
        sale += value;
        // Concatenate the html tags with resultData jsonObject
        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML += "<th>" + key + "</th>";
        rowHTML += "<th>" + value + "</th>";
        rowHTML += "<th>$4.00</th></tr>";

        // Append the row created to the table body, which will refresh the page
        saleTableBodyElement.append(rowHTML);
    }
    sale *= 4;

    // add the sale to the empty body by id "total_price"
    $("#total_sale").text("Total price: $" + sale + ".00");
}


/**
 * Once this .js is loaded, following scripts will be executed by the browser
 */
// Makes the HTTP GET request and registers on success callback function handleStarResult
jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "api/cart", // Setting request url, which is mapped by MoviesServlet
    success: (resultData) => handleCartResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
});
