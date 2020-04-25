
function handleMovieResult(resultData) {
    console.log("handleMovieResult: populating star table from resultData");

    // Populate the star table
    // Find the empty table body by id "star_table_body"
    let TableBodyElement = jQuery("#movie_table_body");

    // Iterate through resultData, no more than 10 entries
    for (let i = 0; i < Math.min(20, resultData.length); i++) {

        // Concatenate the html tags with resultData jsonObject
        let rowHTML = "";
        rowHTML += "<tr>";
        let movieTitle = resultData[i]["title"];
        rowHTML += "<th>" + '<a href="single-movie.html?id=' + resultData[i]['movieId'] + '">' +
            movieTitle + '</a>' +"</th>";
        // rowHTML += "<th>" + resultData[i]["title"] + "</th>";
        rowHTML += "<th>" + resultData[i]["year"] + "</th>";
        rowHTML += "<th>" + resultData[i]["director"] + "</th>";
        rowHTML += "<th>" + resultData[i]["genres"] + "</th>";
        rowHTML += "<th>";
        for (let x = 0; x < resultData[i]['starInfo'].length; x++) {
            if (x != 0){
                rowHTML += ", ";
            }
            rowHTML += '<a href="single-star.html?id=' + resultData[i]['starInfo'][x]["starId"] + '">'
                + resultData[i]['starInfo'][x]["starName"] +     // display star_name for the link text
                '</a>';

        }
        rowHTML += "</th>";
        rowHTML += "<th>" + resultData[i]["rating"] + "</th>";

        rowHTML += "<th>" + '<button onclick = "handleAddToCart('+ "'" + movieTitle+ "'" + ',1)"' + ">Add to cart</button></th>";


        // rowHTML += "<th>" + resultData[i]["star_dob"] + "</th>";
        rowHTML += "</tr>";

        // Append the row created to the table body, which will refresh the page
        TableBodyElement.append(rowHTML);
    }
}

/**
 * Handle the data returned by CartServlet
 * @param resultDataString jsonObject
 */
function handleAddToCartResult(resultDataString) {
    console.log("handle add cart response");
    let resultDataJson = JSON.parse(resultDataString);

    console.log("handle add cart response");
    console.log(resultDataJson);
    console.log(resultDataJson["status"]);

    // If add succeeds, it will redirect the user to index.html
    if (resultDataJson["status"] === "success") {
        var txt = "<div class=\"alert alert-success\" role=\"alert\">" + resultDataJson["alert"] + "</div>";
        $("#add_to_cart_message").append(txt);
    } else {
        // If add fails, the web page will display error messages
        var txt = "<div class=\"alert alert-danger\" role=\"alert\">" + resultDataJson["alert"] + "</div>";
        $("#add_to_cart_message").append(txt);
    }
}

function handleAddToCart(movieTitle, quantity){
    console.log('Handling AddToCart');
    console.log('movie: ' + movieTitle);
    console.log('quantity: ' + quantity);

    $.ajax("api/cart", {
        method: "POST",
        data: {message:"add", movie: movieTitle, quantity:quantity},
        success: handleAddToCartResult
    });
    // event.preventDefault();
}

/**
 * Once this .js is loaded, following scripts will be executed by the browser
 */
// Makes the HTTP GET request and registers on success callback function handleMovieResult
jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "api/result" + window.location.search, // Setting request url, which is mapped by MoviesServlet
    success: (resultData) => handleMovieResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
});