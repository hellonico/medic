Code	|	Text	|	Description
-----	|	----	|	---------
32		|	Could not authenticate you	|	Your call could not be completed as dialed.
34		|	Sorry, that page does not exist	|	Corresponds with an HTTP 404 - the specified resource was not found.
88		|	Rate limit exceeded		|	The request limit for this resource has been reached for the current rate limit window.
89		|	Invalid or expired token	|	The access token used in the request is incorrect or has expired. 
130		|	Over capacity	|	Corresponds with an HTTP 503 - StreamHub is temporarily over capacity.
131		|	Internal error	|	Corresponds with an HTTP 500 - An unknown internal error occurred.
135		|	Could not authenticate you	|	Corresponds with a HTTP 401 - it means that your oauth_timestamp is either ahead or behind our acceptable range
215		|	Bad authentication data		|	Typically sent with HTTP code 400. The method requires authentication but it was not presented or was wholly invalid. If you see and error response which is not listed in the above table, then fall back to the HTTP status code in order to determine the best way to address the error.