let add_star = $("#add_star_form");
let add_movie = $("#add_movie_form");


function handleAddStar(Event) {
    console.log("adding star info");

    Event.preventDefault();

    $.ajax("api/addstar", {
        method: "POST",
        data: add_star.serialize(),
    });
}


function handleAddMovie(Event){
    console.log("adding movie info");

    Event.preventDefault();

    $.ajax("api/addmovie", {
        method: "POST",
        data: add_movie.serialize(),
    });
}




add_star.submit(handleAddStar);
add_movie.submit(handleAddMovie());
