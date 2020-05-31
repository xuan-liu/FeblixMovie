var url = window.location;
var admin = false;


function handleMovieResult(resultData) {
    console.log("handleMovieResult: populating star table from resultData");

    //locate and process the sortMenuElement
    let SortMenuElement = jQuery("#sortMenu");
    let SortHTML = "";
    let tempUrl = new URL(url.toString());
    let tempSearchParams = tempUrl.searchParams;
    let currentOrder = tempSearchParams.get("order");
    console.log('current order: ' + currentOrder);
    let currentLimit = tempSearchParams.get("limit");


    tempSearchParams.set('order',"t_asc_r_asc"); //set the order parameter to Ascending title then Ascending rating
    tempUrl.search = tempSearchParams.toString();
    SortHTML += "<option value='"+ tempUrl.toString() +"'";

    if (currentOrder.localeCompare("t_asc_r_asc") == 0){
        SortHTML += "selected='selected'";
    }
    SortHTML += ">" + "Title: Ascending, Rating: Ascending order</option>";

    tempSearchParams.set('order',"t_asc_r_desc");
    tempUrl.search = tempSearchParams.toString();
    SortHTML += "<option value='"+ tempUrl.toString() +"'";
    if (currentOrder.localeCompare("t_asc_r_desc") == 0){
        SortHTML += "selected='selected'";
    }
    SortHTML += ">" + "Title: Ascending order, Rating: Descending order</option>";

    tempSearchParams.set('order',"t_desc_r_asc");
    tempUrl.search = tempSearchParams.toString();
    SortHTML += "<option value='"+ tempUrl.toString() +"'";
    if (currentOrder.localeCompare("t_desc_r_asc") == 0){
        SortHTML += "selected='selected'";
    }
    SortHTML += ">" + "Title: Descending order, Rating: Ascending order</option>";

    tempSearchParams.set('order',"t_desc_r_desc");
    tempUrl.search = tempSearchParams.toString();
    SortHTML += "<option value='"+ tempUrl.toString() +"'";
    if (currentOrder.localeCompare("t_desc_r_desc") == 0){
        SortHTML += "selected='selected'";
    }
    SortHTML += ">" + "Title: Descending order, Rating: Descending order</option>";


    tempSearchParams.set('order',"r_asc_t_asc");
    tempUrl.search = tempSearchParams.toString(); //set the order parameter to Ascending rating then Ascending title
    SortHTML += "<option value='"+ tempUrl.toString() +"'";
    if (currentOrder.localeCompare("r_asc_t_asc") == 0){
        SortHTML += "selected='selected'";
    }
    SortHTML += ">" + "Rating: Ascending order, Title: Ascending order</option>";

    tempSearchParams.set('order',"r_asc_t_desc");
    tempUrl.search = tempSearchParams.toString();
    SortHTML += "<option value='"+ tempUrl.toString() +"'";
    if (currentOrder.localeCompare("r_asc_t_desc") == 0){
        SortHTML += "selected='selected'";
    }
    SortHTML += ">" + "Rating: Ascending order, Title: Descending order</option>";

    tempSearchParams.set('order',"r_desc_t_asc");
    tempUrl.search = tempSearchParams.toString();
    SortHTML += "<option value='"+ tempUrl.toString() +"'";
    if (currentOrder.localeCompare("r_desc_t_asc") == 0){
        SortHTML += "selected='selected'";
    }
    SortHTML += ">" + "Rating: Descending order, Title: Ascending order</option>";

    tempSearchParams.set('order',"r_desc_t_desc");
    tempUrl.search = tempSearchParams.toString();
    SortHTML += "<option value='"+ tempUrl.toString() +"'";
    if (currentOrder.localeCompare("r_desc_t_desc") == 0){
        SortHTML += "selected='selected'";
    }
    SortHTML += ">" + "Rating: Descending order, Title: Descending order</option>";


    SortMenuElement.append(SortHTML);


    let numDisplayElement = jQuery("#numberToDisplayMenu");
    let numDisplayString = "";
    tempUrl = new URL(url.toString());
    tempSearchParams = tempUrl.searchParams;

    tempSearchParams.set('limit',"10"); //set the limit parameter to 10
    tempUrl.search = tempSearchParams.toString();
    numDisplayString += "<option value='"+ tempUrl.toString() +"'";
    if (currentLimit.localeCompare("10") == 0){
        numDisplayString += "selected='selected'";
    }
    numDisplayString += ">10 results per page</option>";

    tempSearchParams.set('limit',"25");
    tempUrl.search = tempSearchParams.toString(); //set the limit parameter to 25
    numDisplayString += "<option value='"+ tempUrl.toString() +"'";
    if (currentLimit.localeCompare("25") == 0){
        numDisplayString += "selected='selected'";
    }
    numDisplayString += ">25 results per page</option>";

    tempSearchParams.set('limit',"50");
    tempUrl.search = tempSearchParams.toString(); ///set the limit parameter to 50
    numDisplayString += "<option value='"+ tempUrl.toString() +"'";
    if (currentLimit.localeCompare("50") == 0){
        numDisplayString += "selected='selected'";
    }
    numDisplayString += ">50 results per page</option>";

    tempSearchParams.set('limit',"100");
    tempUrl.search = tempSearchParams.toString(); //set the limit parameter to 100
    numDisplayString += "<option value='"+ tempUrl.toString() +"'";
    if (currentLimit.localeCompare("100") == 0){
        numDisplayString += "selected='selected'";
    }
    numDisplayString += ">100 results per page</option>";

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
        rowHTML += "<th>";
        let genres = resultData[i]['genres'];
        for (let x = 0; x < genres.length; x++) {
            if (x != 0){
                rowHTML += ", ";
            }
            rowHTML += "<a href='result.html?genre=" + genres[x] + "&limit=10&offset=0&order=r_desc_t_asc'>" + genres[x] + "</a>";
        }
        rowHTML += "</th>";


        //Process the stars data and hyperlink them
        rowHTML += "<th>";
        let stars = resultData[i]['starInfo'];
        for (let x = 0; x < stars.length; x++) {
            if (x != 0){
                rowHTML += ", ";
            }
            rowHTML += '<a href="single-star.html?id=' + stars[x]["starId"] + '">'
                + stars[x]["starName"] +     // display star_name for the link text
                '</a>';

        }
        rowHTML += "</th>";
        let arg = "\"" + resultData[i]['movieId'] + "\", \"" + movieTitle + "\"";
        rowHTML += "</th>";
        rowHTML += "<th>" + resultData[i]["rating"] + "</th>";
        console.log("admin :" + admin);
        if (admin){
            console.log("TRUE");
            rowHTML += "<th>N/A</th>>";
        }else {
            console.log("FALSE");
            rowHTML += "<th><button class=\"btn btn-default\" onclick = 'handleAddToCart(" + arg + ")'>Add to cart</button></th>"
        }


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

function handleUserType(userTypeData){

    if (userTypeData['usertype'].localeCompare("admin") == 0){
        admin = true;
        $("#dashBoardButton").append("<a href='admin/_dashboard.html' class='btn btn-primary'>Back to DashBoard</a>");

    }
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
    url: "api/usertype", // Setting request url, which is mapped by MoviesServlet
    success: (userTypeData) => handleUserType(userTypeData)// Setting callback function to handle data returned successfully by the StarsServlet
});



jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "api/result" + url.search, // Setting request url, which is mapped by MoviesServlet
    success: (resultData) => handleMovieResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
});

//Cache movieListPage url to session
sessionStorage.setItem("movieListURL", window.location)