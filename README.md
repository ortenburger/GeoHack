# GeoHack
sourcerepo fuer den GeoHack des Guten Lebens


### To run the backend:
  * cd bewegte_stadt
  * pip3 install -r requirements/local.txt 

### create postgressql db 'bewegte_stadt' with username/password
  * createdb bewegte_stadt -U <username> --password <password>
  * export DATABASE_URL=postgres://<username>:<password>@127.0.0.1:5432/bewegte_stadt

### Start the backend-server
  * python3 manage.py migrate
  * python3 manage.py runserver 0.0.0.0:8000
