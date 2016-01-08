# Popular-Movies
This is the first assignment at the Udacity Android nanodegree.

## Features
### User Interface 
* Movies are displayed in the main layout via a grid of their corresponding movie poster thumbnails
* UI contains an element (i.e a spinner or settings menu) to toggle the sort order of the movies by: most popular, highest rated
* UI contains a screen for displaying the details for a selected movie
* Movie details layout contains title, release date, movie poster, vote average, and plot synopsis.

### User Interface - Function
* When a user changes the sort criteria (“most popular and highest rated”) the main view gets updated correctly.
* When a movie poster thumbnail is selected, the movie details screen is launched

### Network API Implementation 
* In a background thread, app queries the /discover/movies API with the query parameter for the sort criteria specified in the settings menu. (Note: Each sorting criteria is a different API call.)
This query can also be used to fetch the related metadata needed for the detail view.
* Movies api is fetched from [here](https://www.themoviedb.org/) 
* you have to get your own api and plug it in ApiKey.java here:
```public static final String API_KEY = "000";```
