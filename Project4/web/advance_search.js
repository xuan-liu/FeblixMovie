

function handleCategoryResult(resultData) {
    console.log("handleCategoryResult: populating star table from resultData");

    let BodyElement = jQuery("#genreContainerBody");

    // Iterate through resultData, no more than 10 entries
    let bodyHTML = "";
    // console.log("passs2");
    for (let x = 0; x < resultData.length; x++) {
        let genre = resultData[x]['genre'];
        bodyHTML += "<a class='grid-link' href='result.html?genre=" + genre + "&limit=10&offset=0&order=r_desc_t_asc'>" + genre + "</a>";
        console.log(bodyHTML);
    }

    BodyElement.append(bodyHTML);
}

function handleUserType(userTypeData){

    if (userTypeData['usertype'].localeCompare("admin") == 0){
        $("#dashBoardButton").append("<a href='admin/_dashboard.html' class='btn btn-primary'>Back to DashBoard</a>");

    }
}
/**
 * Once this .js is loaded, following scripts will be executed by the browser
 */
// Makes the HTTP GET request and registers on success callback function handleCategoryResult
jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "api/categoryResult", // Setting request url, which is mapped by CategoryResultServlet
    success: (resultData) => handleCategoryResult(resultData) // Setting callback function to handle data returned successfully by the CategoryResultServlet
});

jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "api/usertype", // Setting request url, which is mapped by MoviesServlet
    success: (userTypeData) => handleUserType(userTypeData)// Setting callback function to handle data returned successfully by the StarsServlet
});
