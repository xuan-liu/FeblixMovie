let add_star = $("#add_star_form");
let add_movie = $("#add_movie_form");


function handleAddStar(Event) {
    console.log("adding star info");

    Event.preventDefault();

    $.ajax("api/addstar", {
        dataType: "json",
        method: "POST",
        data: add_star.serialize(),
        success: (resultMessage) => handleMessage(resultMessage)
    });

}


function handleAddMovie(Event){
    console.log("adding movie info");

    Event.preventDefault();

    $.ajax("api/addmovie", {
        dataType: "json",
        method: "POST",
        data: add_movie.serialize(),
        success: (resultMessage) => handleMessage(resultMessage)
    });
}


function handleMessage(resultMessage){
    console.log(resultMessage);
    let message_div = document.getElementById("result_message");
    if(message_div.childElementCount != 0){
        message_div.removeChild(message_div.childNodes[0]);
    }
    let txt = "<div class=\"alert alert-success\" role=\"alert\">" + resultMessage["message"] + "</div>";
    $("#result_message").append(txt);
}


function handleMetaData(resultData){
    let bodyString = "";
    for (let i = 0; i < resultData.length; i++){
        bodyString += "<tr>";
        bodyString += "<td>" + resultData[i]['name'] + "</td>";
        bodyString += "<td>";


        for (let c = 0; c < resultData[i]['columns'].length; c++){
            if (c != 0){
                bodyString += ", ";
            }
            bodyString += "<strong>" + resultData[i]['columns'][c]['name'] + "</strong>" + " : " + resultData[i]['columns'][c]['type'];
        }
        bodyString += "</td>";

        bodyString += "</tr>";
    }
    $("#metadata").append(bodyString);

}

$.ajax("api/metadata", {
    dataType: "json",
    method: "GET",
    success: (resultData) => handleMetaData(resultData)
});



add_star.submit(handleAddStar);
add_movie.submit(handleAddMovie);
