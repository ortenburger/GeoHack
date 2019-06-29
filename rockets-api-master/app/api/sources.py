import glob
import json
from pathlib import Path

from flask import request
from flask_restplus import Namespace, Resource, reqparse

sources = Namespace('sources',
                    description='Manage open data sources.',
                    path='/sources')

@sources.route('/')
class Sources(Resource):
    """Provides a list of open data sources from the GeoPortal """

    def __init__(self, *args, **kwargs):
        Resource.__init__(self, *args, **kwargs)

    def get(self):
        """Returns a list of all resources available for download from the system
        in datahub.json format.

        """

        files = glob.iglob('data/**/datapackage.json', recursive=True)
        result = []
        for f in files:
            result.append(json.loads(Path(f).read_text()))

        return result
