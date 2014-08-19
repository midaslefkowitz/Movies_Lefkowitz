/*

@Override
		public void bindView(View movieView, Context context, Cursor cursor) 
		{
			final Movie movie = new Movie(cursor);
			
			Holder holder = (Holder)movieView.getTag();
			holder.setMovie(movie);
			
			TextView movieTitleText = (TextView)movieView.findViewById(R.id.movieTitleText); 
			TextView movieDescriptionText = (TextView)movieView.findViewById(R.id.movieDescriptionText); 
			RatingBar rb = (RatingBar)movieView.findViewById(R.id.movieRatingBar);
			rb.setNumStars(5);
			rb.setMax(5);
			rb.setRating(2.4F);
			
			rb.setEnabled(false);
			
			movieTitleText.setText(movie.getTitle());
			movieDescriptionText.setText(movie.getDescription());
			
			movieView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
			    Toast.makeText(getApplicationContext(), "Short click", Toast.LENGTH_LONG).show();
			   
			    Intent editIntent = new Intent(MoviesActivity.this, EditMovieActivity.class);
			    editIntent.putExtra("isNew",false);
			    editIntent.putExtra("title", movie.getTitle());
			    editIntent.putExtra("description", movie.getDescription());
			    editIntent.putExtra("year", movie.getYear());
			    editIntent.putExtra("rating", movie.getRating());
			    editIntent.putExtra("rottenId", movie.getRottenId());
			    editIntent.putExtra("smallImage", movie.getSmallImage());
			    editIntent.putExtra("largeImage", movie.getLargeImage());
				startActivityForResult(editIntent,EDIT_MOVIE_REQUEST_CODE);
				}
			});
			
			movieView.setOnLongClickListener(new OnLongClickListener() {
				
				
				@Override
				public boolean onLongClick(final View v) {
								
					
			    Toast.makeText(getApplicationContext(), "Long click", Toast.LENGTH_LONG).show();
			    
			    AlertDialog.Builder editBuilder = new AlertDialog.Builder(MoviesActivity.this);
			    
			    editBuilder.setTitle("Edit or Clear Movie").setMessage("Please choose either to clear or to edit the movie");
			    
			    editBuilder.setPositiveButton("Edit", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

						Intent editIntent = new Intent(MoviesActivity.this, EditMovieActivity.class);
						 editIntent.putExtra("isNew",false);
						 editIntent.putExtra("title", movie.getTitle());
						 editIntent.putExtra("description", movie.getDescription());
						 editIntent.putExtra("year", movie.getYear());
						 editIntent.putExtra("rating", movie.getRating());
						 editIntent.putExtra("rottenId", movie.getRottenId());
						 editIntent.putExtra("smallImage", movie.getSmallImage());
						 editIntent.putExtra("largeImage", movie.getLargeImage());
						 startActivityForResult(editIntent,EDIT_MOVIE_REQUEST_CODE);
					}
					
		

				});
			    
			    editBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						
       			    return;
					}
				});
					
					

			    
			    Builder setNeutralButton = editBuilder.setNeutralButton("Clear movie", new DialogInterface.OnClickListener() {
					
				
					@Override
					public void onClick(DialogInterface dialog, int which) {
						

						
					   handler.deleteMovie(movie.getId());
     					moviesCursor = handler.getAllMoviesCursor();
						adapter.swapCursor(moviesCursor);
						adapter.notifyDataSetChanged();
					
					Toast.makeText(getApplicationContext(), "Movie deleted", Toast.LENGTH_LONG).show();					   
						
					}
				});	    	
			    	
			    editBuilder.show();
							    
			    return true;
				}
			});
		}

*/