
function getParameterByName(target) {
    // Get request URL
    let url = window.location.href;
    // Encode target parameter name to url encoding
    target = target.replace(/[\[\]]/g, "\\$&");

    // Ues regular expression to find matched parameter value
    let regex = new RegExp("[?&]" + target + "(=([^&#]*)|&|#|$)"),
        results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return '';

    // Return the decoded parameter value
    return decodeURIComponent(results[2].replace(/\+/g, " "));
}



function handleResult(resultData) {

    console.log("handleResult: populating star info from resultData");

    // populate the star info h3
    // find the empty h3 body by id "star_info"
    let movieInfoElement = jQuery("#movie_info");

    // append two html <p> created to the h3 body, which will refresh the page
    movieInfoElement.append("<p>Movie Title: <b>" + resultData[0]["title"] + "</b></p>" +
        "<p>Movie Year: " + resultData[0]["year"] + "</p>" +
        "<p>Movie Director: " + resultData[0]["director"] + "</p>" +
        "<p>Movie Genres: " + resultData[0]["genres"] + "</p>");

    let starsHTML = "<p>Stars: ";
    for (let x = 0; x < resultData[0]['starInfo'].length; x++) {
        if (x != 0){
            starsHTML += ", ";
        }

        starsHTML += '<a href="single-star.html?id=' + resultData[0]['starInfo'][x]["starId"] + '">'
            + resultData[0]['starInfo'][x]["starName"] +     // display star_name for the link text
            '</a>';
    }
    starsHTML += "</p>";

    movieInfoElement.append(starsHTML + "<p>Movie Rating: " + resultData[0]["rating"] + "</p>");

    console.log("handleResult: populating movie table from resultData");

    // Populate the star table
    // Find the empty table body by id "movie_table_body"

}

/**
 * Once this .js is loaded, following scripts will be executed by the browser\
 */

// Get id from URL
let movieId = getParameterByName('id');

// Makes the HTTP GET request and registers on success callback function handleResult
jQuery.ajax({
    dataType: "json",  // Setting return data type RETURN DATA TYPE FROM THE REQUEST
    method: "GET",// Setting request method
    url: "api/single-movie?id=" + movieId, // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the SingleStarServlet
});