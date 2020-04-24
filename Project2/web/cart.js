
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
        rowHTML += "<th>" + key + "</th>";

        // update movie quantity
        var arg = "\"update\", \"" + key + "\"";
        rowHTML += "<th>";
        rowHTML += "<form ACTION='#' id='updateQuantity' METHOD='post'>";
        rowHTML += "<input id='quantity_update' type='text' value=" + value + " class='input-small'>";
        rowHTML += "<button type='submit' onclick='handleButton(" + arg + ")'>Update</button>";
        rowHTML += "</form>"

        // delete movie item
        arg = "\"delete\", \"" + key + "\"";
        rowHTML += "<button name='delete' onclick='handleButton(" + arg + ")'>Delete</button>";
        rowHTML += "</th>";

        // generate a single price for each movie
        rowHTML += "<th>$4.00</th></tr>";

        // Append the row created to the table body, which will refresh the page
        cartTableBodyElement.append(rowHTML);
    }
}

/**
 * Once the update and delete button is clicked, following scripts will be executed to pass the button message
 * and movieTitle to backend
 */
function handleButton(button, movieTitle){
    console.log(button);
    console.log(movieTitle);
    var quantity = document.getElementById("quantity_update").value;

    $.ajax("api/cart", {
        method: "POST",
        data: {message: button, movie: movieTitle, quantity: quantity},
    });

    window.location.href = "cart.html";
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
