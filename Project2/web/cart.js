
function handleCartResult(resultData) {
    console.log("handleStarResult: populating cart table from resultData");

    // Populate the star table
    // Find the empty table body by id "star_table_body"
    let cartTableBodyElement = jQuery("#cart_table_body");

    // Iterate through resultData, no more than 10 entries
    const jsonObj = resultData[0];
    for (let [key, value] of Object.entries(jsonObj)) {
        // Concatenate the html tags with resultData jsonObject
        let rowHTML = "";
        rowHTML += "<tr>";
        // rowHTML +=
        //     "<th>" +
        //     // Add a link to single-star.html with id passed with GET url parameter
        //     '<a href="single-star.html?id=' + resultData[i]['star_id'] + '">'
        //     + resultData[i]["star_name"] +     // display star_name for the link text
        //     '</a>' +
        //     "</th>";
        rowHTML += "<th>" + key + "</th>";
        rowHTML += "<th>" + value + "</th></tr>";


        // Append the row created to the table body, which will refresh the page
        cartTableBodyElement.append(rowHTML);
    }
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