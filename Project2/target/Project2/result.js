
function handleStarResult(resultData) {
    console.log("handleStarResult: populating star table from resultData");

    // Populate the star table
    // Find the empty table body by id "star_table_body"
    let starTableBodyElement = jQuery("#movie_table_body");

    // Iterate through resultData, no more than 10 entries
    for (let i = 0; i < Math.min(20, resultData.length); i++) {

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
        rowHTML += "<th>" + '<a href="single-movie.html?id=' + resultData[i]['movieId'] + '">' +
            resultData[i]["title"] + '</a>' +"</th>";
        // rowHTML += "<th>" + resultData[i]["title"] + "</th>";
        rowHTML += "<th>" + resultData[i]["year"] + "</th>";
        rowHTML += "<th>" + resultData[i]["director"] + "</th>";
        rowHTML += "<th>" + resultData[i]["genres"] + "</th>";
        rowHTML += "<th>";
        for (let x = 0; x < resultData[i]['starInfo'].length; x++) {
            if (x != 0){
                rowHTML += ", ";
            }
            // rowHTML += resultData[i]['starsInfo'][x]["starName"];
            // rowHTML += resultData[i]['starsInfo'][x]["starId"];
            rowHTML += '<a href="single-star.html?id=' + resultData[i]['starInfo'][x]["starId"] + '">'
                + resultData[i]['starInfo'][x]["starName"] +     // display star_name for the link text
                '</a>';

        }
        rowHTML += "</th>";
        rowHTML += "<th>" + resultData[i]["rating"] + "</th>";

        // rowHTML += "<th>" + resultData[i]["star_dob"] + "</th>";
        rowHTML += "</tr>";

        // Append the row created to the table body, which will refresh the page
        starTableBodyElement.append(rowHTML);
    }
}


/**
 * Once this .js is loaded, following scripts will be executed by the browser
 */
// Makes the HTTP GET request and registers on success callback function handleStarResult
jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "api/result" + window.location.search, // Setting request url, which is mapped by MoviesServlet
    success: (resultData) => handleStarResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
});