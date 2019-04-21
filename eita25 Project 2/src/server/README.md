# Server
## Files
### Database 
#### Purpose
Handles all incoming requests and responds to these appropriately.
#### Endpoints
##### readMany
Returns a list of all available journal entries.
##### read
Returns one specific journal entry.
##### write
Edits a journal specificed by the user.

> write 1 10 edward pswd
##### login
???
### server
Initiates a socket and listens for requests. Once obtained, it will start a TSL connection and hand over control to the Database.
### CSVReader
Returns a list of String[] from a CSV file.
## Running
Run compile.sh. If successfull, run server.sh followed by client.sh.
## Testing
Perform the commands above and type the following in the client.sh window
> write 1 10 edward pswd

This will perform a write request to the server. If the syntax is correct (command id accesslvl name password) the user data will be updated and the server responds with a toString() of the resulting object.

> \>readmany
>sending 'readmany' to server...done
>received '1 10 Franz Lang password##2 5 Klon 1 password##3 4 Klon 2 password##4 3 Klon 3 password##5 2 Klon 4 password##6 1 Klon 5 password##' from server

> \>write 1 5 newfranz newpass 
>sending 'write 1 5 newfranz newpass' to server...done
>received '1 5 newfranz newpass' from server

> \>readmany
>sending 'readmany' to server...done
>received '1 5 newfranz newpass##2 5 Klon 1 password##3 4 Klon 2 password##4 3 Klon 3 password##5 2 Klon 4 password##6 1 Klon 5 password##' from servr
