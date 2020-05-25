
function handleLookup(query, doneCallback) {
    console.log("Autocomplete Initiated")

    // TODO: if you want to check past query results first, you can do it here

    // sending the HTTP GET request to the Java Servlet endpoint hero-suggestion
    // with the query data

    let trimmed_query = query.split(/ +/).join(" ");

    let cached_data = sessionStorage.getItem(trimmed_query);

    if (cached_data == null){
        console.log("Query Result not in Cache");
        console.log("Sending AJAX request to backend Java Servlet")
        jQuery.ajax({
            "method": "GET",
            // generate the request url from the query.
            // escape the query string to avoid errors caused by special characters
            "url": "movie-suggestion?query=" + escape(query),
            "success": function(data) {
                // pass the data, query, and doneCallback function into the success handler
                CacheData(trimmed_query, data);
                handleLookupAjaxSuccess(data, query, doneCallback)
            },
            "error": function(errorData) {
                console.log("lookup ajax error")
                console.log(errorData)
            }
        })
    }
    else{
        console.log("Found Query Result of '" + trimmed_query + "' in Cache");
        console.log("Using Cached Data to Display")
        handleLookupAjaxSuccess(cached_data, query, doneCallback);
    }



}


/*
 * This function is used to handle the ajax success callback function.
 * It is called by our own code upon the success of the AJAX request
 *
 * data is the JSON data string you get from your Java Servlet
 *
 */
function handleLookupAjaxSuccess(data, query, doneCallback) {
    console.log("Parsing Data")

    // parse the string into JSON
    var jsonData = JSON.parse(data);
    console.log("Suggestion List Data: ")
    console.log(jsonData)

    // TODO: if you want to cache the result into a global variable you can do it here

    // call the callback function provided by the autocomplete library
    // add "{suggestions: jsonData}" to satisfy the library response format according to
    //   the "Response Format" section in documentation
    doneCallback( { suggestions: jsonData } );
}


/*
 * This function is the select suggestion handler function.
 * When a suggestion is selected, this function is called by the library.
 *
 * You can redirect to the page you want using the suggestion data.
 */
function handleSelectSuggestion(suggestion) {
    // TODO: jump to the specific result page based on the selected suggestion

    console.log("you select " + suggestion["value"] + " with ID " + suggestion["data"]["movieId"])

    let url = "single-movie.html?id=" + suggestion["data"]["movieId"];

    window.location.replace(url);

}

function CacheData(trimmed_query,data){

    sessionStorage.setItem(trimmed_query, data);
    console.log("Query result of '" + trimmed_query + "' is cached successfully")


}



/*
 * This statement binds the autocomplete library with the input box element and
 *   sets necessary parameters of the library.
 *
 * The library documentation can be find here:
 *   https://github.com/devbridge/jQuery-Autocomplete
 *   https://www.devbridge.com/sourcery/components/jquery-autocomplete/
 *
 */
// $('#autocomplete') is to find element by the ID "autocomplete"
$('#autocomplete').autocomplete({

    minChars: 3,

    // documentation of the lookup function can be found under the "Custom lookup function" section
    lookup: function (query, doneCallback) {
        handleLookup(query, doneCallback)
    },
    onSelect: function(suggestion) {
        handleSelectSuggestion(suggestion)
    },
    // set delay time
    deferRequestBy: 300,
    // there are some other parameters that you might want to use to satisfy all the requirements
    // TODO: add other parameters, such as minimum characters
});


/*
 * do normal full text search if no suggestion is selected
 */
function handleNormalSearch(query) {
    console.log("doing normal search with query: " + query);
    // TODO: you should do normal search here
    let url = "result.html?title=" + encodeURIComponent(query) + "&limit=10&offset=0&order=r_desc_t_asc";

    window.location.replace(url);
}

// bind pressing enter key to a handler function
$('#autocomplete').keypress(function(event) {
    // keyCode 13 is the enter key
    if (event.keyCode == 13) {
        // pass the value of the input box to the handler function
        handleNormalSearch($('#autocomplete').val())
    }
})

// TODO: if you have a "search" button, you may want to bind the onClick event as well of that button


