DELIMITER //

CREATE PROCEDURE add_movie(IN mname VARCHAR(100), IN mdirector VARCHAR(100), IN myear INT, IN mgenre VARCHAR(32), IN mstar VARCHAR(100))


main:BEGIN
	DECLARE Found INT;

	DECLARE newMovieID VARCHAR(10);
    DECLARE newStarID VARCHAR(10);
    DECLARE newGenreID INT;
    

	SET Found =  (SELECT COUNT(*) FROM movies as m where m.title = mname and m.director = mdirector and m.year = myear);


	-- if the given movie exists in the movie table, stop the stored procedure and return the corresoinding message. 
	IF Found = 1 THEN  
		select Concat("Movie '", mname, "' already exists, No changes were made.") as "message";
        leave main;  -- leave the stored procedure
	END IF;
    
    -- otherwise, obtain an id for the new movie.
    SET newMovieID = (SELECT concat('tt', (SUBSTRING(MAX(id),3,7) + 1)) FROM movies);

	-- Insert the new movie to the movies table
	INSERT INTO movies(id, title, year, director) VALUES(newMovieID, mname, myear, mdirector);

	-- Reuse the Found variable to store Star count info
    SET Found = (SELECT COUNT(*) FROM stars WHERE name = mstar);
    
    -- if star does not exists in the stars table, add it.
    IF FOUND = 0 THEN
		SET newStarID = (SELECT concat('nm', (SUBSTRING(MAX(id),3,7) + 1)) FROM stars);
        INSERT INTO stars(id, name) VALUES(newStarID, mstar);
	ELSE
		-- Else, find the id of existing star
		SET newStarID = (SELECT id FROM stars WHERE name = mstar);
    END IF;
		
    -- Modify stars_in_movies table to link the movie with star
    INSERT INTO stars_in_movies(starId, movieId) VALUES(newStarID, newMovieID);
    
    -- Check if genre exits in the genres table
    SET Found = (SELECT COUNT(*) FROM genres WHERE name = mgenre);
    
    IF FOUND = 0 THEN
		INSERT into genres (name) values (mgenre);
	END IF;
	
    SET newGenreID = (SELECT id from genres where name = mgenre);
    

    -- Link movie with genre
    INSERT INTO genres_in_movies(genreId, movieId) VALUES(newGenreID, newMovieID);
	
    SELECT concat("New Movie ", mname, " has been added to the database. ", "Movie ID: ", newMovieID, ", Star ID: ", newStarID, ", Genre ID: ", newGenreID, ".") as "message";
END

//

DELIMITER ;

