# Rockets API and sample Frontend

The repository contains two parts - a sample API that can serve
datahub.io formatted data packages from the server and a small
front-end client that is the beginning of a fancy computing
package deployment environment. Both are only proof-of-concept
level.

## API

The API is based on python and uses Flask with Flask-restplus.
To launch it you first have to install `pipenv` then use
`pipenv install` and then `pipenv run python run.py`.
This will launch the API server on port 5000. 

## Frontend client

The frontend client is built using Quasar-framework which is
based on `Vue.js`. To install and run enter the project directory,
then first run `yarn install`, and after that is done, run
`quasar dev` for the dev server. This assumes that the API
client is running on port 5000 in a different terminal window.

For more details see the respective frameworks and packages.

