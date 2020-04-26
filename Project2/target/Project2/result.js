var url = window.location;


function handleMovieResult(resultData) {
    console.log("handleMovieResult: populating star table from resultData");

    //locate and process the sortMenuElement
    let SortMenuElement = jQuery("#sortMenu");
    let SortHTML = "";
    let tempUrl = new URL(url.toString());
    let tempSearchParams = tempUrl.searchParams;

    tempSearchParams.set('order',"titleasc"); //set the order parameter to ascending title
    tempUrl.search = tempSearchParams.toString();
    SortHTML += "<option value='"+ tempUrl.toString() +"'>Title: Ascending order</option>";

    tempSearchParams.set('order',"titledesc");
    tempUrl.search = tempSearchParams.toString(); //set the order parameter to descending title
    SortHTML += "<option value='"+ tempUrl.toString() +"'>Title: Descending order</option>";

    tempSearchParams.set('order',"ratingasc");
    tempUrl.search = tempSearchParams.toString(); //set the order parameter to ascending rating
    SortHTML += "<option value='"+ tempUrl.toString() +"'>Rating: Ascending order</option>";

    tempSearchParams.set('order',"ratingdesc");
    tempUrl.search = tempSearchParams.toString(); //set the order parameter to descending rating
    SortHTML += "<option value='"+ tempUrl.toString() + "'>Rating: Descending order</option>";

    SortMenuElement.append(SortHTML);


    let numDisplayElement = jQuery("#numberToDisplayMenu");
    let numDisplayString = "";
    tempUrl = new URL(url.toString());
    tempSearchParams = tempUrl.searchParams;

    tempSearchParams.set('limit',"10"); //set the limit parameter to 10
    tempUrl.search = tempSearchParams.toString();
    numDisplayString += "<option value='"+ tempUrl.toString() +"'>10 results per page</option>";

    tempSearchParams.set('limit',"25");
    tempUrl.search = tempSearchParams.toString(); //set the limit parameter to 25
    numDisplayString += "<option value='"+ tempUrl.toString() +"'>25 results per page</option>";

    tempSearchParams.set('limit',"50");
    tempUrl.search = tempSearchParams.toString(); ///set the limit parameter to 50
    numDisplayString += "<option value='"+ tempUrl.toString() +"'>50 results per page</option>";

    tempSearchParams.set('limit',"100");
    tempUrl.search = tempSearchParams.toString(); //set the limit parameter to 100
    numDisplayString += "<option value='"+ tempUrl.toString() + "'>100 results per page</option>";

    numDisplayElement.append(numDisplayString);



    let TableBodyElement = jQuery("#movie_table_body");

    // Iterate through resultData, no more than 10 entries
    for (let i = 0; i < resultData.length; i++) {

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
        let arg = "\"" + resultData[i]['movieId'] + "\", \"" + movieTitle + "\"";
        rowHTML += "</th>";
        rowHTML += "<th>" + resultData[i]["rating"] + "</th>";
        rowHTML += "<th><button onclick = 'handleAddToCart(" + arg + ")'>Add to cart</button></th>"
        // rowHTML += "<th>" + '<button onclick = "handleAddToCart('+ "'" + movieTitle+ "'" + ',1)"' + ">Add to cart</button></th>";

        // rowHTML += "<th>" + resultData[i]["star_dob"] + "</th>";
        rowHTML += "</tr>";

        // Append the row created to the table body, which will refresh the page
        TableBodyElement.append(rowHTML);
    }


    //Create the "Prev" and "Next" tabs
    let PagingElement = jQuery("#pagination");
    let PagingString = "";
    let currentUrl = new URL(url.toString()); //make a copy of the current url
    let search_params = currentUrl.searchParams; //URLSearchParams object, easier to adjust the parameters

    let offset = parseInt(search_params.get('offset'));
    let limit = parseInt(search_params.get('limit'));

    if (offset != 0){
        let newOffset = Math.max(0,offset - limit); //prevent the offset from being a negative number
        search_params.set('offset',newOffset.toString());
        currentUrl.search = search_params.toString();
        let prev_url = currentUrl.toString();
        PagingString += "<li><a href='"+prev_url + "'>prev</li>"
    }
    if(resultData.length == limit){ //If len < limit, reaches the end of data
        let newOffset = offset + limit;
        search_params.set('offset',newOffset.toString());
        currentUrl.search = search_params.toString();
        let next_url = currentUrl.toString();
        PagingString += "<li><a href='"+next_url + "'>next</li>"
    }
    console.log(PagingString);
    PagingElement.append(PagingString);
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
        let txt = "<div class=\"alert alert-success\" role=\"alert\">" + resultDataJson["alert"] + "</div>";
        $("#add_to_cart_message").append(txt);
    } else {
        // If add fails, the web page will display error messages
        let txt = "<div class=\"alert alert-danger\" role=\"alert\">" + resultDataJson["alert"] + "</div>";
        $("#add_to_cart_message").append(txt);
    }
}

function handleAddToCart(movieId, movieTitle){
    console.log('Handling AddToCart');
    console.log('movie: ' + movieTitle);
    console.log("id: " + movieId);
    $.ajax("api/cart", {
        method: "POST",
        data: {message:"add", id: movieId, movie: movieTitle, quantity:1},
        success: handleAddToCartResult
    });
    // event.preventDefault();
}

/**
 * Once this .js is loaded, following scripts will be executed by the browser
 */
// Makes the HTTP GET request and registers on success callback function handleMovieResult
console.log(url.toString());
$.ajax("api/jump",{
    method: "POST", // Setting request method

    data: {movielisturl:url.toString()},// Setting request url, which is mapped by MoviesServlet
});

jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "api/result" + url.search, // Setting request url, which is mapped by MoviesServlet
    success: (resultData) => handleMovieResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
});