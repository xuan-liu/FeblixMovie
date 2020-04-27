
function handleCartResult(resultData) {
    console.log("handleStarResult: populating cart table from resultData");

    // Populate the star table
    // Find the empty table body by id "star_table_body"
    let cartTableBodyElement = jQuery("#cart_table_body");

    // Iterate through resultData, no more than 10 entries
    const jsonObj = resultData[0];
    let sale = 0;
    for (let [key, value] of Object.entries(jsonObj)) {
        sale += value;
        // Concatenate the html tags with resultData jsonObject
        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML += "<th>" + key + "</th>";

        // update movie quantity
        let arg = "\"update\", \"" + key + "\"";
        rowHTML += "<th>";
        // rowHTML += "<form ACTION='#' id='" + key + "' METHOD='post'>";
        rowHTML += "<input id='" + key + "' type='text' value=" + value + " class='input-small'>";
        rowHTML += "<button name ='update' type='submit' onclick='handleButton(" + arg + ")'>Update</button>";
        // rowHTML += "</form>"

        // delete movie item
        arg = "\"delete\", \"" + key + "\"";
        rowHTML += "<button name='delete' onclick='handleButton(" + arg + ")'>Delete</button>";
        rowHTML += "</th>";

        // generate a single price for each movie
        rowHTML += "<th>$4.00</th></tr>";

        // Append the row created to the table body, which will refresh the page
        cartTableBodyElement.append(rowHTML);
    }
    sale *= 4;
    // add the sale to the empty body by id "total_price"
    $("#total_price").text("Total price: $" + sale + ".00");
}

/**
 * Handle the data returned by CartServlet
 * @param resultDataString jsonObject
 */
// function handleButtonResult(resultDataString) {
//     let resultDataJson = JSON.parse(resultDataString);
//
//     console.log("handle button response");
//     console.log(resultDataJson);
//     console.log(resultDataJson["status"]);
//
//     // If add succeeds, it will redirect the user to index.html
//     if (resultDataJson["status"] === "success") {
//         $("#update_cart_message").text(resultDataJson["alert"]);
//     } else {
//         // If login fails, the web page will display
//         // error messages on <div> with id "login_error_message"
//         console.log("show error message");
//         console.log(resultDataJson["alert"]);
//         $("#update_cart_message").text(resultDataJson["alert"]);
//     }
// }

/**
 * Once the update and delete button is clicked, following scripts will be executed to pass the button message
 * and movieTitle to backend
 */
function handleButton(button, movieTitle){
    console.log(button);
    console.log(movieTitle);
    let quantity = document.getElementById(movieTitle).value;
    console.log(quantity);

    $.ajax("api/cart", {
        method: "POST",
        data: {message: button, id: 'null', movie: movieTitle, quantity: quantity},
        // success: handleButtonResult
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
