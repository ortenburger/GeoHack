"""
Global Flask Application Setting

See `.flaskenv` for default settings.
 """

import os
import datetime

class Config(object):
    # If not set fall back to production for safety
    FLASK_ENV =  os.getenv('FLASK_ENV', 'production')
    FLASK_DEBUG = FLASK_ENV == 'production'

    # Set FLASK_SECRET on your production Environment
    SECRET_KEY = os.getenv('FLASK_SECRET', b'\xea\xec\xafuw\x1a_c\xe8\x02\x04\x85')

    APP_DIR = os.path.dirname(__file__)
    ROOT_DIR = os.path.dirname(APP_DIR)

    JSON_AS_ASCII=False
