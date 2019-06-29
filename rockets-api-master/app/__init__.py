import datetime
import logging
import os

from flask import Flask, current_app, send_file
from flask.logging import default_handler

from .api import api_bp
from .config import Config

app = Flask(__name__)
app.config.from_object('app.config.Config')

app.register_blueprint(api_bp)

app.logger.info('>>> {}'.format(Config.FLASK_ENV))

if __name__ == '__main__':
    app.run(debug=True, host="0.0.0.0", port=5000)
