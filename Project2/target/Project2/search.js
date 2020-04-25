

function handleCategoryResult(resultData) {
    console.log("handleCategoryResult: populating star table from resultData");

    let BodyElement = jQuery("#genreContainerBody");

    // Iterate through resultData, no more than 10 entries
    let bodyHTML = "";
    // console.log("passs2");
    for (let x = 0; x < resultData.length; x++) {
        let genre = resultData[x]['genre'];
        bodyHTML += "<a href='result.html?genre=" + genre + "&limit=10&offset=0&order=titleasc'>" + genre + "</a>";
        console.log(bodyHTML);
    }

    BodyElement.append(bodyHTML);
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